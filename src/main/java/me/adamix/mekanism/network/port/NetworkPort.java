package me.adamix.mekanism.network.port;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.adamix.mekanism.block.component.item.ItemComponent;
import me.adamix.mekanism.block.source.ComponentSource;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.type.BlockPos;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

@Data
@AllArgsConstructor
public class NetworkPort {
    private final @NotNull BlockPos pos;
    private final @NotNull String worldName;
    private final @NotNull BlockFace blockFace;
    private final @NotNull PortType portType;
    private final @NotNull ComponentSource source;
    private @NotNull UUID networkId;

    public @NotNull Optional<EnergyComponent> getEnergyComponent() {
        return source.get(EnergyComponent.class);
    }

    public @NotNull Optional<ItemComponent> getItemComponent() {
        return source.get(ItemComponent.class);
    }
}
