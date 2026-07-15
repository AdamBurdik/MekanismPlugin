package me.adamix.mekanism.block.component.network;

import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.block.port.PortType;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static me.adamix.utils.Utils.todo;

public record EnergyComponent(
        @NotNull Map<BlockFace, PortType> ports
) implements Component {

    @Override
    public void load(@NotNull PersistentDataContainer pdc) {
        todo();
    }

    @Override
    public void save(@NotNull PersistentDataContainer pdc) {
        todo();
    }
}
