package me.adamix.mekanism.block.component;

import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.energy.EnergyStorage;
import me.adamix.mekanism.network.port.PortType;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GeneratorEnergyComponent extends EnergyComponent implements TickableComponent {
    private long generateRate;

    public GeneratorEnergyComponent(
            @NotNull Map<BlockFace, PortType> ports,
            @NotNull EnergyStorage storage,
            long generateRate
    ) {
        super(ports, storage);
        this.generateRate = generateRate;
    }

    @Override
    public void tick(@NotNull BlockInstance instance) {
        getStorage().put(generateRate, false);
    }
}
