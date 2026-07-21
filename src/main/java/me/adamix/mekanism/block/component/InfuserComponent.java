package me.adamix.mekanism.block.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.adamix.mekanism.infusion.InfusionMapping;
import me.adamix.mekanism.infusion.InfusionStorage;
import me.adamix.mekanism.infusion.InfusionType;
import me.adamix.mekanism.infusion.InfusionTypeRegistry;
import me.adamix.mekanism.menu.widget.SlotAccessor;
import me.adamix.mekanism.network.port.PortType;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
@ToString
public class InfuserComponent implements Component, TickableComponent {
    private final @NotNull Map<BlockFace, PortType> ports;
    private final @NotNull InfusionStorage storage;
    private final InfusionTypeRegistry infusionTypeRegistry;
    private final ItemStack[] slots = new ItemStack[3]; // Main, Infusion, Output
    private int progress;

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

    public @Nullable ItemStack getMainSlot() {
        return slots[0];
    }

    public void setMainSlot(@Nullable ItemStack itemStack) {
        slots[0] = itemStack;
    }

    public @Nullable ItemStack getInfusionSlot() {
        return slots[1];
    }

    public void setInfusionSlot(@Nullable ItemStack itemStack) {
        slots[1] = itemStack;
    }

    public @Nullable ItemStack getOutputSlot() {
        return slots[2];
    }

    @Override
    public void load(@NotNull PersistentDataContainer pdc) {
        storage.load(pdc);
    }

    @Override
    public void save(@NotNull PersistentDataContainer pdc) {
        storage.save(pdc);
    }

    @Override
    public void tick() {
        fillInfusionBuffer();
    }

    private void fillInfusionBuffer() {
        ItemStack infusionItem = slots[1];
        if (infusionItem == null) return;

        Optional<InfusionMapping> mappingOpt = infusionTypeRegistry.getMappingFor(infusionItem);
        if (mappingOpt.isEmpty()) return;
        InfusionMapping mapping = mappingOpt.get();

        int inserted = storage.insert(mapping.type(), mapping.unitsPerItem(), true);
        if (inserted == mapping.unitsPerItem()) {
            storage.insert(mapping.type(), mapping.unitsPerItem(), false);
            ItemStack reduced = infusionItem.clone();
            reduced.setAmount(reduced.getAmount() - 1);
            slots[1] = reduced.getAmount() == 0 ? null : reduced;
        }
    }
}
