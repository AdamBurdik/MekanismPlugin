package me.adamix.mekanism.recipe.matcher;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ItemMatcher {
    boolean matches(@NotNull ItemStack stack);
}