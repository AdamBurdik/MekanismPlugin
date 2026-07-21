package me.adamix.mekanism.block;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.adamix.mekanism.block.persistence.BlockPersistenceService;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.data.MekanismKeys;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.type.ChunkPos;
import me.adamix.mekanism.type.StoredBlock;
import me.adamix.mekanism.type.WorldPos;
import me.adamix.mekanism.type.pdc.StoredBlockListDataType;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.adamix.utils.Utils.todo;

@Slf4j
@RequiredArgsConstructor
public class BlockService {
    private final BlockRegistry blockRegistry;
    private final BlockPersistenceService blockPersistenceService;

    private final Map<WorldPos, MekanismBlockType> locationToType = new HashMap<>();
    private final Map<WorldPos, ItemDisplay> locationToEntity = new HashMap<>();
    private final Map<ChunkPos, List<WorldPos>> blocksByChunk = new HashMap<>();

    public void placeBlock(
            @NotNull Block block,
            @NotNull MekanismBlockType type
    ) {
        BlockDefinition definition = blockRegistry.getOrThrow(type);

        block.setType(definition.base());
        if (definition.baseData() != null) {
            block.setBlockData(definition.baseData());
        }

        WorldPos pos = WorldPos.of(block);
        locationToType.put(pos, type);

        ChunkPos chunkPos = ChunkPos.of(pos);
        blocksByChunk.computeIfAbsent(chunkPos, _ -> new ArrayList<>())
                .add(pos);
        blockPersistenceService.markDirty(pos);
    }

    public void breakBlock(
            @NotNull Block block
    ) {
        WorldPos pos = WorldPos.of(block);

        todo();

        locationToType.remove(pos);
        locationToEntity.remove(pos);

        ChunkPos chunkPos = ChunkPos.of(pos);
        blocksByChunk.computeIfAbsent(chunkPos, _ -> new ArrayList<>())
                .remove(pos);
        blockPersistenceService.markDirty(pos);
        saveToChunkPdc(pos);
    }

    public void spawnEntity(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull NetworkContext networkContext,
            @NotNull BlockInstance instance
    ) {
        BlockDefinition definition = blockRegistry.getOrThrow(type);

        var entity = definition.handler().spawnEntity(
                block,
                type,
                definition,
                networkContext,
                instance
        );
        locationToEntity.put(WorldPos.of(block), entity);
    }

    public boolean isMekanismBlock(@NotNull Block block) {
        return locationToType.containsKey(WorldPos.of(block));
    }

    public @NotNull Optional<MekanismBlockType> getType(@NotNull Block block) {
        return Optional.ofNullable(locationToType.get(WorldPos.of(block)));
    }

    public void updateBlock(
            @NotNull Block block,
            @NotNull NetworkContext networkContext,
            @NotNull BlockInstance instance
    ) {
        WorldPos pos = WorldPos.of(block);

        MekanismBlockType type = locationToType.get(pos);
        if (type == null) return;

        ItemDisplay itemDisplay = locationToEntity.get(pos);
        if (itemDisplay == null) {
            throw new IllegalStateException("Block does not contain an entity: " + pos);
        }

        BlockDefinition reg = blockRegistry.getOrThrow(type);

        reg.handler().updateBlock(
                block,
                type,
                itemDisplay,
                reg,
                networkContext,
                instance
        );

        blockPersistenceService.markDirty(pos);
    }

    public void saveToChunkPdc(WorldPos pos) {
        long start = System.currentTimeMillis();
        Chunk chunk = pos.resolveBlock().getChunk();
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();

        ChunkPos chunkPos = ChunkPos.of(chunk);

        List<StoredBlock> blocksInChunk = blocksByChunk.get(chunkPos)
                .stream()
                .map(w -> new StoredBlock(
                        w.block(),
                        locationToType.get(w),
                        locationToEntity.get(w).getUniqueId()
                ))
                .toList();

        pdc.set(MekanismKeys.CHUNK_BLOCKS_KEY, StoredBlockListDataType.INSTANCE, blocksInChunk);
        long end = System.currentTimeMillis();
        log.info("Saving blocks to chunk took: {}ms", end - start);
    }

    public void loadChunk(
            @NotNull ChunkPos pos,
            @NotNull List<WorldPos> stored
    ) {
        blocksByChunk.put(pos, stored);
    }

    public void loadBlock(
            @NotNull WorldPos pos,
            @NotNull MekanismBlockType type,
            @NotNull ItemDisplay entity
    ) {
        locationToType.put(pos, type);
        locationToEntity.put(pos, entity);
    }
}
