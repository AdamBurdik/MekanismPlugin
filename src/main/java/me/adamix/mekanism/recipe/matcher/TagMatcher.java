package me.adamix.mekanism.recipe.matcher;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record TagMatcher(@NotNull Tag<Material> tag) implements ItemMatcher {
    public boolean matches(@NotNull ItemStack stack) {
        return tag.isTagged(stack.getType());
    }
}