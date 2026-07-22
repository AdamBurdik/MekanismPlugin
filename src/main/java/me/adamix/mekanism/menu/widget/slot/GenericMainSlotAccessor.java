package me.adamix.mekanism.menu.widget.slot;

import me.adamix.mekanism.block.component.item.GenericSlotsComponent;
import me.adamix.mekanism.menu.widget.SlotAccessor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record GenericMainSlotAccessor(
        @NotNull GenericSlotsComponent component
) implements SlotAccessor {
    @Override
    public @Nullable ItemStack get() {
        return component.getMainSlot();
    }

    @Override
    public void set(@Nullable ItemStack item) {
        component.setMainSlot(item);
    }

    @Override
    public boolean canAccept(@NotNull ItemStack item) {
        return true;
    }
}
