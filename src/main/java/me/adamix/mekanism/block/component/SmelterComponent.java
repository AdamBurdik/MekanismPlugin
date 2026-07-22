package me.adamix.mekanism.block.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.component.item.GenericSlotsComponent;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.recipe.RecipeRegistry;
import me.adamix.mekanism.recipe.smelter.SmelterRecipe;
import me.adamix.utils.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


@RequiredArgsConstructor
@Getter
@ToString
public class SmelterComponent implements Component, TickableComponent, GenericSlotsComponent {
    private static final long ENERGY_PER_TICK = 20;

    private final RecipeRegistry recipeRegistry;
    private final ItemStack[] slots = new ItemStack[2]; // Main, Output
    private int progress = 0;
    private int maxProgress = 0;

    @Override
    public @Nullable ItemStack getMainSlot() {
        return slots[0];
    }

    @Override
    public void setMainSlot(@Nullable ItemStack itemStack) {
        slots[0] = itemStack;
    }

    @Override
    public @Nullable ItemStack getOutputSlot() {
        return slots[1];
    }

    @Override
    public void setOutputSlot(@Nullable ItemStack itemStack) {
        slots[1] = itemStack;
    }

    @Override
    public void tick(@NotNull BlockInstance instance) {
        ItemStack mainSlot = slots[0];
        if (mainSlot == null) {
            progress = 0;
            return;
        }

        Optional<SmelterRecipe> recipeOpt = recipeRegistry.findSmelterRecipe(mainSlot);
        if (recipeOpt.isEmpty()) {
            progress = 0;
            return;
        }

        SmelterRecipe recipe = recipeOpt.get();
        ItemStack currentOutput = slots[1];
        if (!ItemUtils.canFitOutput(currentOutput, recipe.output())) return;

        maxProgress = recipe.smeltingTime();

        EnergyComponent energyComponent = instance.get(EnergyComponent.class).orElseThrow();
        long extracted = energyComponent.extract(ENERGY_PER_TICK, true);
        if (extracted < ENERGY_PER_TICK) {
            return;
        }

        progress++;
        if (progress >= recipe.smeltingTime()) {
            energyComponent.extract(ENERGY_PER_TICK, false);

            ItemStack newMain = mainSlot.clone();
            newMain.setAmount(mainSlot.getAmount() - 1);
            slots[0] = newMain.getAmount() == 0 ? null : newMain;

            ItemStack newOutput = currentOutput == null ? recipe.output().clone() : currentOutput.clone();
            if (currentOutput != null) newOutput.setAmount(currentOutput.getAmount() + recipe.output().getAmount());
            slots[1] = newOutput;

            progress = 0;
        }
    }

    @Override
    public void load(@NotNull PersistentDataContainer pdc) {

    }

    @Override
    public void save(@NotNull PersistentDataContainer pdc) {

    }
}
