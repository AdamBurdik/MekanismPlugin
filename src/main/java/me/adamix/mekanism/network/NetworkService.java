package me.adamix.mekanism.network;

import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.block.component.network.TransporterComponent;
import me.adamix.mekanism.network.port.NetworkPort;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.type.BlockPos;
import me.adamix.mekanism.type.Tuple;
import me.adamix.mekanism.type.WorldPos;
import me.adamix.utils.BlockUtils;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import static me.adamix.utils.BlockUtils.CARDINAL_DIRECTIONS;
import static me.adamix.utils.Utils.todo;

@RequiredArgsConstructor
public class NetworkService {
    private final Logger log;
    private final Map<UUID, AbstractNetwork> networksById = new HashMap<>();
    private final Map<WorldPos, UUID> transporterToId = new HashMap<>();
    private final Map<WorldPos, Map<BlockFace, NetworkPort>> portsOf = new HashMap<>();

    public @NotNull Optional<AbstractNetwork> getNetwork(@NotNull WorldPos pos, @NotNull BlockFace face) {
        UUID networkId = null;
        if (transporterToId.containsKey(pos)) {
            networkId = transporterToId.get(pos);
        }
        if (portsOf.containsKey(pos)) {
            var ports = portsOf.get(pos)
                    .get(face);
            if (ports != null) {
                networkId = ports.getNetworkId();
            }
        }

        if (networkId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(networksById.get(networkId));
    }

    public @NotNull AbstractNetwork createNetwork(@NotNull NetworkType type, @NotNull String worldName) {
        UUID id = UUID.randomUUID();
        AbstractNetwork network = switch (type) {
            case ENERGY -> new EnergyNetwork(id, worldName);
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
        networkA.getTransporters().addAll(networkB.getTransporters());
        networkA.getConsumers().addAll(networkB.getConsumers());
        networkA.getProducers().addAll(networkB.getProducers());
        for (BlockPos cable : networkB.getTransporters()) {
            transporterToId.put(cable.withWorld(networkA.getWorldName()), networkA.getId());
        }

        for (NetworkPort consumer : networkB.getConsumers()) {
            consumer.setNetworkId(networkA.getId());
        }
        for (NetworkPort producer : networkB.producers) {
            producer.setNetworkId(networkA.getId());
        }

        networksById.remove(networkB.getId());

        log.info("Merged networks: {} and {}", networkA.getId(), networkB.getId());
    }

    public @NotNull NetworkContext scanSurroundings(@NotNull WorldPos pos) {
        Map<BlockFace, AbstractNetwork> map = new HashMap<>();

        for (Tuple<WorldPos, BlockFace> tuple : BlockUtils.getSurroundings(pos)) {
            WorldPos surrounding = tuple.left();
            BlockFace face = tuple.right();

            UUID networkId = null;

            if (transporterToId.containsKey(surrounding)) {
                networkId = transporterToId.get(surrounding);
            } else if (portsOf.containsKey(surrounding)) {
                var ports = portsOf.get(surrounding);

                if (ports.containsKey(face.getOppositeFace())) {
                    networkId = ports.get(face.getOppositeFace())
                        .getNetworkId();
                }
            }

            if (networkId == null) continue;

            AbstractNetwork network = networksById.get(networkId);
            if (network == null) {
                log.error("Surrounding block has network id that does not exist in network map. Maybe zombie block?");
                continue;
            }

            map.put(tuple.right(), network);
        }

        return new NetworkContext(
                map
        );
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

            registerPorts(block, type, NetworkType.ENERGY, instance, component.getPorts());
        }
    }

    private void registerPorts(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull NetworkType networkType,
            @NotNull BlockInstance instance,
            @NotNull Map<BlockFace, PortType> ports
    ) {
        WorldPos pos = WorldPos.of(block);

        NetworkContext networkContext = scanSurroundings(pos);
        Map<BlockFace, AbstractNetwork> map = networkContext.networkMap();

        Map<BlockFace, NetworkPort> faceToId = new HashMap<>();
        Set<BlockFace> connectedFaces = new HashSet<>();

        map.forEach((face, network) -> {
            connectedFaces.add(face);
            PortType portType = ports.get(face);
            if (portType == PortType.DISABLED) return;

            var port = new NetworkPort(
                    pos.block(),
                    block.getWorld().getName(),
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

            faceToId.put(face, port);
        });

        ports.forEach((face, portType) -> {
            if (connectedFaces.contains(face)) return;
            if (portType == PortType.DISABLED) return;

            AbstractNetwork network = createNetwork(networkType, block.getWorld().getName());

            var port = new NetworkPort(
                    pos.block(),
                    block.getWorld().getName(),
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

            faceToId.put(face, port);
        });

        portsOf.put(pos, faceToId);
    }

    private void registerTransporter(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockInstance instance,
            @NotNull TransporterComponent component
    ) {
        WorldPos pos = WorldPos.of(block);

        NetworkContext networkContext = scanSurroundings(pos);
        Map<BlockFace, AbstractNetwork> map = networkContext.networkMap();
        var surroundingNetworks = new HashSet<>(map.values());

        if (surroundingNetworks.isEmpty()) {
            // Create new network
            AbstractNetwork network = createNetwork(component.type(), block.getWorld().getName());
            network.addTransporter(block);
            transporterToId.put(pos, network.getId());
        } else if (surroundingNetworks.size() == 1) {
            // Join this network
            var network = map.values()
                    .stream()
                    .findFirst()
                    .orElseThrow();

            network.addTransporter(block);
            transporterToId.put(pos, network.getId());
            log.info("Added cable to network: {}", network.id);
        } else {
            // Merge networks
            var network = map.values()
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
    }

    public void updateBlock(@NotNull Block location) {
        todo();
    }

    public void tick() {
        for (AbstractNetwork network : networksById.values()) {
            if (network instanceof EnergyNetwork energyNetwork) {
                energyNetwork.tick();
            }
            // if (network instanceof ItemNetwork itemNetwork) { tickItemNetwork(itemNetwork); }
        }
    }
}
