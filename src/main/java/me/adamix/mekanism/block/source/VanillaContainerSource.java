package me.adamix.mekanism.block.source;

import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.block.component.item.ItemComponent;
import me.adamix.mekanism.block.component.item.VanillaContainerComponent;
import me.adamix.mekanism.network.port.PortType;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VanillaContainerSource implements ComponentSource {
    private final Map<Class<? extends Component>, Component> adapters;

    public VanillaContainerSource(@NotNull Inventory inventory) {
        adapters = Map.of(ItemComponent.class, new VanillaContainerComponent(inventory, new HashMap<>(Map.of(
                BlockFace.SOUTH, PortType.BOTH,
                BlockFace.NORTH, PortType.BOTH,
                BlockFace.EAST, PortType.BOTH,
                BlockFace.WEST, PortType.BOTH,
                BlockFace.UP, PortType.BOTH,
                BlockFace.DOWN, PortType.BOTH
        ))));
    }

    @Override
    public <T extends Component> Optional<T> get(Class<T> type) {
        return Optional.ofNullable(type.cast(adapters.get(type)));
    }
}
