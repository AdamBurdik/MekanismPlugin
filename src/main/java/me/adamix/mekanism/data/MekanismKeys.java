package me.adamix.mekanism.data;

import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.network.NetworkType;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MekanismKeys {
    public static @NotNull NamespacedKey BLOCK_TYPE_KEY;
    public static @NotNull NamespacedKey CHUNK_BLOCKS_KEY;


    // Components keys
    public static @NotNull NamespacedKey NETWORK_TYPE_KEY;

    public static @NotNull NamespacedKey FACING;

    public static @NotNull NamespacedKey ENERGY_COMPONENT_ENERGY;
    public static @NotNull NamespacedKey ENERGY_COMPONENT_CAPACITY;
    public static @NotNull NamespacedKey ENERGY_COMPONENT_MAX_INSERT;
    public static @NotNull NamespacedKey ENERGY_COMPONENT_MAX_EXTRACT;

    public static final EnumDataType<MekanismBlockType> BLOCK_TYPE = new EnumDataType<>(MekanismBlockType.class);
    public static final EnumDataType<NetworkType> NETWORK_TYPE = new EnumDataType<>(NetworkType.class);


    public static void init(@NotNull JavaPlugin plugin) {
        BLOCK_TYPE_KEY = new NamespacedKey(plugin, "mekanism_block_type");
        CHUNK_BLOCKS_KEY = new NamespacedKey(plugin, "mekanism_chunk_blocks");

        // Components keys
        NETWORK_TYPE_KEY = new NamespacedKey(plugin, "network_type");

        FACING = new NamespacedKey(plugin, "facing");

        ENERGY_COMPONENT_ENERGY = new NamespacedKey(plugin, "energy_component_energy");
        ENERGY_COMPONENT_CAPACITY = new NamespacedKey(plugin, "energy_component_capacity");
        ENERGY_COMPONENT_MAX_INSERT = new NamespacedKey(plugin, "energy_component_max_insert");
        ENERGY_COMPONENT_MAX_EXTRACT = new NamespacedKey(plugin, "energy_component_max_extract");
    }
}
