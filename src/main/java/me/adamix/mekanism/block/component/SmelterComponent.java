package me.adamix.mekanism.block.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.component.item.GenericSlotsComponent;
import me.adamix.mekanism.block.component.item.ItemComponent;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.recipe.RecipeRegistry;
import me.adamix.mekanism.recipe.matcher.ItemMatcher;
import me.adamix.mekanism.recipe.smelter.SmelterRecipe;
import me.adamix.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Getter
@ToString
public class SmelterComponent implements Component, TickableComponent, GenericSlotsComponent, ItemComponent {
    private static final long ENERGY_PER_TICK = 20;

    private final @NotNull Map<BlockFace, PortType> ports;
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

    @Override
    public @Nullable ItemStack insert(
            @NotNull ItemStack stack,
            @NotNull BlockFace side,
            boolean simulate
    ) {
        ItemStack current = getMainSlot();

        if (current != null && !current.isSimilar(stack)) {
            return stack;
        }

        int currentAmount = (current == null) ? 0 : current.getAmount();
        int maxStackSize = current != null ? current.getMaxStackSize() : stack.getMaxStackSize();
        int spaceAvailable = maxStackSize - currentAmount;

        if (spaceAvailable <= 0) {
            return stack;
        }

        int insertedAmount = Math.min(stack.getAmount(), spaceAvailable);
        int remainingAmount = stack.getAmount() - insertedAmount;

        if (!simulate) {
            if (current == null) {
                ItemStack newStack = stack.clone();
                newStack.setAmount(insertedAmount);
                setMainSlot(newStack);
            } else {
                current.setAmount(currentAmount + insertedAmount);
            }
        }

        if (remainingAmount <= 0) {
            return null;
        }

        ItemStack remaining = stack.clone();
        remaining.setAmount(remainingAmount);
        return remaining;
    }

    @Override
    public @Nullable ItemStack extract(
            @NotNull ItemMatcher matcher,
            @NotNull BlockFace side,
            int amount,
            boolean simulate
    ) {
        ItemStack output = slots[1];
        if (output == null) return null;

        if (output.getAmount() <= amount) {
            if (!simulate) {
                slots[1] = null;
            }

            return output;
        }

        ItemStack extracted = output.clone();
        extracted.setAmount(amount);

        if (!simulate) {
            int newAmount = output.getAmount() - amount;
            output.setAmount(newAmount);
        }

        return extracted;
    }

    @Override
    public @NotNull ItemMatcher getAcceptedMatcher(@NotNull BlockFace side) {
        return stack -> stack.getType() == Material.IRON_ORE;
    }
}
