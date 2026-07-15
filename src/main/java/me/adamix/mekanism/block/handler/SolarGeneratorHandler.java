package me.adamix.mekanism.block.handler;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.BlockRegistry;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.component.GeneratorEnergyComponent;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.energy.EnergyStorage;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.network.port.PortType;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class SolarGeneratorHandler implements BlockHandler {
    @Override
    public @NotNull BlockInstance createBlockInstance(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull NetworkContext networkContext
    ) {
        var instance = new BlockInstance();
        var ports = Map.of(
                BlockFace.SOUTH, PortType.DISABLED,
                BlockFace.NORTH, PortType.DISABLED,
                BlockFace.EAST, PortType.DISABLED,
                BlockFace.WEST, PortType.DISABLED,
                BlockFace.UP, PortType.DISABLED,
                BlockFace.DOWN, PortType.OUTPUT
        );

        instance.add(
                EnergyComponent.class,
                new GeneratorEnergyComponent(
                        ports,
                        new EnergyStorage(1000, 0, 10, 0)
                )
        );
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
            @NotNull BlockRegistry.Definition reg
    ) {
        entity.setItemStack(createItem(reg.itemModel()));
        entity.setTransformation(reg.transformation());
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

        return location.getWorld()
                .spawn(
                        offsetLoc,
                        ItemDisplay.class,
                        e -> applyMeta(e, reg)
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

    }
}
