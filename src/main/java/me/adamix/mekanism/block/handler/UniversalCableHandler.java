package me.adamix.mekanism.block.handler;


import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.BlockRegistry;
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
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

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
            @NotNull BlockRegistry.Definition reg
    ) {
        entity.setItemStack(createItem(state, reg.itemModel()));

        entity.setTransformation(reg.transformation());
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
            @NotNull NetworkContext networkContext
    ) {
        var instance = new BlockInstance();
        // TODO Add transport components or smth idk
        instance.add(
                TransporterComponent.class,
                new TransporterComponent(NetworkType.ENERGY)
        );

        return instance;
    }

    @Override
    public @NotNull ItemDisplay spawnEntity(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull BlockRegistry.Definition reg,
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
                    e -> applyMeta(e, state, reg)
            );
    }

    @Override
    public void updateBlock(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull ItemDisplay entity,
            @NotNull BlockRegistry.Definition reg,
            @NotNull NetworkContext networkContext
    ) {
        byte state = getModelState(networkContext);

        applyMeta(entity, state, reg);
    }
}
