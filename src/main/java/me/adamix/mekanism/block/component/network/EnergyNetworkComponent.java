package me.adamix.mekanism.block.component.network;

import lombok.Data;
import me.adamix.mekanism.block.component.Component;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
public class EnergyNetworkComponent implements Component {
    private @NotNull UUID networkId;

    public EnergyNetworkComponent(@NotNull UUID networkId) {
        this.networkId = networkId;
    }

    @Override
    public void load(@NotNull PersistentDataContainer pdc) {
        throw new NotImplementedException();
    }

    @Override
    public void save(@NotNull PersistentDataContainer pdc) {
        throw new NotImplementedException();
    }
}
