package me.adamix.mekanism.block.handler;

import io.papermc.paper.datacomponent.item.CustomModelData;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.network.NetworkType;
import me.adamix.utils.BlockUtils;
import me.adamix.utils.EntityUtils;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public record TransporterBlockHandler(
        @NotNull NetworkType networkType
) implements BlockHandler {
    private byte getModelState(@NotNull NetworkContext networkContext) {
        // SOUTH, NORTH, EAST, WEST, DOWN, UP
        byte state = 0;

        byte i = 1;

        for (BlockFace face : BlockUtils.CARDINAL_DIRECTIONS) {
            var network = networkContext.get(face);
            if (network.isPresent() && network.get().type() == networkType) {
                state |= i;
            }

            i = (byte) (i << 1);
        }

        return state;
    }

    @Override
    public @NotNull ItemDisplay spawnEntity(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockDefinition definition,
            @NotNull NetworkContext networkContext,
            @NotNull BlockInstance instance,
            @NotNull BlockFace facing
    ) {
        CustomModelData customModelData = CustomModelData.customModelData()
                .addString(Byte.toString(getModelState(networkContext)))
                .build();

        return EntityUtils.spawnItemDisplay(block.getLocation(), definition, customModelData);
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
        CustomModelData customModelData = CustomModelData.customModelData()
                .addString(Byte.toString(getModelState(networkContext)))
                .build();

        EntityUtils.updateEntity(entity, definition, customModelData);
    }
}
