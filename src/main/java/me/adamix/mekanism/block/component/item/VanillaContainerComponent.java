package me.adamix.mekanism.block.component.item;

import lombok.Getter;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.recipe.matcher.ItemMatcher;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record VanillaContainerComponent(
        @NotNull Inventory inventory,
        @NotNull Map<BlockFace, PortType> ports
) implements ItemComponent {

    @Override
    public @Nullable ItemStack insert(@NotNull ItemStack item, @NotNull BlockFace side, boolean simulate) {
        ItemStack remaining = item.clone();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack slot = inventory.getItem(i);

            if (slot == null || slot.getType().isAir()) {
                int toPlace = Math.min(remaining.getAmount(), remaining.getMaxStackSize());

                if (!simulate) {
                    ItemStack toInsert = remaining.clone();
                    toInsert.setAmount(toPlace);
                    inventory.setItem(i, toInsert);
                }

                remaining.setAmount(remaining.getAmount() - toPlace);
                if (remaining.getAmount() <= 0) return null;
                continue;
            }

            if (slot.isSimilar(remaining)) {
                int space = slot.getMaxStackSize() - slot.getAmount();
                int toAdd = Math.min(space, remaining.getAmount());

                if (toAdd > 0) {
                    if (!simulate) {
                        slot.setAmount(slot.getAmount() + toAdd);
                        inventory.setItem(i, slot);
                    }

                    remaining.setAmount(remaining.getAmount() - toAdd);
                    if (remaining.getAmount() <= 0) return null;
                }
            }
        }

        return remaining.getAmount() > 0 ? remaining : null;
    }

    @Override
    public @NotNull ItemStack extract(
            @NotNull ItemMatcher matcher,
            @NotNull BlockFace side,
            int amount,
            boolean simulate
    ) {
        for (ItemStack stack : inventory.getStorageContents()) {
            if (stack != null && !stack.getType().isAir() && matcher.matches(stack)) {
                int take = Math.min(amount, stack.getAmount());
                ItemStack result = stack.clone();
                result.setAmount(take);

                if (!simulate) {
                    stack.setAmount(stack.getAmount() - take);
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public @NotNull ItemMatcher getAcceptedMatcher(@NotNull BlockFace side) {
        return ItemMatcher.ANY;
    }

    @Override
    public void load(@NotNull PersistentDataContainer pdc) {

    }

    @Override
    public void save(@NotNull PersistentDataContainer pdc) {

    }

    @Override
    public @NotNull Map<BlockFace, PortType> getPorts() {
        return ports;
    }
}
