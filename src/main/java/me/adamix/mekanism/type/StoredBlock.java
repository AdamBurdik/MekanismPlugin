package me.adamix.mekanism.type;

import me.adamix.mekanism.block.MekanismBlockType;
import org.jetbrains.annotations.NotNull;

public record StoredBlock(@NotNull BlockPos pos, @NotNull MekanismBlockType type) {
}
