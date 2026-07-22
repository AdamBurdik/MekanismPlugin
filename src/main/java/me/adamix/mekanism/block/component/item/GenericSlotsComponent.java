package me.adamix.mekanism.block.component.item;

import me.adamix.mekanism.block.component.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface GenericSlotsComponent extends Component {
    @Nullable ItemStack getMainSlot();

    void setMainSlot(@Nullable ItemStack itemStack);

    @Nullable ItemStack getOutputSlot();

    void setOutputSlot(@Nullable ItemStack itemStack);
}
