package me.adamix.mekanism.block.handler;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import me.adamix.mekanism.block.BlockFacade;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.type.WorldPos;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class EnergyCubeHandler implements BlockHandler {
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
        System.out.println("New state: " + state);
        entity.setItemStack(createItem(state, definition.itemModel()));
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

        EnergyComponent energy = instance.get(EnergyComponent.class).orElseThrow();

        // SOUTH, NORTH, EAST, WEST, DOWN, UP
        byte state = getState(energy);

        return location.getWorld()
                .spawn(
                        offsetLoc,
                        ItemDisplay.class,
                        e -> applyMeta(e, state, definition)
                );
    }

    private static byte getState(EnergyComponent energy) {
        byte state = 0;

        for (Map.Entry<BlockFace, PortType> entry : energy.getPorts().entrySet()) {
            BlockFace blockFace = entry.getKey();
            PortType portType = entry.getValue();

            boolean shouldConnect = portType == PortType.INPUT || portType == PortType.OUTPUT;

            if (shouldConnect) {
                switch (blockFace) {
                    case SOUTH -> state |= 0b100000;
                    case NORTH -> state |= 0b010000;
                    case EAST -> state  |= 0b001000;
                    case WEST -> state  |= 0b000100;
                    case DOWN -> state  |= 0b000010;
                    case UP -> state    |= 0b000001;
                }
            }
        }
        return state;
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
        EnergyComponent energy = instance.get(EnergyComponent.class).orElseThrow();

        // SOUTH, NORTH, EAST, WEST, DOWN, UP
        byte state = getState(energy);

        applyMeta(entity, state, definition);
    }
}
