package me.adamix.mekanism.menu.widget;

import me.adamix.mekanism.block.BlockInstance;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record ItemSlotSupplierWidget(
        int slot,
        @NotNull Function<BlockInstance, SlotAccessor> slotAccessor
) implements WidgetDefinition {

}
