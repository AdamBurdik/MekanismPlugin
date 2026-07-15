package me.adamix.mekanism.data;

import me.adamix.mekanism.block.MekanismBlockType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MekanismKeys {
    public static @NotNull NamespacedKey BLOCK_TYPE_KEY;

    public static final EnumDataType<MekanismBlockType> BLOCK_TYPE = new EnumDataType<>(MekanismBlockType.class);

    public static void init(@NotNull JavaPlugin plugin) {
        BLOCK_TYPE_KEY = new NamespacedKey(plugin, "mekanism_block_type");
    }
}
