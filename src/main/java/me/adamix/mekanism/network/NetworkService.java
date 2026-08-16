package me.adamix.mekanism.network;

import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.component.item.ItemComponent;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.block.component.network.TransporterComponent;
import me.adamix.mekanism.block.instance.BlockInstanceService;
import me.adamix.mekanism.block.persistence.BlockPersistenceService;
import me.adamix.mekanism.block.source.ComponentSource;
import me.adamix.mekanism.block.source.VanillaContainerSource;
import me.adamix.mekanism.network.port.NetworkPort;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.type.BlockPos;
import me.adamix.mekanism.type.Tuple;
import me.adamix.mekanism.type.WorldPos;
import me.adamix.utils.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.adamix.utils.BlockUtils.CARDINAL_DIRECTIONS;
import static me.adamix.utils.Utils.todo;

@RequiredArgsConstructor
public class NetworkService {
    private final Logger log;
    private final Map<UUID, AbstractNetwork> networksById = new ConcurrentHashMap<>();
    private final Map<WorldPos, UUID> transporterToId = new ConcurrentHashMap<>();
    private final Map<WorldPos, Map<BlockFace, Set<NetworkPort>>> portsOf = new ConcurrentHashMap<>();
    private final Map<WorldPos, Map<BlockFace, UUID>> externalPorts = new ConcurrentHashMap<>();
    private final BlockPersistenceService blockPersistenceService;
    private final BlockInstanceService instanceService;

    public @NotNull List<AbstractNetwork> getNetworks(@NotNull WorldPos pos, @NotNull BlockFace face) {
        List<UUID> networkIds = new ArrayList<>(1);
        if (transporterToId.containsKey(pos)) {
            networkIds.add(transporterToId.get(pos));
        }
        if (portsOf.containsKey(pos)) {
            var ports = portsOf.get(pos)
                    .get(face);
            if (ports != null) {
                ports.stream()
                        .map(NetworkPort::getNetworkId)
                        .forEach(networkIds::add);
            }
        }
        if (externalPorts.containsKey(pos)) {
            networkIds.add(externalPorts.get(pos)
                    .get(face));
        }

        return networkIds.stream()
                .map(networksById::get)
                .toList();
    }

    public @NotNull AbstractNetwork createNetwork(@NotNull NetworkType type, @NotNull String worldName) {
        UUID id = UUID.randomUUID();
        AbstractNetwork network = switch (type) {
            case ENERGY -> new EnergyNetwork(id, worldName);
            case ITEM -> new ItemNetwork(id, worldName);
        };

        networksById.put(id, network);
        log.info("New energy network created: {}", id);

        return network;
    }

