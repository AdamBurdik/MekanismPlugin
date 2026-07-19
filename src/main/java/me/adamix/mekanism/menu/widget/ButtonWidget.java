package me.adamix.mekanism.menu.widget;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record ButtonWidget(
        int slotIndex,
        @NotNull ItemStack icon,
        @NotNull Consumer<Player> onClick
) implements WidgetDefinition {}