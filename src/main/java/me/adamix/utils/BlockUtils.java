package me.adamix.utils;

import me.adamix.mekanism.type.Tuple;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {

    public static final BlockFace[] CARDINAL_DIRECTIONS = {
            BlockFace.UP, BlockFace.DOWN,
            BlockFace.WEST, BlockFace.EAST,
            BlockFace.NORTH, BlockFace.SOUTH,
    };

    public static @NotNull List<Block> getSurroundingBlocks(@NotNull Block block) {
        List<Block> neighbors = new ArrayList<>();

        for (BlockFace face : CARDINAL_DIRECTIONS) {
            Location location;
            Block neighbor = block.getRelative(face);
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    public static @NotNull List<Location> getSurroundingLocations(@NotNull Location location) {
        List<Location> neighbors = new ArrayList<>();

        for (BlockFace face : CARDINAL_DIRECTIONS) {
            Location neighbor = location.clone().add(face.getModX(), face.getModY(), face.getModZ());
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    public static @NotNull List<Tuple<Location, BlockFace>> getSurroundings(@NotNull Location location) {
        List<Tuple<Location, BlockFace>> neighbors = new ArrayList<>();

        for (BlockFace face : CARDINAL_DIRECTIONS) {
            Location neighbor = location.clone().add(face.getModX(), face.getModY(), face.getModZ());
            neighbors.add(new Tuple<>(neighbor, face));
        }

        return neighbors;
    }
}