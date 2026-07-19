package me.adamix.mekanism.menu;

import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public record OpenMenuContext(
        @NotNull WorldPos pos,
        @NotNull MenuDefinition definition,
        @NotNull BlockInstance instance,
        @NotNull Inventory inventory
        ) {
}
