package me.adamix.mekanism.block.registry;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.component.GeneratorEnergyComponent;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.block.component.network.TransporterComponent;
import me.adamix.mekanism.block.handler.EnergyCubeHandler;
import me.adamix.mekanism.block.handler.SolarGeneratorHandler;
import me.adamix.mekanism.block.handler.UniversalCableHandler;
import me.adamix.mekanism.energy.EnergyStorage;
import me.adamix.mekanism.menu.MenuDefinition;
import me.adamix.mekanism.menu.widget.IndicatorWidget;
import me.adamix.mekanism.network.NetworkType;
import me.adamix.mekanism.network.port.PortType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlockRegistry {
    private final Map<MekanismBlockType, BlockDefinition> registry = new HashMap<>();

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

        register(MekanismBlockType.BASIC_UNIVERSAL_CABLE, new BlockDefinition(
                Material.CONDUIT,
                conduitBlockData,
                "basic_universal_cable",
                cableTransformation,
                List.of(
                        block -> new TransporterComponent(NetworkType.ENERGY)
                ),
                new UniversalCableHandler(),
                null
        ));

        var ports = Map.of(
                BlockFace.SOUTH, PortType.INPUT,
                BlockFace.NORTH, PortType.INPUT,
                BlockFace.EAST, PortType.DISABLED,
                BlockFace.WEST, PortType.DISABLED,
                BlockFace.UP, PortType.OUTPUT,
                BlockFace.DOWN, PortType.OUTPUT
        );

        Integer[] array = {
                2, 3, 4, 5, 6,
                11, 12, 13, 14, 15,
                20, 21, 22, 23, 24
        };

        List<List<ItemStack>> frames = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            List<ItemStack> slotFrames = new ArrayList<>(6);

            for (int lvl = 0; lvl < 11; lvl++) {
                int cmd = i << 4 | lvl;

                ItemStack item = ItemStack.of(Material.PAPER);
                item.editMeta(meta -> {
                    meta.customName(
                            Component.text("Indicator")
                    );
                });
                CustomModelData customModelData = CustomModelData.customModelData()
                        .addString(Integer.toString(cmd))
                        .build();
                item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
                item.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "energy_indicator"));

                slotFrames.add(item);
            }

            frames.add(slotFrames);
        }

        //  INDEX LVL
        // 0b0000 00

        register(MekanismBlockType.BASIC_ENERGY_CUBE, new BlockDefinition(
                Material.BARRIER,
                null,
                "basic_energy_cube",
                cableTransformation,
                List.of(
                        block -> new EnergyComponent(
                                ports,
                                new EnergyStorage(1000, 100, 100, 0)
                        )
                ),
        new EnergyCubeHandler(),
                new MenuDefinition(
                        "<font:mekanism:spaces>\uFF08</font><font:mekanism:menu_titles>\uFF00</font>",
                        4,
                        List.of(
                                new IndicatorWidget(
                                        List.of(array),
                                        3,
                                        instance -> {
                                            EnergyComponent component = instance.get(EnergyComponent.class).orElseThrow();
                                            return 1.0 * component.getEnergy() / component.getCapacity();
                                        },
                                        instance -> {
                                            EnergyComponent component = instance.get(EnergyComponent.class).orElseThrow();
                                            return "%d FE".formatted(component.getEnergy());
                                        },
                                        frames
                                )
                        )
                )
        ));

        register(MekanismBlockType.SOLAR_GENERATOR, new BlockDefinition(
                Material.BARRIER,
                null,
                "solar_generator",
                fullBlockTransformation,
                List.of(
                        block -> new GeneratorEnergyComponent(
                                Map.of(
                                        BlockFace.SOUTH, PortType.DISABLED,
                                        BlockFace.NORTH, PortType.DISABLED,
                                        BlockFace.EAST, PortType.DISABLED,
                                        BlockFace.WEST, PortType.DISABLED,
                                        BlockFace.UP, PortType.DISABLED,
                                        BlockFace.DOWN, PortType.OUTPUT
                                ),
                                new EnergyStorage(1000, 0, 10, 0)
                        )
                ),
                new SolarGeneratorHandler(),
                null
        ));
    }

    public void register(
            @NotNull MekanismBlockType type,
            @NotNull BlockDefinition definition
    ) {
        registry.put(type, definition);
    }

    public @NotNull Optional<BlockDefinition> get(@NotNull MekanismBlockType type) {
        return Optional.ofNullable(registry.get(type));
    }

    public @NotNull BlockDefinition getOrThrow(@NotNull MekanismBlockType type) {
        if (!registry.containsKey(type)) {
            throw new IllegalArgumentException("Block type not registered: " + type.name());
        }
        return registry.get(type);
    }
}
