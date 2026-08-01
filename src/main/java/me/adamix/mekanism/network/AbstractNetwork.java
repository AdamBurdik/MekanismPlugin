package me.adamix.mekanism.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.network.port.NetworkPort;
import me.adamix.mekanism.type.BlockPos;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static me.adamix.utils.BlockUtils.CARDINAL_DIRECTIONS;

@Getter
@RequiredArgsConstructor
public abstract class AbstractNetwork {
    protected final @NotNull UUID id;
    protected final @NotNull String worldName;
    protected final Set<BlockPos> transporters = new HashSet<>();
    protected final Map<BlockPos, NetworkPort> consumers = new HashMap<>();
    protected final Map<BlockPos, NetworkPort> producers = new HashMap<>();

    public abstract @NotNull NetworkType type();

    public boolean isEmpty() {
        return consumers.isEmpty() && producers.isEmpty();
    }

    public @Nullable Set<WorldPos> tick() {
        return null;
    }

    public void addTransporter(
            @NotNull Block block
    ) {
        transporters.add(BlockPos.of(block));
    }

    public void addConsumer(
            @NotNull NetworkPort consumer
    ) {
        consumers.put(consumer.getPos(), consumer);
    }

    public void removeConsumer(
            @NotNull NetworkPort consumer
    ) {
        consumers.remove(consumer.getPos());
    }

    public void addProducer(
            @NotNull NetworkPort producer
    ) {
        producers.put(producer.getPos(), producer);
    }

    public void removeProducer(
            @NotNull NetworkPort producer
    ) {
        producers.remove(producer.getPos());
    }

    public void removeTransporter(@NotNull Block block) {
        transporters.remove(block.getLocation());
    }

    public void update() {

    }

    public @NotNull Set<BlockPos> getSurrounding(@NotNull BlockPos pos) {
        Set<BlockPos> neighbors = new HashSet<>();

        for (BlockFace face : CARDINAL_DIRECTIONS) {
            BlockPos neighbor = pos.offset(
                    face.getModX(),
                    face.getModY(),
                    face.getModZ()
            );
            if (
                    consumers.containsKey(neighbor) ||
                            producers.containsKey(neighbor) ||
                            transporters.contains(neighbor)
            ) {
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    public boolean isNetworkPort(@NotNull BlockPos pos) {
        return consumers.containsKey(pos) || producers.containsKey(pos);
    }

    public @Nullable NetworkPort getNetworkPortAt(@NotNull BlockPos pos) {
        if (consumers.containsKey(pos)) return consumers.get(pos);
        if (producers.containsKey(pos)) return producers.get(pos);
        return null;
    }

    @Deprecated
    public @NotNull Set<BlockFace> getSurroundingFaces(@NotNull Location location) {
        Set<BlockFace> neighbors = new HashSet<>();

        for (BlockFace face : CARDINAL_DIRECTIONS) {
            Location neighbor = location.clone()
                    .add(face.getModX(), face.getModY(), face.getModZ());
            if (transporters.contains(neighbor)) neighbors.add(face);
        }

        return neighbors;
    }

    @Deprecated
    public @NotNull Set<Location> getSurrounding(@NotNull Location location) {
        Set<Location> neighbors = new HashSet<>();

        for (BlockFace face : CARDINAL_DIRECTIONS) {
            Location neighbor = location.clone()
                    .add(face.getModX(), face.getModY(), face.getModZ());
            if (transporters.contains(neighbor)) neighbors.add(neighbor);
        }

        return neighbors;
    }
}
