package me.adamix.mekanism.type;

import me.adamix.mekanism.block.MekanismBlockType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record StoredBlock(
        @NotNull BlockPos pos,
        @NotNull MekanismBlockType type,
        @NotNull UUID entityId
) {
}
