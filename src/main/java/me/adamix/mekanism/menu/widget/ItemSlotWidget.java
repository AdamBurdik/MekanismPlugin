package me.adamix.mekanism.menu.widget;

import org.jetbrains.annotations.NotNull;

public record ItemSlotWidget(
        int slotIndex,
        @NotNull SlotAccessor slotAccessor
) implements WidgetDefinition {
}
