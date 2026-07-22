package me.adamix.mekanism.block;


import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.block.component.TickableComponent;
import me.adamix.mekanism.block.instance.BlockInstanceService;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.block.tick.BlockTickService;
import me.adamix.mekanism.menu.MenuService;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.network.NetworkService;
import me.adamix.mekanism.type.WorldPos;
import me.adamix.utils.BlockUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class BlockFacade {
    private final BlockService blockService;
    private final NetworkService networkService;
    private final BlockTickService blockTickService;
    private final BlockInstanceService blockInstanceService;
    private final BlockRegistry blockRegistry;
    private final MenuService menuService;

    public void placeBlock(
            @NotNull Block block,
            @NotNull BlockFace facing,
            @NotNull MekanismBlockType type
    ) {
        WorldPos pos = WorldPos.of(block);

        // 1. Place base block
        blockService.placeBlock(block, type);

        // 2. Scan surrounding blocks for their network
        NetworkContext networkContext = networkService.scanSurroundings(pos);

        // 3. Create block instance
        BlockInstance instance = blockInstanceService.create(
                block,
                facing,
                type,
                networkContext
        );

        for (Component component : instance.components()) {
            if (component instanceof TickableComponent tickable) {
                blockTickService.register(pos, instance, tickable);
            }
        }

        // 4. Save block instance
        // TODO Save the instance somewhere, like BlockInstanceService

        // 5. Spawn entity
        blockService.spawnEntity(
                block,
                type,
                networkContext,
                instance
        );

        blockService.saveToChunkPdc(pos);

        // 6. Register block to network
        networkService.registerBlock(
                block,
                type,
                instance
        );

        // 7. Update surrounding blocks
        updateSurroundings(block);
    }

    public void updateSurroundings(@NotNull Block block) {
        for (Block surrounding : BlockUtils.getSurroundingBlocks(block)) {
            updateBlock(surrounding);
        }
    }

    public void updateBlock(@NotNull Block block) {
        WorldPos location = WorldPos.of(block);

        if (!blockService.isMekanismBlock(block)) {
            return;
        }
        var instance = blockInstanceService.get(WorldPos.of(block));
        if (instance.isEmpty()) return;

        NetworkContext networkContext = networkService.scanSurroundings(location);


        blockService.updateBlock(
                block,
                networkContext,
                instance.get()
        );

//        networkService.updateBlock(block);
    }

    public void onBlockClick(
            @NotNull Player player,
            @NotNull Block block
    ) {
        if (!blockService.isMekanismBlock(block)) {
            return;
        }

        var type = blockService.getType(block)
                .orElseThrow();

        var definition = blockRegistry.get(type)
                .orElseThrow();

        if (definition.menu() == null) {
            return;
        }

        var instance = blockInstanceService.get(WorldPos.of(block))
                .orElseThrow();

        menuService.open(
                player,
                definition.menu(),
                instance
        );
    }
}
