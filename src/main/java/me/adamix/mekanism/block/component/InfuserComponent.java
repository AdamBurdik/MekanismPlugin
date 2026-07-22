package me.adamix.mekanism.block.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.component.item.GenericSlotsComponent;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.infusion.InfusionMapping;
import me.adamix.mekanism.infusion.InfusionStorage;
import me.adamix.mekanism.infusion.InfusionType;
import me.adamix.mekanism.infusion.InfusionTypeRegistry;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.recipe.RecipeRegistry;
import me.adamix.mekanism.recipe.infuser.InfuserRecipe;
import me.adamix.utils.ItemUtils;
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
public class InfuserComponent implements Component, TickableComponent, GenericSlotsComponent {
    private static final long ENERGY_PER_TICK = 20;

    private final @NotNull Map<BlockFace, PortType> ports;
    private final @NotNull InfusionStorage storage;
    private final InfusionTypeRegistry infusionTypeRegistry;
    private final RecipeRegistry recipeRegistry;
    private final ItemStack[] slots = new ItemStack[3]; // Main, Infusion, Output
    private int progress = 0;
    private int maxProgress = 0;

    public int insert(@Nullable InfusionType type, int amount, boolean simulate) {
        return storage.insert(type, amount, simulate);
    }

    public boolean consume(InfusionType type, int amount) {
        return storage.consume(type, amount);
    }

    public @Nullable InfusionType getType() {
        return storage.getCurrentType();
    }

    public long getAmount() {
        return storage.getAmount();
    }

    public long getCapacity() {
        return storage.getCapacity();
    }

    public @Nullable ItemStack getInfusionSlot() {
        return slots[1];
    }

    public void setInfusionSlot(@Nullable ItemStack itemStack) {
        slots[1] = itemStack;
    }

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
        return slots[2];
    }

    @Override
    public void setOutputSlot(@Nullable ItemStack itemStack) {
        slots[2] = itemStack;
    }

    @Override
    public void load(@NotNull PersistentDataContainer pdc) {
        storage.load(pdc);
    }

    @Override
    public void save(@NotNull PersistentDataContainer pdc) {
        storage.save(pdc);
    }

    @Override
    public void tick(@NotNull BlockInstance instance) {
        fillInfusionBuffer();

        ItemStack mainSlot = slots[0];
        if (mainSlot == null || storage.getCurrentType() == null) {
            progress = 0;
            return;
        }

        Optional<InfuserRecipe> recipeOpt = recipeRegistry.findInfuserRecipe(mainSlot, storage.getCurrentType());
        if (recipeOpt.isEmpty()) {
            progress = 0;
            return;
        }

        InfuserRecipe recipe = recipeOpt.get();
        if (storage.getAmount() < recipe.infusionAmount()) {
            progress = 0;
            return;
        }

        ItemStack currentOutput = slots[2];
        if (!ItemUtils.canFitOutput(currentOutput, recipe.output())) return;

        maxProgress = recipe.processingTime();

        EnergyComponent energyComponent = instance.get(EnergyComponent.class).orElseThrow();
        long extracted = energyComponent.extract(ENERGY_PER_TICK, true);
        if (extracted < ENERGY_PER_TICK) {
            return;
        }

        progress++;
        if (progress >= recipe.processingTime()) {
            energyComponent.extract(ENERGY_PER_TICK, false);

            ItemStack newMain = mainSlot.clone();
            newMain.setAmount(mainSlot.getAmount() - 1);
            slots[0] = newMain.getAmount() == 0 ? null : newMain;

            storage.consume(recipe.infusionType(), recipe.infusionAmount());

            ItemStack newOutput = currentOutput == null ? recipe.output().clone() : currentOutput.clone();
            if (currentOutput != null) newOutput.setAmount(currentOutput.getAmount() + recipe.output().getAmount());
            slots[2] = newOutput;

            progress = 0;
        }
    }

    private void fillInfusionBuffer() {
        ItemStack infusionItem = slots[1];
        if (infusionItem == null) return;

        Optional<InfusionMapping> mappingOpt = infusionTypeRegistry.getMappingFor(infusionItem);
        if (mappingOpt.isEmpty()) return;
        InfusionMapping mapping = mappingOpt.get();

        int inserted = storage.insert(mapping.type(), mapping.unitsPerItem(), true);
        if (inserted == mapping.unitsPerItem()) {
            storage.insert(mapping.type(), mapping.unitsPerItem(), false);
            ItemStack reduced = infusionItem.clone();
            reduced.setAmount(reduced.getAmount() - 1);
            slots[1] = reduced.getAmount() == 0 ? null : reduced;
        }
    }
}
