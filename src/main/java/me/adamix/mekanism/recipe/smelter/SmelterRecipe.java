package me.adamix.mekanism.recipe.smelter;

import me.adamix.mekanism.recipe.MekanismRecipe;
import me.adamix.mekanism.recipe.matcher.ItemMatcher;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record SmelterRecipe(
        @NotNull ItemMatcher mainInput,
        @NotNull ItemStack output,
        int smeltingTime
) implements MekanismRecipe {
}
