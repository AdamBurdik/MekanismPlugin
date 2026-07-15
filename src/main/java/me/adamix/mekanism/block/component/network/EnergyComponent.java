package me.adamix.mekanism.block.component.network;

import lombok.Getter;
import lombok.ToString;
import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.energy.EnergyStorage;
import me.adamix.mekanism.network.port.PortType;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static me.adamix.utils.Utils.todo;

@Getter
@ToString
public class EnergyComponent implements Component {
    private final @NotNull Map<BlockFace, PortType> ports;
    private final @NotNull EnergyStorage storage;

    public EnergyComponent(
            @NotNull Map<BlockFace, PortType> ports,
            @NotNull EnergyStorage storage
    ) {
        this.ports = ports;
        this.storage = storage;
    }

    public long extract(long amount, boolean simulate) {
        return storage.extract(amount, simulate);
    }

    public long insert(long amount, boolean simulate) {
        return storage.insert(amount, simulate);
    }

    public long getEnergy() {
        return storage.getEnergy();
    }

    public long getCapacity() {
        return storage.getCapacity();
    }

    @Override
    public void load(@NotNull PersistentDataContainer pdc) {
        todo();
    }

    @Override
    public void save(@NotNull PersistentDataContainer pdc) {
        todo();
    }
}
