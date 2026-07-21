package me.adamix.mekanism.block.persistence;

import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.Mekanism;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.Chunk;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class ChunkPersistenceService {
    private final Mekanism plugin;

    public void save(@NotNull WorldPos pos, @NotNull MekanismBlockType type) {
        Chunk chunk = pos.resolveBlock().getChunk();
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();


    }
}
