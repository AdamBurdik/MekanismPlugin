package me.adamix.mekanism.menu;

import me.adamix.mekanism.block.BlockInstance;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public record OpenMenuContext(
        @NotNull Location location,
        @NotNull MenuDefinition definition,
        @NotNull BlockInstance instance,
        @NotNull Inventory inventory
        ) {
}
