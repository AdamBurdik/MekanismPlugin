package me.adamix.mekanism.infusion;

import me.adamix.mekanism.recipe.matcher.ItemMatcher;
import org.jetbrains.annotations.NotNull;

public record InfusionMapping(
        @NotNull ItemMatcher matcher,
        InfusionType type,
        int unitsPerItem
) {
}