package me.adamix.mekanism.block.handler;

import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.network.NetworkContext;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

public interface BlockHandler {
    @NotNull BlockInstance createBlockInstance(
            @NotNull Block block,
            @NotNull BlockFace facing,
            @NotNull MekanismBlockType type,
            @NotNull NetworkContext networkContext,
            @NotNull BlockDefinition definition
    );

    @NotNull ItemDisplay spawnEntity(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockDefinition definition,
            @NotNull NetworkContext networkContext,
            @NotNull BlockInstance instance
    );

    void updateBlock(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull ItemDisplay entity,
            @NotNull BlockDefinition definition,
            @NotNull NetworkContext networkContext,
            @NotNull BlockInstance instance
    );
}
