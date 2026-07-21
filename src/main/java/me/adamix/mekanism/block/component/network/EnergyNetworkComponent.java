package me.adamix.mekanism.block.component.network;

import lombok.Data;
import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.data.MekanismKeys;
import me.adamix.mekanism.type.pdc.UUIDDataType;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

@Data
public class EnergyNetworkComponent implements Component {
    private @NotNull UUID networkId;

    public EnergyNetworkComponent(@NotNull UUID networkId) {
        this.networkId = networkId;
    }

    @Override
    public void load(@NotNull PersistentDataContainer pdc) {
        // Network id should not be stored
    }

    @Override
    public void save(@NotNull PersistentDataContainer pdc) {
        // Network id should not be stored
    }
}
