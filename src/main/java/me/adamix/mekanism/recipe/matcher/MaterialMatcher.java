package me.adamix.mekanism.recipe.matcher;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record MaterialMatcher(@NotNull Material material) implements ItemMatcher {
    public boolean matches(@NotNull ItemStack stack) {
        return stack.getType() == material;
    }
}