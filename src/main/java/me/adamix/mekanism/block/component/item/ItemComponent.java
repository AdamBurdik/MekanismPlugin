package me.adamix.mekanism.block.component.item;

import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.recipe.matcher.ItemMatcher;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface ItemComponent extends Component {
    @Nullable ItemStack insert(@NotNull ItemStack item, @NotNull BlockFace side, boolean simulate);

    @Nullable ItemStack extract(@NotNull ItemMatcher matcher, @NotNull BlockFace side, int amount, boolean simulate);

    @NotNull ItemMatcher getAcceptedMatcher(@NotNull BlockFace side);

    default ItemStack extract(@NotNull BlockFace side, int amount, boolean simulate) {
        return extract(ItemMatcher.ANY, side, amount, simulate);
    }

    @NotNull Map<BlockFace, PortType> getPorts();
}
