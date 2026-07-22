package me.adamix.mekanism.recipe;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.adamix.mekanism.infusion.InfusionType;
import me.adamix.mekanism.recipe.infuser.InfuserRecipe;
import me.adamix.mekanism.recipe.matcher.MaterialMatcher;
import me.adamix.mekanism.recipe.smelter.SmelterRecipe;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RecipeRegistry {
    private final Map<RecipeType, List<MekanismRecipe>> recipesByType = new HashMap<>();

    public RecipeRegistry() {
        var enrichedIron = ItemStack.of(Material.PAPER);
        enrichedIron.editMeta(meta -> {
            meta.customName(Component.text("Enriched Iron"));
        });
        enrichedIron.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "material/enriched_iron"));

        register(RecipeType.INFUSING, new InfuserRecipe(
                new MaterialMatcher(Material.IRON_INGOT),
                InfusionType.CARBON,
                10,
                enrichedIron,
                200
        ));

        register(RecipeType.SMELTING, new SmelterRecipe(
                new MaterialMatcher(Material.IRON_ORE),
                ItemStack.of(Material.IRON_INGOT),
                200
        ));
    }

    public <T> @NotNull List<T> getRecipes(@NotNull RecipeType type, @NotNull Class<T> clazz) {
        List<MekanismRecipe> recipes = recipesByType.get(type);
        if (recipes == null) return List.of();

        return recipes.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .toList();
    }

    public <T extends MekanismRecipe> void register(@NotNull RecipeType type, @NotNull T recipe) {
        recipesByType.computeIfAbsent(type, _ -> new ArrayList<>())
                .add(recipe);
    }

    public @NotNull Optional<InfuserRecipe> findInfuserRecipe(
            @NotNull ItemStack mainInput,
            @NotNull InfusionType availableType
    ) {
        return getRecipes(RecipeType.INFUSING, InfuserRecipe.class).stream()
                .filter(r -> r.mainInput().matches(mainInput))
                .filter(r -> r.infusionType() == availableType)
                .findFirst();
    }

    public @NotNull Optional<SmelterRecipe> findSmelterRecipe(
            @NotNull ItemStack mainInput
    ) {
        return getRecipes(RecipeType.SMELTING, SmelterRecipe.class).stream()
                .filter(r -> r.mainInput().matches(mainInput))
                .findFirst();
    }
}
