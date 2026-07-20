package me.adamix.mekanism.type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public record WorldPos(@NotNull String worldName, @NotNull BlockPos block) {
    public @NotNull WorldPos offset(int x, int y, int z) {
        return new WorldPos(worldName,
                new BlockPos(
                        this.block.x() + x,
                        this.block.y() + y,
                        this.block.z() + z
                )
        );
    }

    public Block resolveBlock() {
        World world = resolveWorld();
        return world.getBlockAt(block.x(), block.y(), block.z());
    }

    public @NotNull World resolveWorld() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) throw new IllegalStateException("Cant find world: " + worldName);
        return world;
    }

    public static @NotNull WorldPos of(@NotNull Block block) {
        return new WorldPos(
                block.getWorld().getName(),
                BlockPos.of(block)
        );
    }

    public static @NotNull WorldPos of(@NotNull Location location) {
        return new WorldPos(
                location.getWorld().getName(),
                BlockPos.of(location)
        );
    }
}
