package me.adamix.mekanism.menu.widget;

import me.adamix.mekanism.menu.MenuDefinition;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record SubMenuWidget(
        @NotNull MenuDefinition subMenu,
        int slot,
        @NotNull ItemStack icon
) implements WidgetDefinition {
}
