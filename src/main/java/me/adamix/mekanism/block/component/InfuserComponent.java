package me.adamix.mekanism.block.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.adamix.mekanism.infusion.InfusionStorage;
import me.adamix.mekanism.infusion.InfusionType;
import me.adamix.mekanism.network.port.PortType;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RequiredArgsConstructor
@Getter
@ToString
public class InfuserComponent implements Component {
    private final @NotNull Map<BlockFace, PortType> ports;
    private final @NotNull InfusionStorage storage;

    public int insert(@Nullable InfusionType type, int amount, boolean simulate) {
        return storage.insert(type, amount, simulate);
    }

    public boolean consume(InfusionType type, int amount) {
        return storage.consume(type, amount);
    }

    public @Nullable InfusionType getType() {
        return storage.getCurrentType();
    }

    public long getAmount() {
        return storage.getAmount();
    }

    public long getCapacity() {
        return storage.getCapacity();
    }

    @Override
    public void load(@NotNull PersistentDataContainer pdc) {
        storage.load(pdc);
    }

    @Override
    public void save(@NotNull PersistentDataContainer pdc) {
        storage.save(pdc);
    }
}
