package me.adamix.mekanism.bfs;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record BfsResult(
        boolean found,
        @NotNull Set<Location> visited
) {
}
