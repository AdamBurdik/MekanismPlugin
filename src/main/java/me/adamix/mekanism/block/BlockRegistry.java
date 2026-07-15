package me.adamix.mekanism.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockRegistry {
    public record Definition(
            @NotNull Material base,
            @Nullable BlockData baseData,
            @NotNull String itemModel,
            @NotNull Transformation transformation
    ) {
    }

    private final Map<MekanismBlockType, Definition> registry = new HashMap<>();

    public BlockRegistry() {
        Transformation cableTransformation = new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf(0, 0, 0, 1),
                new Vector3f(1.02f, 1.02f, 1.02f),
                new Quaternionf(0, 0, 0, 1)
        );

        Transformation fullBlockTransformation = new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf(0, 0, 0, 1f),
                new Vector3f(1f, 1f, 1f),
                new Quaternionf(0, 0, 0, 1f)
        );

        BlockData conduitBlockData = Bukkit.createBlockData(Material.CONDUIT);
        ((Waterlogged) conduitBlockData).setWaterlogged(false);

        register(
                MekanismBlockType.BASIC_UNIVERSAL_CABLE,
                Material.CONDUIT,
                conduitBlockData,
                "basic_universal_cable",
                cableTransformation
        );
        register(
                MekanismBlockType.ADVANCED_UNIVERSAL_CABLE,
                Material.CONDUIT,
                conduitBlockData,
                "advanced_universal_cable",
                cableTransformation
        );
        register(
                MekanismBlockType.ELITE_UNIVERSAL_CABLE,
                Material.CONDUIT,
                conduitBlockData,
                "elite_universal_cable",
                cableTransformation
        );
        register(
                MekanismBlockType.ULTIMATE_UNIVERSAL_CABLE,
                Material.CONDUIT,
                conduitBlockData,
                "ultimate_universal_cable",
                cableTransformation
        );
        register(
                MekanismBlockType.BASIC_ENERGY_CUBE,
                Material.BARRIER,
                null,
                "basic_energy_cube",
                fullBlockTransformation
        );
        register(
                MekanismBlockType.SOLAR_GENERATOR,
                Material.BARRIER,
                null,
                "solar_generator",
                fullBlockTransformation
        );
    }

    public void register(
            @NotNull MekanismBlockType type,
            @NotNull Material base,
            @Nullable BlockData baseState,
            @NotNull String itemModel,
            @NotNull Transformation transformation
    ) {
        registry.put(type, new Definition(base, baseState, itemModel, transformation));
    }

    public @NotNull Optional<Definition> get(@NotNull MekanismBlockType type) {
        return Optional.ofNullable(registry.get(type));
    }

    public @NotNull BlockRegistry.Definition getOrThrow(@NotNull MekanismBlockType type) {
        if (!registry.containsKey(type)) {
            throw new IllegalArgumentException("Block type not registered: " + type.name());
        }
        return registry.get(type);
    }
}
