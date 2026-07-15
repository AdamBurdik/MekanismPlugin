package me.adamix.mekanism.block.component.energy;

public interface EnergyExtractable {
    long extract(long amount, boolean simulate);
}
