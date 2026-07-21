package me.adamix.mekanism.event;

import com.jeff_media.customblockdata.CustomBlockData;
import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.Mekanism;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.BlockService;
import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.block.component.TickableComponent;
import me.adamix.mekanism.block.instance.BlockInstanceService;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.block.tick.BlockTickService;
import me.adamix.mekanism.data.MekanismKeys;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.network.NetworkService;
import me.adamix.mekanism.type.ChunkPos;
import me.adamix.mekanism.type.StoredBlock;
import me.adamix.mekanism.type.WorldPos;
import me.adamix.mekanism.type.pdc.StoredBlockListDataType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ChunkListener implements Listener {
    private final Mekanism plugin;
    private final BlockService blockService;
    private final BlockRegistry blockRegistry;
    private final NetworkService networkService;
    private final BlockInstanceService blockInstanceService;
    private final BlockTickService blockTickService;

    @EventHandler
    public void onChunkLoad(@NotNull ChunkLoadEvent event) {
        var pdc = event.getChunk().getPersistentDataContainer();

        List<StoredBlock> storedBlocks = pdc.get(MekanismKeys.CHUNK_BLOCKS_KEY, StoredBlockListDataType.INSTANCE);
        if (storedBlocks == null) return;

        var chunk = event.getChunk();

        List<WorldPos> blocks = new ArrayList<>();

        for (StoredBlock stored : storedBlocks) {
            WorldPos pos = new WorldPos(
                    chunk.getWorld().getName(),
                    stored.pos()
            );

            Block block = pos.resolveBlock();

            var definition = blockRegistry.get(stored.type()).orElseThrow();

            NetworkContext networkContext = networkService.scanSurroundings(pos);

            BlockInstance instance = definition.handler().createBlockInstance(
                    block,
                    BlockFace.NORTH,
                    stored.type(),
                    networkContext,
                    definition
            );

            ItemDisplay entity = (ItemDisplay) Bukkit.getEntity(stored.entityId());
            if (entity == null) {
                entity = definition.handler().spawnEntity(
                        block,
                        stored.type(),
                        definition,
                        networkContext,
                        instance
                );
            }

            blockService.loadBlock(pos, stored.type(), entity);
            blocks.add(pos);

            instance.load(new CustomBlockData(block, plugin));

            networkService.registerBlock(
                    block,
                    stored.type(),
                    instance
            );

            for (Component component : instance.components()) {
                if (component instanceof TickableComponent tickable) {
                    blockTickService.register(pos, tickable);
                }
            }

            blockInstanceService.load(instance);
        }

        blockService.loadChunk(ChunkPos.of(chunk), blocks);
    }
}
