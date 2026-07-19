package me.adamix.mekanism.block.component;

import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ComponentFactory {
    @NotNull Component create(@NotNull Block block);
}
