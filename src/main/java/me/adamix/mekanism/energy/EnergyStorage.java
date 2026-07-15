package me.adamix.mekanism.energy;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EnergyStorage {
    private long energy;
    private final long capacity;
    private final long maxInsert;
    private final long maxExtract;

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
}