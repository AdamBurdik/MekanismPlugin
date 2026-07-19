package me.adamix.utils;

import me.adamix.mekanism.type.Tuple;
import me.adamix.mekanism.type.WorldPos;
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
            Block neighbor = block.getRelative(face);
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    public static @NotNull List<WorldPos> getSurroundingLocations(@NotNull WorldPos location) {
        List<WorldPos> neighbors = new ArrayList<>();

        for (BlockFace face : CARDINAL_DIRECTIONS) {
            WorldPos neighbor = location.offset(face.getModX(), face.getModY(), face.getModZ());
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    public static @NotNull List<Tuple<WorldPos, BlockFace>> getSurroundings(@NotNull WorldPos pos) {
        List<Tuple<WorldPos, BlockFace>> neighbors = new ArrayList<>();

        for (BlockFace face : CARDINAL_DIRECTIONS) {
            WorldPos neighbor = pos.offset(face.getModX(), face.getModY(), face.getModZ());
            neighbors.add(new Tuple<>(neighbor, face));
        }

        return neighbors;
    }
}