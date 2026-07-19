package me.adamix.mekanism.block.registry;

import me.adamix.mekanism.block.component.ComponentFactory;
import me.adamix.mekanism.block.handler.BlockHandler;
import me.adamix.mekanism.menu.MenuDefinition;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BlockDefinition(
        @NotNull Material base,
        @Nullable BlockData baseData,
        @NotNull String itemModel,
        @NotNull Transformation transformation,
        @NotNull List<ComponentFactory> components,
        @NotNull BlockHandler handler,
        @Nullable MenuDefinition menu
) {

}
