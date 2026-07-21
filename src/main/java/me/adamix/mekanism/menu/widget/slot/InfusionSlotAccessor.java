package me.adamix.mekanism.menu.widget.slot;

import me.adamix.mekanism.block.component.InfuserComponent;
import me.adamix.mekanism.menu.widget.SlotAccessor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record InfusionSlotAccessor(
        @NotNull InfuserComponent component
) implements SlotAccessor {
    @Override
    public @Nullable ItemStack get() {
        return component.getInfusionSlot();
    }

    @Override
    public void set(@Nullable ItemStack item) {
        component.setInfusionSlot(item);
    }

    @Override
    public boolean canAccept(@NotNull ItemStack item) {
        return true;
    }
}
