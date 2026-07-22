package me.adamix.utils;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemUtils {
    public static boolean canFitOutput(@Nullable ItemStack output, @NotNull ItemStack itemStack) {
        if (output == null) return true;
        if (output.getType() != itemStack.getType()) return false;
        if (output.getAmount() + itemStack.getAmount() > 64) return false;
        // TODO Add check for tags/custom items

        return true;
    }
}
