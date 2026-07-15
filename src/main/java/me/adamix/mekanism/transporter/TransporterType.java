package me.adamix.mekanism.transporter;

import me.adamix.mekanism.network.NetworkType;
import org.jetbrains.annotations.NotNull;

public enum TransporterType {
    UNIVERSAL_CABLE(NetworkType.ENERGY);

    public final NetworkType networkType;

    TransporterType(@NotNull NetworkType networkType) {
        this.networkType = networkType;
    }
}