    @NotNull Set<BlockPos> bfs(
            @NotNull Set<BlockPos> nodes,
            @NotNull BlockPos starting
    ) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        visited.add(starting);
        queue.add(starting);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (BlockPos surrounding : getSurrounding(nodes, current)) {
                if (!visited.contains(surrounding)) {
                    visited.add(surrounding);
                    queue.add(surrounding);
                }
            }
        }

        return visited;
    }

    @NotNull Set<BlockPos> getSurrounding(
            @NotNull Set<BlockPos> nodes,
            @NotNull BlockPos pos
    ) {
        Set<BlockPos> neighbors = new HashSet<>();

        for (BlockFace face : CARDINAL_DIRECTIONS) {
            BlockPos neighbor = pos.offset(face.getModX(), face.getModY(), face.getModZ());

            if (nodes.contains(neighbor)) neighbors.add(neighbor);
        }

        return neighbors;
    }

    private void mergeNetworks(
            @NotNull AbstractNetwork networkA,
            @NotNull AbstractNetwork networkB
    ) {
        networkA.getConsumers().putAll(networkB.getConsumers());
        networkA.getProducers().putAll(networkB.getProducers());
        for (BlockPos cable : networkB.getTransporters()) {
            transporterToId.put(cable.withWorld(networkA.getWorldName()), networkA.getId());
        }

        for (NetworkPort consumer : networkB.getConsumers().values()) {
            consumer.setNetworkId(networkA.getId());
        }
        for (NetworkPort producer : networkB.getProducers().values()) {
            producer.setNetworkId(networkA.getId());
        }

        networksById.remove(networkB.getId());

        networkA.update();
        log.info("Merged networks: {} and {}", networkA.getId(), networkB.getId());
    }

    public @NotNull NetworkContext scanSurroundings(@NotNull WorldPos pos) {
        return scanSurroundings(pos, null);
    }

    public @NotNull NetworkContext scanSurroundings(@NotNull WorldPos pos, @Nullable NetworkType type) {
        Map<BlockFace, List<AbstractNetwork>> map = new HashMap<>();

        for (Tuple<WorldPos, BlockFace> tuple : BlockUtils.getSurroundings(pos)) {
            WorldPos surrounding = tuple.left();
            BlockFace face = tuple.right();

            List<UUID> networkIds = new ArrayList<>(0);

            if (transporterToId.containsKey(surrounding)) {
                networkIds.add(transporterToId.get(surrounding));
            } else if (portsOf.containsKey(surrounding)) {
                var ports = portsOf.get(surrounding);

                if (ports.containsKey(face.getOppositeFace())) {
                    ports.get(face.getOppositeFace()).stream()
                            .map(NetworkPort::getNetworkId)
                            .forEach(networkIds::add);
                }
            } else if (externalPorts.containsKey(surrounding)) {
                var ports = externalPorts.get(surrounding);
                networkIds.add(ports.get(face.getOppositeFace()));
            }
            if (networkIds.isEmpty()) continue;

            for (UUID networkId : networkIds) {
                AbstractNetwork network = networksById.get(networkId);
                if (network == null) {
                    log.error("Surrounding block has network id that does not exist in network map. Maybe zombie block?");
                    continue;
                }

                if (type != null && network.type() != type) continue;

                map.computeIfAbsent(tuple.right(), _ -> new ArrayList<>(1))
                        .add(network);
            }
        }

        return new NetworkContext(map);
    }

    public void registerBlock(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockInstance instance
    ) {
        if (instance.has(TransporterComponent.class)) {
            var component = instance.get(TransporterComponent.class)
                    .orElseThrow();

            registerTransporter(block, type, instance, component);
        }
        if (instance.has(EnergyComponent.class)) {
            var component = instance.get(EnergyComponent.class)
                    .orElseThrow();

            registerPorts(WorldPos.of(block), NetworkType.ENERGY, instance, component.getPorts());
        }
        if (instance.has(ItemComponent.class)) {
            var component = instance.get(ItemComponent.class)
                    .orElseThrow();

            registerPorts(WorldPos.of(block), NetworkType.ITEM, instance, component.getPorts());
        }
    }

    public void unregisterBlock(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockInstance instance
    ) {
        todo();
    }

    public void unregisterPort(
            @NotNull WorldPos pos,
            @NotNull BlockFace face
    ) {
        Set<NetworkPort> ports = portsOf.get(pos).get(face);
        if (ports == null) return;

        for (NetworkPort port : ports) {
            UUID networkId = port.getNetworkId();

            AbstractNetwork network = networksById.get(networkId);
            if (network == null) {
                // TODO Handle somehow idk
                return;
            }

            if (port.getPortType() == PortType.INPUT) {
                network.removeConsumer(port);
            } else if (port.getPortType() == PortType.OUTPUT) {
                network.removeProducer(port);
            }

            if (network.isEmpty()) {
                networksById.remove(networkId);
            }

            portsOf.get(pos).remove(face);
        }
    }

    public void registerPorts(
            @NotNull WorldPos pos,
            @NotNull NetworkType networkType,
            @NotNull BlockInstance instance,
            @NotNull Map<BlockFace, PortType> ports
    ) {
        NetworkContext networkContext = scanSurroundings(pos);
        Map<BlockFace, AbstractNetwork> map = networkContext.filter(networkType);

        Map<BlockFace, NetworkPort> faceToId = new HashMap<>();
        Set<BlockFace> connectedFaces = new HashSet<>();

        map.forEach((face, network) -> {
            connectedFaces.add(face);
            PortType portType = ports.get(face);
            if (portType == PortType.DISABLED) return;

            var port = new NetworkPort(
                    pos.block(),
                    pos.worldName(),
                    face,
                    portType,
                    instance,
                    network.getId()
            );

            if (portType == PortType.INPUT) {
                network.addConsumer(port);
            } else if (portType == PortType.OUTPUT) {
                network.addProducer(port);
            }
            if (portType == PortType.BOTH) {
                network.addConsumer(port);
                network.addProducer(port);
            }

            faceToId.put(face, port);
        });

        ports.forEach((face, portType) -> {
            if (connectedFaces.contains(face)) return;
            if (portType == PortType.DISABLED) return;

            AbstractNetwork network = createNetwork(networkType, pos.worldName());

            var port = new NetworkPort(
                    pos.block(),
                    pos.worldName(),
                    face,
                    portType,
                    instance,
                    network.getId()
            );

            if (portType == PortType.INPUT) {
                network.addConsumer(port);
            }
            if (portType == PortType.OUTPUT) {
                network.addProducer(port);
            }
            if (portType == PortType.BOTH) {
                network.addConsumer(port);
                network.addProducer(port);
            }

            faceToId.put(face, port);
        });

        var registryPorts = portsOf.computeIfAbsent(pos, _ -> new HashMap<>());

        faceToId.forEach((face, port) -> {
//            if (registryPorts.containsKey(face)) {
//                throw new IllegalStateException("Trying to register port for block with already registered port for " + face);
//            }
            registryPorts.computeIfAbsent(face, _ -> new HashSet<>())
                            .add(port);
        });
    }

    private void registerTransporter(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockInstance instance,
            @NotNull TransporterComponent component
    ) {
        WorldPos pos = WorldPos.of(block);

        NetworkContext networkContext = scanSurroundings(pos, component.type());
        Map<BlockFace, AbstractNetwork> map = networkContext.filter(component.type());
        var surroundingNetworks = new HashSet<>(map.values());

        AbstractNetwork network;

        if (surroundingNetworks.isEmpty()) {
            // Create new network
            network = createNetwork(component.type(), block.getWorld().getName());
            network.addTransporter(block);
            transporterToId.put(pos, network.getId());
        } else if (surroundingNetworks.size() == 1) {
            // Join this network
            network = map.values()
                    .stream()
                    .findFirst()
                    .orElseThrow();

            network.addTransporter(block);
            transporterToId.put(pos, network.getId());
            log.info("Added cable to network: {}", network.id);
        } else {
            // Merge networks
            network = map.values()
                    .stream()
                    .findFirst()
                    .orElseThrow();

            surroundingNetworks.remove(network);
            for (AbstractNetwork otherNetwork : surroundingNetworks) {
                mergeNetworks(network, otherNetwork);
            }
            network.addTransporter(block);
            transporterToId.put(pos, network.getId());
        }

        // Register external ports
        for (Tuple<WorldPos, BlockFace> surrounding : BlockUtils.getSurroundings(pos)) {
            WorldPos surroundingPos = surrounding.left();
            BlockFace face = surrounding.right();

            if (transporterToId.containsKey(surroundingPos)) continue;

            // Only register externals for specific blocks.
            // TODO Some better way to register them, not by this check
            if (type == MekanismBlockType.BASIC_LOGISTICAL_TRANSPORTER) {
                Optional<ComponentSource> source = resolveSource(surroundingPos, NetworkType.ITEM);
                source.ifPresent(src -> {
                    // Not sure if sharing 1 port for producer and consumer is good idea.
                    NetworkPort port = new NetworkPort(
                            surroundingPos.block(), surroundingPos.worldName(),
                            face.getOppositeFace(), PortType.BOTH, src, network.getId()
                    );
                    network.addConsumer(port);
                    network.addProducer(port);
                    externalPorts.computeIfAbsent(surroundingPos, _ -> new HashMap<>())
                            .put(face.getOppositeFace(), network.getId());
                });
            }
        }
    }

    private @NotNull Optional<ComponentSource> resolveSource(@NotNull WorldPos pos, @NotNull NetworkType type) {
        Optional<BlockInstance> own = instanceService.get(pos);
        if (own.isPresent()) {
            return Optional.of(own.get());
        }

        if (type == NetworkType.ITEM) {
            Block block = pos.resolveBlock();
            if (block.getState() instanceof Container container) {
                return Optional.of(new VanillaContainerSource(container.getInventory()));
            }
        }

        return Optional.empty();
    }

    public void updateBlock(@NotNull Block location) {
        todo();
    }

    public void tick() {
        for (AbstractNetwork network : networksById.values()) {
            var touched = network.tick();
            if (touched != null) {
                touched.forEach(blockPersistenceService::markDirty);
            }
            // if (network instanceof ItemNetwork itemNetwork) { tickItemNetwork(itemNetwork); }
        }
    }
}
