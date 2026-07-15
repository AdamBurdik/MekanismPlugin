package me.adamix.mekanism.block.component.energy;

public interface EnergyInsertable {
    long insert(long amount, boolean simulate);
}