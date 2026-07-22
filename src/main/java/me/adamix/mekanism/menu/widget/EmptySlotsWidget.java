package me.adamix.mekanism.menu.widget;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record EmptySlotsWidget(
        @NotNull List<Integer> slots
) implements WidgetDefinition {
    public EmptySlotsWidget(@NotNull Integer... slots) {
        this(List.of(slots));
    }
}
