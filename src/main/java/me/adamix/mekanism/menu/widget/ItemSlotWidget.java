package me.adamix.mekanism.menu.widget;

import org.jetbrains.annotations.NotNull;

public record ItemSlotWidget(
        int slot,
        @NotNull SlotAccessor slotAccessor
) implements WidgetDefinition {
}
