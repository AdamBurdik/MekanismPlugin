package me.adamix.mekanism.infusion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.adamix.mekanism.data.MekanismKeys;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
@ToString
public class InfusionStorage {
    private @Nullable InfusionType currentType;
    private int amount;
    private int capacity;

    public int insert(@Nullable InfusionType type, int amount, boolean simulate) {
        if (currentType != null && currentType != type) return 0;

        int inserted = Math.min(amount, capacity - this.amount);
        if (!simulate) {
            this.currentType = type;
            this.amount += inserted;
            if (this.amount == 0) this.currentType = null;
        }
        return inserted;
    }

    public boolean consume(InfusionType type, int amount) {
        if (currentType != type || this.amount < amount) return false;
        this.amount -= amount;
        if (this.amount == 0) currentType = null;
        return true;
    }

    public void load(@NotNull PersistentDataContainer pdc) {
        this.currentType = pdc.get(MekanismKeys.INFUSION_STORAGE_TYPE, MekanismKeys.INFUSION_TYPE);
        this.amount = pdc.get(MekanismKeys.INFUSION_STORAGE_AMOUNT, PersistentDataType.INTEGER).intValue();
        this.capacity = pdc.get(MekanismKeys.INFUSION_STORAGE_CAPACITY, PersistentDataType.INTEGER).intValue();
    }

    public void save(@NotNull PersistentDataContainer pdc) {
        if (currentType != null) {
            pdc.set(MekanismKeys.INFUSION_STORAGE_TYPE, MekanismKeys.INFUSION_TYPE, currentType);
        }
        pdc.set(MekanismKeys.INFUSION_STORAGE_AMOUNT, PersistentDataType.INTEGER, amount);
        pdc.set(MekanismKeys.INFUSION_STORAGE_CAPACITY, PersistentDataType.INTEGER, capacity);
    }
}