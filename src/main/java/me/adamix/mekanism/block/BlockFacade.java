package me.adamix.mekanism.block;


import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.handler.BlockHandler;
import me.adamix.mekanism.block.handler.BlockHandlerRegistry;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.network.NetworkService;
import me.adamix.utils.BlockUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class BlockFacade {
    private final BlockService blockService;
    private final NetworkService networkService;

    private final BlockHandlerRegistry blockHandlerRegistry;

    public void placeBlock(
            @NotNull Block block,
            @NotNull MekanismBlockType type
    ) {
        Location location = block.getLocation();

        // Get stuff from registry
        BlockHandler blockHandler = blockHandlerRegistry.getOrThrow(type);

        // 1. Place base block
        blockService.placeBlock(block, type);

        // 2. Scan surrounding blocks for their network
        NetworkContext networkContext = networkService.scanSurroundings(location);

        // 3. Create block instance
        BlockInstance instance = blockHandler.createBlockInstance(
                block,
                type,
                networkContext
        );

        // 4. Save block instance
        // TODO Save the instance somewhere, like BlockInstanceService

        // 5. Spawn entity
        blockService.spawnEntity(
                block,
                type,
                networkContext
        );

        // 6. Register block to network
        networkService.registerBlock(
                block,
                type,
                instance
        );

        // 7. Update surrounding blocks
        for (Block surrounding : BlockUtils.getSurroundingBlocks(block)) {
            updateBlock(surrounding);
        }
    }

    public void updateBlock(@NotNull Block block) {
        Location location = block.getLocation();

        if (!blockService.isMekanismBlock(block)) {
            return;
        }

        NetworkContext networkContext = networkService.scanSurroundings(location);

        blockService.updateBlock(
                block,
                networkContext
        );

        networkService.updateBlock(block);
    }
}
