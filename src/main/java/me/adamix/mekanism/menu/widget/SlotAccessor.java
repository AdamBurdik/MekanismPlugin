package me.adamix.mekanism.menu.widget;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SlotAccessor {
    @Nullable ItemStack get();
    void set(@Nullable ItemStack item);
    boolean canAccept(@NotNull ItemStack item);
}