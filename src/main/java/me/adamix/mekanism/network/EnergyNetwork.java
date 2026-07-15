package me.adamix.mekanism.network;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class EnergyNetwork extends AbstractNetwork {
    public EnergyNetwork(@NotNull UUID id) {
        super(id);
    }

    @Override
    public @NotNull NetworkType type() {
        return NetworkType.ENERGY;
    }
}
