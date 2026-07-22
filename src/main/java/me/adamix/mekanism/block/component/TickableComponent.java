package me.adamix.mekanism.block.component;

import me.adamix.mekanism.block.BlockInstance;
import org.jetbrains.annotations.NotNull;

public interface TickableComponent {
    void tick(@NotNull BlockInstance instance);
}