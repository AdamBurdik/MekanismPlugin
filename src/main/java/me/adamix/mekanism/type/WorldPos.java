package me.adamix.mekanism.type;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public record WorldPos(@NotNull World world, @NotNull BlockPos block) {
    public @NotNull WorldPos offset(int x, int y, int z) {
        return new WorldPos(world,
                new BlockPos(
                        this.block.x() + x,
                        this.block.y() + y,
                        this.block.z() + z
                )
        );
    }

    public static @NotNull WorldPos of(@NotNull Block block) {
        return new WorldPos(
                block.getWorld(),
                BlockPos.of(block)
        );
    }
}
