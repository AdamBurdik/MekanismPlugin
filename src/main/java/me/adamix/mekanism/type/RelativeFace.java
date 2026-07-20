package me.adamix.mekanism.type;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

public enum RelativeFace {
    FRONT, BACK, LEFT, RIGHT, TOP, BOTTOM;

    public @NotNull BlockFace toWorldFace(@NotNull BlockFace facing) {
        return switch (this) {
            case FRONT -> facing;
            case BACK -> facing.getOppositeFace();
            case LEFT -> rotateClockwise(facing, 3); // 3 steps CW = 1 step CCW
            case RIGHT -> rotateClockwise(facing, 1);
            case TOP -> BlockFace.UP;
            case BOTTOM -> BlockFace.DOWN;
        };
    }

    private static @NotNull BlockFace rotateClockwise(@NotNull BlockFace face, int steps) {
        BlockFace[] horizontals = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        for (int i = 0; i < horizontals.length; i++) {
            if (horizontals[i] == face) {
                return horizontals[(i + steps) % 4];
            }
        }
        return face;
    }

    public static @NotNull RelativeFace fromWorldFace(@NotNull BlockFace face, @NotNull BlockFace blockFacing) {
        if (face == BlockFace.UP) return RelativeFace.TOP;
        if (face == BlockFace.DOWN) return RelativeFace.BOTTOM;
        if (face == blockFacing) return RelativeFace.FRONT;
        if (face == blockFacing.getOppositeFace()) return RelativeFace.BACK;

        // Check if face is to the right of facing
        if (face == RelativeFace.RIGHT.toWorldFace(blockFacing)) {
            return RelativeFace.RIGHT;
        }

        return RelativeFace.LEFT;
    }
}