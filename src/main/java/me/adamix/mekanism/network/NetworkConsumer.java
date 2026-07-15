package me.adamix.mekanism.network;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

public record NetworkConsumer(
        @NotNull Location location,
        @NotNull BlockFace face
) {
}
