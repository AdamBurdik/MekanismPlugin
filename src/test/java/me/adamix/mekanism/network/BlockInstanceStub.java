package me.adamix.mekanism.network;

import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.component.network.EnergyComponent;

class BlockInstanceStub extends BlockInstance {
    private final EnergyComponent energyComponent;

    BlockInstanceStub(EnergyComponent energyComponent) {
        this.energyComponent = energyComponent;
        add(energyComponent);
    }
}
