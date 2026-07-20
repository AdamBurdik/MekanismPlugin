package me.adamix.mekanism.block.handler;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.type.WorldPos;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SolarGeneratorHandler implements BlockHandler {
    @Override
    public @NotNull BlockInstance createBlockInstance(
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


    private @NotNull ItemStack createItem(
            @NotNull String itemModel
    ) {
        ItemStack itemStack = ItemStack.of(Material.PAPER);

        itemStack.setData(
                DataComponentTypes.ITEM_MODEL,
                Key.key("mekanism", itemModel)
        );

        return itemStack;
    }

    private void applyMeta(
            @NotNull ItemDisplay entity,
            @NotNull BlockDefinition definition
    ) {
        entity.setItemStack(createItem(definition.itemModel()));
        entity.setTransformation(definition.transformation());
    }

    @Override
    public @NotNull ItemDisplay spawnEntity(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockDefinition definition,
            @NotNull NetworkContext networkContext,
            @NotNull BlockInstance instance
    ) {
        Location location = block.getLocation();
        Location offsetLoc = location.clone()
                .add(0.5, 0.5, 0.5);

        return location.getWorld()
                .spawn(
                        offsetLoc,
                        ItemDisplay.class,
                        e -> applyMeta(e, definition)
                );
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

    }
}
