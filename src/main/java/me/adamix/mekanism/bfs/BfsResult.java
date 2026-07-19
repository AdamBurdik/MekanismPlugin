package me.adamix.mekanism.bfs;

import me.adamix.mekanism.type.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record BfsResult(
        boolean found,
        @NotNull Set<BlockPos> visited
) {
}
