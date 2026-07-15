package me.adamix.mekanism.block.component.energy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.adamix.mekanism.block.component.Component;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public class GeneratorEnergyComponent implements Component, EnergyExtractable, EnergyStorage {
    private long stored;
    private final long capacity;

    @Override
    public void load(@NotNull PersistentDataContainer pdc) {

    }

    @Override
    public void save(@NotNull PersistentDataContainer pdc) {

    }

    @Override
    public long extract(long amount, boolean simulate) {
        long taken = Math.min(amount, stored);
        if (!simulate) stored -= taken;
        return taken;
    }
}
