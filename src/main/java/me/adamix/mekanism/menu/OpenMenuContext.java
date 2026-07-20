package me.adamix.mekanism.menu;

import me.adamix.mekanism.block.BlockInstance;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

public record OpenMenuContext(
        @NotNull MenuDefinition definition,
        @NotNull BlockInstance instance,
        @NotNull Inventory inventory,
        @NotNull Stack<MenuDefinition> stack
) {
}
