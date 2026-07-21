package me.adamix.mekanism.energy;

import lombok.Getter;
import lombok.ToString;
import me.adamix.mekanism.data.MekanismKeys;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString
public class EnergyStorage {
    private long energy;
    private long capacity;
    private long maxInsert;
    private long maxExtract;

    public EnergyStorage(long capacity) {
        this(capacity, capacity, capacity, 0L);
    }

    public EnergyStorage(long capacity, long maxInsert, long maxExtract, long initialEnergy) {
        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
        this.energy = Math.min(initialEnergy, capacity);
    }

    public long put(long amount, boolean simulate) {
        long inserted = Math.min(amount, capacity - energy);
        if (!simulate) {
            energy += inserted;
        }
        return inserted;
    }

    public long insert(long amount, boolean simulate) {
        long inserted = Math.min(Math.min(amount, maxInsert), capacity - energy);
        if (!simulate) {
            energy += inserted;
        }
        return inserted;
    }

    public long extract(long amount, boolean simulate) {
        long extracted = Math.min(Math.min(amount, maxExtract), energy);
        if (!simulate) {
            energy -= extracted;
        }
        return extracted;
    }

    public boolean isFull() {
        return energy >= capacity;
    }

    public boolean isEmpty() {
        return energy == 0;
    }

    public void load(@NotNull PersistentDataContainer pdc) {
        this.energy = pdc.get(MekanismKeys.ENERGY_STORAGE_ENERGY, PersistentDataType.LONG).longValue();
        this.capacity = pdc.get(MekanismKeys.ENERGY_STORAGE_CAPACITY, PersistentDataType.LONG).longValue();
        this.maxInsert = pdc.get(MekanismKeys.ENERGY_STORAGE_MAX_INSERT, PersistentDataType.LONG).longValue();
        this.maxExtract = pdc.get(MekanismKeys.ENERGY_STORAGE_MAX_EXTRACT, PersistentDataType.LONG).longValue();
    }

    public void save(@NotNull PersistentDataContainer pdc) {
        pdc.set(MekanismKeys.ENERGY_STORAGE_ENERGY, PersistentDataType.LONG, energy);
        pdc.set(MekanismKeys.ENERGY_STORAGE_CAPACITY, PersistentDataType.LONG, capacity);
        pdc.set(MekanismKeys.ENERGY_STORAGE_MAX_INSERT, PersistentDataType.LONG, maxInsert);
        pdc.set(MekanismKeys.ENERGY_STORAGE_MAX_EXTRACT, PersistentDataType.LONG, maxExtract);
    }
}