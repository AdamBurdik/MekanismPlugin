package me.adamix.mekanism.network;

import lombok.Getter;
import me.adamix.mekanism.transporter.TransportTier;
import me.adamix.mekanism.transporter.TransporterType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static me.adamix.utils.BlockUtils.CARDINAL_DIRECTIONS;

@Getter
public abstract class AbstractNetwork {
    protected final UUID id;
    protected final Set<Location> transporters = new HashSet<>();
    protected final Set<NetworkConsumer> consumers = new HashSet<>();

    public AbstractNetwork(@NotNull UUID id) {
        this.id = id;
    }

    public abstract @NotNull NetworkType type();

    public void addTransporter(
            @NotNull Block block
    ) {
        transporters.add(block.getLocation());
    }

    public void addConsumer(
            @NotNull NetworkConsumer consumer
    ) {
        consumers.add(consumer);
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
