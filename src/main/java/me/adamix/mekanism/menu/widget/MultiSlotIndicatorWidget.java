package me.adamix.mekanism.menu.widget;

import me.adamix.mekanism.block.BlockInstance;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public record MultiSlotIndicatorWidget(
        @NotNull List<Integer> slots,
        int sourceSlot,
        int rowCount,
        @NotNull Function<BlockInstance, Double> valueProvider,
        @Nullable Function<BlockInstance, String> labelProvider,
        @NotNull List<ItemStack> frames
) implements WidgetDefinition {}