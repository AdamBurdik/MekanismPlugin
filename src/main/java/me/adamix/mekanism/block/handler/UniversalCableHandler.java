package me.adamix.mekanism.block.handler;


import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.component.network.TransporterComponent;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.network.NetworkType;
import me.adamix.utils.BlockUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class UniversalCableHandler implements BlockHandler {
    private @NotNull ItemStack createItem(
            byte state,
            @NotNull String itemModel
    ) {
        ItemStack itemStack = ItemStack.of(Material.PAPER);

        itemStack.setData(
                DataComponentTypes.ITEM_MODEL,
                Key.key("mekanism", itemModel)
        );

        CustomModelData customModelData = CustomModelData.customModelData()
                .addString(Byte.toString(state))
                .build();
        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
        return itemStack;
    }

    private void applyMeta(
            @NotNull ItemDisplay entity,
            byte state,
            @NotNull BlockDefinition definition
    ) {
        entity.setItemStack(createItem(state, definition.itemModel()));

        entity.setTransformation(definition.transformation());
    }

    private byte getModelState(@NotNull NetworkContext networkContext) {
        // SOUTH, NORTH, EAST, WEST, DOWN, UP
        byte state = 0;

        byte i = 1;

        for (BlockFace face : BlockUtils.CARDINAL_DIRECTIONS) {
            var network = networkContext.get(face);
            if (network.isPresent() && network.get().type() == NetworkType.ENERGY) {
                state |= i;
            }

            i = (byte) (i << 1);
        }

        return state;
    }

    @Override
    public @NotNull BlockInstance createBlockInstance(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull NetworkContext networkContext,
            @NotNull BlockDefinition definition
    ) {
        var instance = new BlockInstance();

        definition.components().forEach(factory -> {
            instance.add(factory.create(block));
        });

        return instance;
    }

    @Override
    public @NotNull ItemDisplay spawnEntity(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockDefinition definition,
            @NotNull NetworkContext networkContext
    ) {
        Location location = block.getLocation();
        Location offsetLoc = location.clone()
                .add(0.5, 0.5, 0.5);

        byte state = getModelState(networkContext);

        return location.getWorld()
                .spawn(
                        offsetLoc,
                        ItemDisplay.class,
                        e -> applyMeta(e, state, definition)
                );
    }

    @Override
    public void updateBlock(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull ItemDisplay entity,
            @NotNull BlockDefinition definition,
            @NotNull NetworkContext networkContext
    ) {
        byte state = getModelState(networkContext);

        applyMeta(entity, state, definition);
    }
}
