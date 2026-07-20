package me.adamix.mekanism.network;

import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.block.BlockFace;

class BlockInstanceStub extends BlockInstance {
    private final EnergyComponent energyComponent;

    BlockInstanceStub(EnergyComponent energyComponent, WorldPos worldPos, BlockFace facing) {
        super(worldPos, facing);
        this.energyComponent = energyComponent;
        add(energyComponent);
    }
}
