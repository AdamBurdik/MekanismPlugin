package me.adamix.mekanism.type;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public record BlockPos(
        int x, int y, int z
) {
    public @NotNull BlockPos offset(int x, int y, int z) {
        return new BlockPos(this.x + x, this.y + y, this.z + z);
    }

    public @NotNull WorldPos withWorld(@NotNull World world) {
        return new WorldPos(world.getName(), this);
    }

        public @NotNull WorldPos withWorld(@NotNull String worldName) {
        return new WorldPos(worldName, this);
    }


    public static @NotNull BlockPos of(@NotNull Block block) {
        return new BlockPos(
                block.getX(),
                block.getY(),
                block.getZ()
        );
    }

    public static @NotNull BlockPos of(@NotNull Location location) {
        return new BlockPos(
                location.blockX(),
                location.blockY(),
                location.blockZ()
        );
    }
}
