package me.adamix.mekanism.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.network.port.NetworkPort;
import me.adamix.mekanism.type.BlockPos;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.adamix.utils.BlockUtils.CARDINAL_DIRECTIONS;

@Getter
@RequiredArgsConstructor
public abstract class AbstractNetwork {
    protected final @NotNull UUID id;
    protected final @NotNull World world;
    protected final Set<BlockPos> transporters = new HashSet<>();
    protected final Set<NetworkPort> consumers = new HashSet<>();
    protected final Set<NetworkPort> producers = new HashSet<>();

    public abstract @NotNull NetworkType type();

    public void tick() {

    }

    public void addTransporter(
            @NotNull Block block
    ) {
        transporters.add(BlockPos.of(block));
    }

    public void addConsumer(
            @NotNull NetworkPort consumer
    ) {
        consumers.add(consumer);
    }

    public void addProducer(
            @NotNull NetworkPort producer
    ) {
        producers.add(producer);
    }

    public void removeTransporter(@NotNull Block block) {
        transporters.remove(block.getLocation());
    }

    public @NotNull Set<BlockFace> getSurroundingFaces(@NotNull Location location) {
        Set<BlockFace> neighbors = new HashSet<>();

        for (BlockFace face : CARDINAL_DIRECTIONS) {
            Location neighbor = location.clone()
                    .add(face.getModX(), face.getModY(), face.getModZ());
            if (transporters.contains(neighbor)) neighbors.add(face);
        }

        return neighbors;
    }

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
