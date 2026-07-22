package me.adamix.utils;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import me.adamix.mekanism.block.registry.BlockDefinition;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class EntityUtils {
    private static @NotNull ItemStack createItem(
            @NotNull String itemModel,
            @Nullable CustomModelData customModelData
    ) {
        ItemStack itemStack = ItemStack.of(Material.PAPER);

        itemStack.setData(
                DataComponentTypes.ITEM_MODEL,
                Key.key("mekanism", itemModel)
        );

        if (customModelData != null) {
            itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
        }
        return itemStack;
    }

    public static @NotNull ItemDisplay spawnItemDisplay(
            @NotNull Block block,
            @NotNull BlockDefinition definition,
            @Nullable CustomModelData customModelData

    ) {
        World world = block.getWorld();
        Location location = block.getLocation();
        Location offsetLoc = location.clone()
                .add(0.5, 0.5, 0.5);

        return world.spawn(
                offsetLoc,
                ItemDisplay.class,
                e -> {
                    e.setItemStack(createItem(definition.itemModel(), customModelData));
                    e.setTransformation(definition.transformation());
                }
        );
    }
}
