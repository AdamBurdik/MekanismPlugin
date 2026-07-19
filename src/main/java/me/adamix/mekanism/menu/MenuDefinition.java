package me.adamix.mekanism.menu;

import me.adamix.mekanism.menu.widget.WidgetDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MenuDefinition(
        @NotNull String title,
        int rows,
        @NotNull List<WidgetDefinition> widgets
) {
}
