package me.adamix.mekanism.menu.widget;

import me.adamix.mekanism.block.BlockInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record ButtonIndicatorWidget(
        int slot,
        @NotNull BiConsumer<Player, BlockInstance> onClick,
        @Nullable Function<BlockInstance, String> labelProvider,
        @NotNull Function<BlockInstance, ItemStack> iconProvider
) implements WidgetDefinition {
}
