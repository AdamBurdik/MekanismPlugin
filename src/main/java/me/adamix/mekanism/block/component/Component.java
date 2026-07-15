package me.adamix.mekanism.block.component;

import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public interface Component {
    void load(@NotNull PersistentDataContainer pdc);
    void save(@NotNull PersistentDataContainer pdc);
}
