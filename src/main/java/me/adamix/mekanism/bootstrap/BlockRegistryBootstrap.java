package me.adamix.mekanism.bootstrap;

import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.component.GeneratorEnergyComponent;
import me.adamix.mekanism.block.component.InfuserComponent;
import me.adamix.mekanism.block.component.SmelterComponent;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.block.component.network.TransporterComponent;
import me.adamix.mekanism.block.handler.EnergyCubeHandler;
import me.adamix.mekanism.block.handler.GenericBlockHandler;
import me.adamix.mekanism.block.handler.SolarGeneratorHandler;
import me.adamix.mekanism.block.handler.TransporterBlockHandler;
import me.adamix.mekanism.block.handler.UniversalCableHandler;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.energy.EnergyStorage;
import me.adamix.mekanism.infusion.InfusionStorage;
import me.adamix.mekanism.infusion.InfusionTypeRegistry;
import me.adamix.mekanism.menu.MenuRegistry;
import me.adamix.mekanism.network.NetworkType;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.recipe.RecipeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BlockRegistryBootstrap {
    private final BlockRegistry registry;
    private final RecipeRegistry recipeRegistry;
    private final MenuRegistry menuRegistry;

    private final InfusionTypeRegistry infusionTypeRegistry;

    private final Transformation cableTransformation = new Transformation(
            new Vector3f(0, 0, 0),
            new Quaternionf(0, 0, 0, 1),
            new Vector3f(1.02f, 1.02f, 1.02f),
            new Quaternionf(0, 0, 0, 1)
    );

    private final Transformation fullBlockTransformation = new Transformation(
            new Vector3f(0, 0, 0),
            new Quaternionf(0, 0, 0, 1f),
            new Vector3f(1f, 1f, 1f),
            new Quaternionf(0, 0, 0, 1f)
    );

    private @NotNull Map<BlockFace, PortType> ports() {
        return new HashMap<>(Map.of(
                BlockFace.SOUTH, PortType.INPUT,
                BlockFace.NORTH, PortType.INPUT,
                BlockFace.EAST, PortType.DISABLED,
                BlockFace.WEST, PortType.DISABLED,
                BlockFace.UP, PortType.OUTPUT,
                BlockFace.DOWN, PortType.OUTPUT
        ));
    }

    private @NotNull EnergyComponent energyComponent(
            long capacity,
            long maxInsert,
            long maxExtract,
            long initialAmount
    ) {
        return new EnergyComponent(
                ports(),
                new EnergyStorage(
                        capacity,
                        maxInsert,
                        maxExtract,
                        initialAmount
                )
        );
    }

    public void registerBlocks() {
        BlockData conduitBlockData = Bukkit.createBlockData(Material.CONDUIT);
        ((Waterlogged) conduitBlockData).setWaterlogged(false);

        registry.register(MekanismBlockType.ENERGIZED_SMELTER, new BlockDefinition(
                Material.BARRIER,
                null,
                "energized_smelter",
                fullBlockTransformation,
                List.of(
                        _ -> energyComponent(800, 100, 100, 0),
                        _ -> new SmelterComponent(ports(), recipeRegistry)
                ),
                new GenericBlockHandler(),
                menuRegistry.energizedSmelter()
        ));
        registry.register(MekanismBlockType.METALLURGIC_INFUSER, new BlockDefinition(
                Material.BARRIER,
                null,
                "metallurgic_infuser",
                fullBlockTransformation,
                List.of(
                        _ -> energyComponent(8000, 100, 100, 0),
                        _ -> new InfuserComponent(ports(), new InfusionStorage(null, 0, 1000), infusionTypeRegistry, recipeRegistry)
                ),
                new GenericBlockHandler(),
                menuRegistry.metallurgicInfuser()
        ));
        registry.register(MekanismBlockType.SOLAR_GENERATOR, new BlockDefinition(
                Material.BARRIER,
                null,
                "solar_generator",
                fullBlockTransformation,
                List.of(
                        _ -> new GeneratorEnergyComponent(
                                ports(),
                                new EnergyStorage(3840, 0, 20, 0),
                                17
                        )
                ),
                new SolarGeneratorHandler(),
                menuRegistry.solarGenerator()
        ));
        registry.register(MekanismBlockType.BASIC_ENERGY_CUBE, new BlockDefinition(
                Material.BARRIER,
                null,
                "energy_cube/basic",
                fullBlockTransformation,
                List.of(
                        _ -> energyComponent(1600000, 1600, 1600, 0)
                ),
                new EnergyCubeHandler(),
                menuRegistry.energyCube()
        ));
        registry.register(MekanismBlockType.BASIC_UNIVERSAL_CABLE, new BlockDefinition(
                Material.CONDUIT,
                conduitBlockData,
                "universal_cable/basic",
                cableTransformation,
                List.of(
                        _ -> new TransporterComponent(NetworkType.ENERGY)
                ),
                new UniversalCableHandler(),
                null
        ));
        registry.register(MekanismBlockType.BASIC_LOGISTICAL_TRANSPORTER, new BlockDefinition(
                Material.CONDUIT,
                conduitBlockData,
                "logistical_transporter/basic",
                cableTransformation,
                List.of(
                        _ -> new TransporterComponent(NetworkType.ITEM)
                ),
                new TransporterBlockHandler(NetworkType.ITEM),
                null
        ));
    }
}
