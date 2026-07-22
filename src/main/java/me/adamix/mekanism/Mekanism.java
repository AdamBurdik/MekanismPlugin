package me.adamix.mekanism;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.adamix.mekanism.block.BlockFacade;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.BlockService;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.component.GeneratorEnergyComponent;
import me.adamix.mekanism.block.component.InfuserComponent;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.block.component.network.TransporterComponent;
import me.adamix.mekanism.block.handler.EnergyCubeHandler;
import me.adamix.mekanism.block.handler.MetallurgicInfuserHandler;
import me.adamix.mekanism.block.handler.SolarGeneratorHandler;
import me.adamix.mekanism.block.handler.UniversalCableHandler;
import me.adamix.mekanism.block.instance.BlockInstanceService;
import me.adamix.mekanism.block.persistence.BlockPersistenceService;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.block.tick.BlockTickService;
import me.adamix.mekanism.command.DebugCommand;
import me.adamix.mekanism.command.GiveCommand;
import me.adamix.mekanism.command.SaveCommand;
import me.adamix.mekanism.command.TestCommand;
import me.adamix.mekanism.data.MekanismKeys;
import me.adamix.mekanism.energy.EnergyStorage;
import me.adamix.mekanism.event.BlockListener;
import me.adamix.mekanism.event.ChunkListener;
import me.adamix.mekanism.event.InventoryListener;
import me.adamix.mekanism.infusion.InfusionMapping;
import me.adamix.mekanism.infusion.InfusionStorage;
import me.adamix.mekanism.infusion.InfusionType;
import me.adamix.mekanism.infusion.InfusionTypeRegistry;
import me.adamix.mekanism.item.SlotType;
import me.adamix.mekanism.menu.MenuDefinition;
import me.adamix.mekanism.menu.MenuService;
import me.adamix.mekanism.menu.widget.ButtonIndicatorWidget;
import me.adamix.mekanism.menu.widget.ButtonWidget;
import me.adamix.mekanism.menu.widget.IndicatorWidget;
import me.adamix.mekanism.menu.widget.ItemSlotSupplierWidget;
import me.adamix.mekanism.menu.widget.ItemSlotWidget;
import me.adamix.mekanism.menu.widget.MultiSlotIndicatorWidget;
import me.adamix.mekanism.menu.widget.SlotAccessor;
import me.adamix.mekanism.menu.widget.SubMenuWidget;
import me.adamix.mekanism.menu.widget.WidgetDefinition;
import me.adamix.mekanism.menu.widget.slot.InfuserMainSlotAccessor;
import me.adamix.mekanism.menu.widget.slot.InfuserOutputSlotAccessor;
import me.adamix.mekanism.menu.widget.slot.InfusionSlotAccessor;
import me.adamix.mekanism.network.NetworkService;
import me.adamix.mekanism.network.NetworkType;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.recipe.RecipeRegistry;
import me.adamix.mekanism.recipe.matcher.MaterialMatcher;
import me.adamix.mekanism.type.RelativeFace;
import me.adamix.utils.StringUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public final class Mekanism extends JavaPlugin {
    private NetworkService networkService;
    private BlockFacade blockFacade;
    private MenuService menuService;
    private BlockPersistenceService blockPersistenceService;
    private InfusionTypeRegistry infusionTypeRegistry;
    private RecipeRegistry recipeRegistry;

    private final BiFunction<String, SlotType, ItemStack> slotSupplier = (name, type) -> {
        var itemStack = ItemStack.of(Material.PAPER);
        itemStack.editMeta(meta -> {
            meta.customName(Component.text(name));
        });
        itemStack.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "slots/" + type.name().toLowerCase()));

        return itemStack;
    };
    private final Map<PortType, ItemStack> portToSlotItem = Map.of(
            PortType.INPUT, slotSupplier.apply("Input", SlotType.BLUE),
            PortType.OUTPUT, slotSupplier.apply("Output", SlotType.DARK_RED),
            PortType.DISABLED, slotSupplier.apply("None", SlotType.NONE)
    );

    public void onSlotClick(
            @NotNull Player player,
            @NotNull BlockInstance instance,
            @NotNull RelativeFace relativeFace
    ) {
        var energy = instance.get(EnergyComponent.class).orElseThrow();
        var face = relativeFace.toWorldFace(instance.getFacing());

        PortType current = energy.getPorts().get(face);
        PortType next = switch (current) {
            case INPUT -> PortType.OUTPUT;
            case OUTPUT -> PortType.DISABLED;
            case DISABLED -> PortType.INPUT;
            case null -> PortType.INPUT;
        };

        energy.getPorts().put(face, next);

        networkService.unregisterPort(
                instance.getPos(),
                face
        );
        if (next != PortType.DISABLED) {
            networkService.registerPorts(
                    instance.getPos(),
                    NetworkType.ENERGY,
                    instance,
                    energy.getPorts()
            );
        }
        blockFacade.updateBlock(instance.getPos().resolveBlock());
        blockFacade.updateSurroundings(instance.getPos().resolveBlock());
        menuService.update(player);
    }

    public void clearSlots(
            @NotNull Player player,
            @NotNull BlockInstance instance
    ) {
        var energy = instance.get(EnergyComponent.class).orElseThrow();

        for (BlockFace face : BlockFace.values()) {
            energy.getPorts().put(face, PortType.DISABLED);

            networkService.unregisterPort(
                    instance.getPos(),
                    face
            );
        }
        blockFacade.updateBlock(instance.getPos().resolveBlock());
        blockFacade.updateSurroundings(instance.getPos().resolveBlock());
        menuService.update(player);
    }

    public @NotNull ItemStack slotItemProvider(@NotNull BlockInstance instance, @NotNull RelativeFace relativeFace) {
        var energy = instance.get(EnergyComponent.class).orElseThrow();

        var face = relativeFace.toWorldFace(instance.getFacing());

        PortType portType = energy.getPorts().get(face);
        return portToSlotItem.get(portType);
    }

    public void registerBlocks(
            @NotNull MenuService menuService,
            @NotNull BlockFacade blockFacade,
            @NotNull BlockRegistry registry
    ) {
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

        registry.register(MekanismBlockType.BASIC_UNIVERSAL_CABLE, new BlockDefinition(
                Material.CONDUIT,
                conduitBlockData,
                "universal_cable/basic",
                cableTransformation,
                List.of(
                        block -> new TransporterComponent(NetworkType.ENERGY)
                ),
                new UniversalCableHandler(),
                null
        ));

        Supplier<Map<BlockFace, PortType>> portsSupplier = () -> new HashMap<>(Map.of(
                BlockFace.SOUTH, PortType.INPUT,
                BlockFace.NORTH, PortType.INPUT,
                BlockFace.EAST, PortType.DISABLED,
                BlockFace.WEST, PortType.DISABLED,
                BlockFace.UP, PortType.OUTPUT,
                BlockFace.DOWN, PortType.OUTPUT
        ));

        var d = BlockFace.DOWN;

        d.getDirection();


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

        var slotsIcon = ItemStack.of(Material.PAPER);
        slotsIcon.editMeta(meta -> {
            meta.customName(Component.text("Side Config"));
        });
        slotsIcon.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "slots_icon"));

        var closeIcon = ItemStack.of(Material.PAPER);
        closeIcon.editMeta(meta -> {
            meta.customName(Component.text("Close"));
        });
        closeIcon.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "close_icon"));

        var clearSlotsIcon = ItemStack.of(Material.PAPER);
        clearSlotsIcon.editMeta(meta -> {
            meta.customName(Component.text("Clear Slots"));
        });
        clearSlotsIcon.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "clear_slots_icon"));

        var ejectIcon = ItemStack.of(Material.PAPER);
        ejectIcon.editMeta(meta -> {
            meta.customName(Component.text("Eject is not yet supported"));
        });
        ejectIcon.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "eject_icon"));

        List<WidgetDefinition> energyConfigWidgets = new ArrayList<>();
        energyConfigWidgets.add(new ButtonWidget(
                0,
                closeIcon,
                menuService::closeSubmenu
        ));

