package me.adamix.mekanism.block.handler;

import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

public interface BlockHandler {
    default @NotNull BlockInstance createBlockInstance(
            @NotNull Block block,
            @NotNull BlockFace facing,
            @NotNull MekanismBlockType type,
            @NotNull NetworkContext networkContext,
            @NotNull BlockDefinition definition
    ) {
        var instance = new BlockInstance(WorldPos.of(block), facing);

        definition.components().forEach(factory -> {
            instance.add(factory.create(block));
        });

        return instance;
    }

    @NotNull ItemDisplay spawnEntity(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockDefinition definition,
            @NotNull NetworkContext networkContext,
            @NotNull BlockInstance instance,
            @NotNull BlockFace facing
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
