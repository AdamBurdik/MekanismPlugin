package me.adamix.mekanism.recipe.infuser;

import me.adamix.mekanism.infusion.InfusionType;
import me.adamix.mekanism.recipe.matcher.ItemMatcher;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record InfuserRecipe(
        @NotNull ItemMatcher mainInput,
        @NotNull InfusionType infusionType,
        int infusionAmount,
        @NotNull ItemStack output,
        int processingTime
) {}