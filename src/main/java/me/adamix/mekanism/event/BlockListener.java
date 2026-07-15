package me.adamix.mekanism.event;

import me.adamix.mekanism.block.BlockFacade;
import me.adamix.mekanism.block.BlockService;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.data.MekanismKeys;
import me.adamix.mekanism.network.NetworkService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;


public class BlockListener implements Listener {
    private final BlockFacade blockFacade;

    public BlockListener(
            @NotNull BlockFacade blockFacade
    ) {
        this.blockFacade = blockFacade;
    }

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {

        var pdc = event.getItemInHand().getPersistentDataContainer();
        MekanismBlockType type = pdc.get(MekanismKeys.BLOCK_TYPE_KEY, MekanismKeys.BLOCK_TYPE);
        if (type == null) {
            return;
        }

        blockFacade.placeBlock(event.getBlockPlaced(), type);
    }

    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {

    }
}
