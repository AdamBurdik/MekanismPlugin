package me.adamix.mekanism.block.handler;

import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.BlockRegistry;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.network.NetworkContext;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

public interface BlockHandler {
    @NotNull BlockInstance createBlockInstance(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull NetworkContext networkContext
    );

    @NotNull ItemDisplay spawnEntity(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockRegistry.Definition reg,
            @NotNull NetworkContext networkContext
    );

    void updateBlock(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull ItemDisplay entity,
            @NotNull BlockRegistry.Definition reg,
            @NotNull NetworkContext networkContext
    );
}
