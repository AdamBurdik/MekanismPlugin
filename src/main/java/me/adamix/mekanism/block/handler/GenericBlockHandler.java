package me.adamix.mekanism.block.handler;

import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.utils.EntityUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

public class GenericBlockHandler implements BlockHandler {
    @Override
    public @NotNull ItemDisplay spawnEntity(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockDefinition definition,
            @NotNull NetworkContext networkContext,
            @NotNull BlockInstance instance
    ) {
        return EntityUtils.spawnItemDisplay(block, definition, null);
    }

    @Override
    public void updateBlock(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull ItemDisplay entity,
            @NotNull BlockDefinition definition,
            @NotNull NetworkContext networkContext,
            @NotNull BlockInstance instance
    ) {
        // Nothing
    }
}