//        ports.forEach((face, port) -> {
//            ItemStack itemStack = switch (port) {
//                case INPUT -> slotSupplier.apply("Input", SlotType.BLUE);
//                case OUTPUT -> slotSupplier.apply("Output", SlotType.DARK_RED);
//                case DISABLED -> slotSupplier.apply("None", SlotType.NONE);
//            };
//
//            RelativeFace.fromWorldFace()
//        });

        BiFunction<RelativeFace, Integer, WidgetDefinition> slotIndicatorSupplier = (relativeFace, slot) -> new ButtonIndicatorWidget(
                slot,
                (player, instance) -> {
                    onSlotClick(player, instance, relativeFace);
                },
                instance -> {
                    String side = StringUtils.capitalizeFirst(relativeFace.name().toLowerCase());

                    EnergyComponent energy = instance.get(EnergyComponent.class).orElseThrow();

                    var face = relativeFace.toWorldFace(instance.getFacing());

                    PortType portType = energy.getPorts().get(face);
                    String portName = switch (portType) {
                        case INPUT -> "Input";
                        case OUTPUT -> "Output";
                        case DISABLED -> "None";
                    };

                    String slotTypeName = switch (portType) {
                        case INPUT -> "Blue";
                        case OUTPUT -> "Dark Red";
                        case DISABLED -> "Light Gray";
                    };

                    return portName + " (" + slotTypeName + ") (" + side + ")";
                },
                instance -> slotItemProvider(instance, relativeFace)
        );

        var dummySlotAccessor = new SlotAccessor() {
            @Override
            public @Nullable ItemStack get() {
                return null;
            }

            @Override
            public void set(@Nullable ItemStack item) {

            }

            @Override
            public boolean canAccept(@NotNull ItemStack item) {
                return false;
            }
        };

        var energyConfigMenu = new MenuDefinition(
                "<font:mekanism:spaces>\uFF08</font><font:mekanism:menu_titles>\uFF02</font>",
                4,
                List.of(
                        new ButtonWidget(
                                0,
                                closeIcon,
                                menuService::closeSubmenu
                        ),
                        slotIndicatorSupplier.apply(RelativeFace.TOP, 13),
                        slotIndicatorSupplier.apply(RelativeFace.LEFT, 21),
                        slotIndicatorSupplier.apply(RelativeFace.FRONT, 22),
                        slotIndicatorSupplier.apply(RelativeFace.RIGHT, 23),
                        slotIndicatorSupplier.apply(RelativeFace.BACK, 30),
                        slotIndicatorSupplier.apply(RelativeFace.BOTTOM, 31),
                        new ItemSlotWidget(
                                2,
                                dummySlotAccessor
                        ),
                        new ItemSlotWidget(
                                3,
                                dummySlotAccessor
                        ),
                        new ItemSlotWidget(
                                4,
                                dummySlotAccessor
                        ),
                        new ItemSlotWidget(
                                5,
                                dummySlotAccessor
                        ),
                        new ItemSlotWidget(
                                6,
                                dummySlotAccessor
                        ),
                        new ButtonWidget(
                                8,
                                ejectIcon,
                                _ -> {
                                }
                        ),
                        new ButtonIndicatorWidget(
                                35,
                                this::clearSlots,
                                instance -> "Clear Slots",
                                instance -> clearSlotsIcon
                        )
                )
        );

        registry.register(MekanismBlockType.BASIC_ENERGY_CUBE, new BlockDefinition(
                Material.BARRIER,
                null,
                "energy_cube/basic",
                cableTransformation,
                List.of(
                        block -> new EnergyComponent(
                                portsSupplier.get(),
                                new EnergyStorage(1600000, 1600, 1600, 0)
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
                                            return "%d FE/%d FE".formatted(component.getEnergy(), component.getCapacity());
                                        },
                                        frames
                                ),
                                new SubMenuWidget(
                                        energyConfigMenu,
                                        27,
                                        slotsIcon
                                ),
                                new ItemSlotWidget(
                                        10,
                                        dummySlotAccessor
                                ),
                                new ItemSlotWidget(
                                        16,
                                        dummySlotAccessor
                                )
                        )
                )
        ));

        List<ItemStack> verticalIndicatorFrames = new ArrayList<>();

        for (int i = 0; i < 49; i++) {
            ItemStack item = ItemStack.of(Material.PAPER);
            CustomModelData customModelData = CustomModelData.customModelData()
                    .addString(Integer.toString(i))
                    .build();
            item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
            item.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "vertical_energy_indicator"));

            verticalIndicatorFrames.add(item);
        }

        List<ItemStack> infusionFrames = new ArrayList<>();

        for (InfusionType infusionType : InfusionType.values()) {
            for (int i = 0; i < 49; i++) {
                ItemStack item = ItemStack.of(Material.PAPER);
                CustomModelData customModelData = CustomModelData.customModelData()
                        .addString(Integer.toString(i))
                        .build();
                item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
                item.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "infuser_indicator/" + infusionType.name().toLowerCase()));

                infusionFrames.add(item);
            }
        }

        List<ItemStack> infuserArrowIndicator = new ArrayList<>();
        for (int i = 0; i <= 32; i++) {
            ItemStack item = ItemStack.of(Material.PAPER);
            CustomModelData customModelData = CustomModelData.customModelData()
                    .addString(Integer.toString(i))
                    .build();
            item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
            item.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "infuser_arrow_indicator"));

            infuserArrowIndicator.add(item);
        }

        registry.register(MekanismBlockType.SOLAR_GENERATOR, new BlockDefinition(
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
                                new EnergyStorage(3840, 0, 20, 0),
                                20
                        )
                ),
                new SolarGeneratorHandler(),
                new MenuDefinition(
                        "<font:mekanism:spaces>\uFF08</font><font:mekanism:menu_titles>\uFF01</font>",
                        4,
                        List.of(
                                new MultiSlotIndicatorWidget(
                                        List.of(8, 17, 26),
                                        26,
                                        3,
                                        instance -> {
                                            EnergyComponent component = instance.get(EnergyComponent.class).orElseThrow();
                                            return 1.0 * component.getEnergy() / component.getCapacity();
                                        },
                                        instance -> {
                                            EnergyComponent component = instance.get(EnergyComponent.class).orElseThrow();
                                            return "%d FE".formatted(component.getEnergy());
                                        },
                                        verticalIndicatorFrames
                                ),
                                new ItemSlotWidget(2, dummySlotAccessor),
                                new ItemSlotWidget(3, dummySlotAccessor),
                                new ItemSlotWidget(4, dummySlotAccessor),
                                new ItemSlotWidget(5, dummySlotAccessor),
                                new ItemSlotWidget(6, dummySlotAccessor),

                                new ItemSlotWidget(11, dummySlotAccessor),
                                new ItemSlotWidget(12, dummySlotAccessor),
                                new ItemSlotWidget(13, dummySlotAccessor),
                                new ItemSlotWidget(14, dummySlotAccessor),
                                new ItemSlotWidget(15, dummySlotAccessor),

                                new ItemSlotWidget(20, dummySlotAccessor),
                                new ItemSlotWidget(21, dummySlotAccessor),
                                new ItemSlotWidget(22, dummySlotAccessor),
                                new ItemSlotWidget(23, dummySlotAccessor),
                                new ItemSlotWidget(24, dummySlotAccessor)
                        )
                )
        ));

        registry.register(MekanismBlockType.METALLURGIC_INFUSER, new BlockDefinition(
                Material.BARRIER,
                null,
                "metallurgic_infuser",
                fullBlockTransformation,
                List.of(
                        block -> new EnergyComponent(
                                portsSupplier.get(),
                                new EnergyStorage(
                                        8000, 100, 100, 0
                                )
                        ),
                        block -> new InfuserComponent(
                                portsSupplier.get(),
                                new InfusionStorage(
                                        null, 0, 1000
                                ),
                                infusionTypeRegistry,
                                recipeRegistry
                        )
                ),
                new MetallurgicInfuserHandler(),
                new MenuDefinition(
                        "<font:mekanism:spaces>\uFF08</font><font:mekanism:menu_titles>\uFF03</font>",
                        4,
                        List.of(
                                new ItemSlotSupplierWidget(
                                        10,
                                        instance -> {
                                            InfuserComponent component = instance.get(InfuserComponent.class).orElseThrow();
                                            return new InfusionSlotAccessor(component);
                                        }
                                ),
                                new ItemSlotSupplierWidget(
                                        20,
                                        instance -> {
                                            InfuserComponent component = instance.get(InfuserComponent.class).orElseThrow();
                                            return new InfuserMainSlotAccessor(component);
                                        }
                                ),
                                new ItemSlotSupplierWidget(
                                        24,
                                        instance -> {
                                            InfuserComponent component = instance.get(InfuserComponent.class).orElseThrow();
                                            return new InfuserOutputSlotAccessor(component);
                                        }
                                ),
                                new MultiSlotIndicatorWidget(
                                        List.of(8, 17, 26),
                                        26,
                                        3,
                                        instance -> {
                                            EnergyComponent component = instance.get(EnergyComponent.class).orElseThrow();
                                            return 1.0 * component.getEnergy() / component.getCapacity();
                                        },
                                        instance -> {
                                            EnergyComponent component = instance.get(EnergyComponent.class).orElseThrow();
                                            return "%d FE".formatted(component.getEnergy());
                                        },
                                        verticalIndicatorFrames
                                ),
                                new MultiSlotIndicatorWidget(
                                        List.of(0, 9, 18),
                                        18,
                                        3,
                                        instance -> {
                                            InfuserComponent component = instance.get(InfuserComponent.class).orElseThrow();

                                            if (component.getType() == null) {
                                                return 0.0;
                                            }

                                            double offset = switch (component.getType()) {
                                                case CARBON -> 0;
                                                case REDSTONE -> 0.25;
                                                case DIAMOND -> 0.50;
                                                case GOLD -> 0.75;
                                            };




                                            // 0 - 25%    = carbon
                                            // 25% - 50%  = redstone
                                            // 50% - 75%  = diamond
                                            // 75% - 100% = gold

                                            double pct = 0.2499 * component.getAmount() / component.getCapacity();
                                            return offset + pct;
                                        },
                                        instance -> {
                                            InfuserComponent component = instance.get(InfuserComponent.class).orElseThrow();
                                            if (component.getType() == null) {
                                                return "Empty";
                                            }
                                            return "%d %s".formatted(component.getAmount(), StringUtils.capitalizeFirst(component.getType().name().toLowerCase()));
                                        },
                                        infusionFrames
                                ),
                                new MultiSlotIndicatorWidget(
                                        List.of(21, 22, 23),
                                        23,
                                        1,
                                        instance -> {
                                            InfuserComponent component = instance.get(InfuserComponent.class).orElseThrow();
                                            if (component.getMaxProgress() == 0) return 0.0;
                                            return ((double) component.getProgress()) / component.getMaxProgress();
                                        },
                                        instance -> {
                                            InfuserComponent component = instance.get(InfuserComponent.class).orElseThrow();
                                            if (component.getMaxProgress() == 0) return "0%";
                                            return 100.0 * component.getProgress() / component.getMaxProgress() + "%";
                                        },
                                        infuserArrowIndicator
                                )
                        )
                )
        ));
    }

    @Override
    public void onEnable() {
        MekanismKeys.init(this);

        menuService = new MenuService();

        recipeRegistry = new RecipeRegistry();
        BlockRegistry blockRegistry = new BlockRegistry();
        BlockInstanceService blockInstanceService = new BlockInstanceService(blockRegistry);
        blockPersistenceService = new BlockPersistenceService(
                getSLF4JLogger(),
                this,
                blockInstanceService
        );
        infusionTypeRegistry = new InfusionTypeRegistry();
        infusionTypeRegistry.register(new InfusionMapping(new MaterialMatcher(Material.COAL), InfusionType.CARBON, 10));
        infusionTypeRegistry.register(new InfusionMapping(new MaterialMatcher(Material.REDSTONE), InfusionType.REDSTONE, 10));

        BlockTickService blockTickService = new BlockTickService(blockPersistenceService);
        networkService = new NetworkService(getSLF4JLogger(), blockPersistenceService);
        BlockService blockService = new BlockService(blockRegistry, blockPersistenceService);

        blockFacade = new BlockFacade(
                blockService,
                networkService,
                blockTickService,
                blockInstanceService,
                blockRegistry,
                menuService
        );

        registerBlocks(menuService, blockFacade, blockRegistry);

        Bukkit.getPluginManager()
                .registerEvents(new BlockListener(blockFacade), this);
        Bukkit.getPluginManager()
                .registerEvents(new InventoryListener(menuService), this);
        Bukkit.getPluginManager()
                .registerEvents(new ChunkListener(this, blockService, blockRegistry, networkService, blockInstanceService, blockTickService), this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                () -> {
                    blockTickService.tick();
                    networkService.tick();
                    menuService.tickOpenMenus();
                },
                0L,
                1
        );

        Bukkit.getScheduler().runTaskTimer(
                this,
                blockPersistenceService::periodicSave,
                10L,
                20L * 30
        );

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register("debug", new DebugCommand(networkService, blockInstanceService));
            commands.registrar().register("mgive", new GiveCommand(blockRegistry));
            commands.registrar().register("mtest", new TestCommand(this, blockRegistry, menuService));
            commands.registrar().register("msave", new SaveCommand(blockPersistenceService));
        });
    }

    @Override
    public void onDisable() {
        blockPersistenceService.saveAll();
    }
}
