package me.adamix.mekanism.network.port;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

@Data
@AllArgsConstructor
public class NetworkPort {
    private final @NotNull Location location;
    private final @NotNull BlockFace blockFace;
    private final @NotNull PortType portType;
    private final @NotNull BlockInstance blockInstance;
    private @NotNull UUID networkId;

    public @NotNull Optional<EnergyComponent> getEnergyComponent() {
        return blockInstance.get(EnergyComponent.class);
    }
}
