package me.adamix.mekanism.type;

import org.bukkit.Chunk;
import org.jetbrains.annotations.NotNull;

public record ChunkPos(int x, int z) {
    public static @NotNull ChunkPos of(@NotNull Chunk chunk) {
        return new ChunkPos(chunk.getX(), chunk.getZ());
    }

    public static @NotNull ChunkPos of(@NotNull WorldPos pos) {
        return new ChunkPos(
                pos.block().x() >> 4,
                pos.block().z() >> 4
        );
    }
}
