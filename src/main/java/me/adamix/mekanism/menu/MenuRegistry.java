package me.adamix.mekanism.menu;

import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.adamix.mekanism.block.BlockFacade;
import me.adamix.mekanism.block.component.InfuserComponent;
import me.adamix.mekanism.block.component.SmelterComponent;
import me.adamix.mekanism.block.component.item.GenericSlotsComponent;
import me.adamix.mekanism.block.component.item.ItemComponent;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.item.ItemRegistry;
import me.adamix.mekanism.item.SlotType;
import me.adamix.mekanism.menu.widget.ButtonIndicatorWidget;
import me.adamix.mekanism.menu.widget.ButtonWidget;
import me.adamix.mekanism.menu.widget.EmptySlotsWidget;
import me.adamix.mekanism.menu.widget.IndicatorWidget;
import me.adamix.mekanism.menu.widget.ItemSlotSupplierWidget;
import me.adamix.mekanism.menu.widget.ItemSlotWidget;
import me.adamix.mekanism.menu.widget.MultiSlotIndicatorWidget;
import me.adamix.mekanism.menu.widget.SlotAccessor;
import me.adamix.mekanism.menu.widget.SubMenuWidget;
import me.adamix.mekanism.menu.widget.slot.GenericMainSlotAccessor;
import me.adamix.mekanism.menu.widget.slot.GenericOutputSlotAccessor;
import me.adamix.mekanism.network.NetworkService;
import me.adamix.mekanism.network.NetworkType;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.translation.Translations;
import me.adamix.mekanism.translation.Translations.Spaces;
import me.adamix.mekanism.translation.Translations.Titles;
import me.adamix.mekanism.type.RelativeFace;
import me.adamix.utils.StringUtils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Getter
@Accessors(fluent = true)
public class MenuRegistry {
    private final MenuService menuService;
    private final NetworkService networkService;
    private final BlockFacade blockFacade;

    private final @NotNull ItemRegistry itemRegistry;
    private final Translations translations;

    // Actual menus
    private final MenuDefinition
            energyConfig,
            energizedSmelter,
            metallurgicInfuser,
            solarGenerator,
            energyCube;

    public MenuRegistry(
            MenuService menuService,
            NetworkService networkService,
            BlockFacade blockFacade,
            @NotNull ItemRegistry itemRegistry,
            Translations translations
    ) {
        this.menuService = menuService;
        this.networkService = networkService;
        this.blockFacade = blockFacade;
        this.itemRegistry = itemRegistry;
        this.translations = translations;

        energyConfig = new MenuDefinition(
                translations.menuTitle(Spaces.NEG_8, Titles.ENERGY_CONFIG),
                4,
                List.of(
                        new ButtonWidget(0, itemRegistry.closeIcon(), menuService::closeSubmenu),
                        slotIndicatorWidget(13, RelativeFace.TOP, NetworkType.ENERGY),
                        slotIndicatorWidget(21, RelativeFace.RIGHT, NetworkType.ENERGY),
                        slotIndicatorWidget(22, RelativeFace.FRONT, NetworkType.ENERGY),
                        slotIndicatorWidget(23, RelativeFace.LEFT, NetworkType.ENERGY),
                        slotIndicatorWidget(30, RelativeFace.BACK, NetworkType.ENERGY),
                        slotIndicatorWidget(31, RelativeFace.BOTTOM, NetworkType.ENERGY),
                        emptySlot(2),
                        emptySlot(3),
                        emptySlot(4),
                        emptySlot(5),
                        emptySlot(6),
                        new ButtonWidget(8, itemRegistry.ejectIcon(), _ -> {
                        })
//                    new ButtonIndicatorWidget(
//                            35,
//                            this::clearSlots,
//                            instance -> "Clear Slots",
//                            instance -> clearSlotsIcon
//                    )
                )
        );

        energizedSmelter = new MenuDefinition(
                translations.menuTitle(Spaces.NEG_8, Titles.ENERGIZED_SMELTER),
                4,
                List.of(
                        new EmptySlotsWidget(12, 21),
                        mainSlotWidget(3),
                        outputSlotWidget(15),
                        smelterIndicatorWidget(List.of(13, 14), 14, 1),
                        energyIndicatorWidget(List.of(8, 17, 26), 26, 3),
                        sideConfigWidget(27)
                )
        );

        metallurgicInfuser = new MenuDefinition(
                translations.menuTitle(Spaces.NEG_8, Titles.METALLURGIC_INFUSER),
                4,
                List.of(
                        new ItemSlotSupplierWidget(10, instance -> {
                            GenericSlotsComponent component = instance.get(GenericSlotsComponent.class).orElseThrow();
                            return new GenericMainSlotAccessor(component);
                        }),
                        mainSlotWidget(20),
                        outputSlotWidget(24),
                        energyIndicatorWidget(List.of(8, 17, 26), 26, 3),
                        infusingIndicatorWidget(List.of(0, 9, 18), 18, 3),
                        new MultiSlotIndicatorWidget(List.of(21, 22, 23), 23, 1,
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
                                itemRegistry.infuserArrowIndicatorFrames()
                        ),
                        sideConfigWidget(27)
                )
        );

        solarGenerator = new MenuDefinition(
                translations.menuTitle(Spaces.NEG_8, Titles.SOLAR_GENERATOR),
                4,
                List.of(
                        energyIndicatorWidget(List.of(8, 17, 26), 26, 3),
                        emptySlot(2),
                        emptySlot(3),
                        emptySlot(4),
                        emptySlot(5),
                        emptySlot(6),

                        emptySlot(11),
                        emptySlot(12),
                        emptySlot(13),
                        emptySlot(14),
                        emptySlot(15),

                        emptySlot(20),
                        emptySlot(21),
                        emptySlot(22),
                        emptySlot(23),
                        emptySlot(24)
                )
        );

        energyCube = new MenuDefinition(
                translations.menuTitle(Spaces.NEG_8, Titles.ENERGY_CUBE),
                4,
                List.of(
                        new IndicatorWidget(
                                List.of(
                                        2, 3, 4, 5, 6,
                                        11, 12, 13, 14, 15,
                                        20, 21, 22, 23, 24
                                ),
                                3,
                                instance -> {
                                    EnergyComponent component = instance.get(EnergyComponent.class).orElseThrow();
                                    return 1.0 * component.getEnergy() / component.getCapacity();
                                },
                                instance -> {
                                    EnergyComponent component = instance.get(EnergyComponent.class).orElseThrow();
                                    return "%d FE/%d FE".formatted(component.getEnergy(), component.getCapacity());
                                },
                                itemRegistry.energyCubeIndicatorFrames()
                        ),
                        sideConfigWidget(27),
                        emptySlot(10),
                        emptySlot(16)
                )
        );
    }


    private final SlotAccessor dummySlotAccessor = new SlotAccessor() {
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

    private @NotNull ItemSlotWidget emptySlot(int slot) {
        return new ItemSlotWidget(slot, dummySlotAccessor);
    }

    private final BiFunction<String, SlotType, ItemStack> slotIndicatorSupplier = (name, type) -> {
        var itemStack = ItemStack.of(Material.PAPER);
        itemStack.editMeta(meta -> {
            meta.customName(Component.text(name));
        });
        itemStack.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "slots/" + type.name().toLowerCase()));

        return itemStack;
    };

    private final Map<PortType, ItemStack> portToSlotIndicatorItem = Map.of(
            PortType.INPUT, slotIndicatorSupplier.apply("Input", SlotType.BLUE),
            PortType.OUTPUT, slotIndicatorSupplier.apply("Output", SlotType.DARK_RED),
            PortType.BOTH, slotIndicatorSupplier.apply("Both", SlotType.PURPLE),
            PortType.DISABLED, slotIndicatorSupplier.apply("None", SlotType.NONE)
    );

    private @NotNull ButtonIndicatorWidget slotIndicatorWidget(
            int slot,
            @NotNull RelativeFace relativeFace,
            @NotNull NetworkType networkType
    ) {
        return new ButtonIndicatorWidget(
                slot,
                (player, instance) -> {
                    var face = relativeFace.toWorldFace(instance.getFacing());

                    Map<BlockFace, PortType> ports = switch (networkType) {
                        case ENERGY -> instance.get(EnergyComponent.class).orElseThrow().getPorts();
                        case ITEM -> instance.get(ItemComponent.class).orElseThrow().getPorts();
                    };

                    PortType current = ports.get(face);
                    PortType next = switch (current) {
                        case INPUT -> PortType.OUTPUT;
                        case OUTPUT -> PortType.BOTH;
                        case BOTH -> PortType.DISABLED;
                        case DISABLED -> PortType.INPUT;
                        case null -> PortType.INPUT;
                    };

                    ports.put(face, next);

                    networkService.unregisterPort(
                            instance.getPos(),
                            face
                    );
                    if (next != PortType.DISABLED) {
                        networkService.registerPorts(
                                instance.getPos(),
                                NetworkType.ENERGY,
                                instance,
                                ports
                        );
                    }
                    blockFacade.updateBlock(instance.getPos().resolveBlock());
                    blockFacade.updateSurroundings(instance.getPos().resolveBlock());
                    menuService.update(player);
                },
                instance -> {
                    String side = StringUtils.capitalizeFirst(relativeFace.name().toLowerCase());

                    Map<BlockFace, PortType> ports = switch (networkType) {
                        case ENERGY -> instance.get(EnergyComponent.class).orElseThrow().getPorts();
                        case ITEM -> instance.get(ItemComponent.class).orElseThrow().getPorts();
                    };

                    var face = relativeFace.toWorldFace(instance.getFacing());

                    PortType portType = ports.get(face);
                    String portName = switch (portType) {
                        case INPUT -> "Input";
                        case OUTPUT -> "Output";
                        case DISABLED -> "None";
                        case BOTH -> "Both";
                    };

                    String slotTypeName = switch (portType) {
                        case INPUT -> "Blue";
                        case OUTPUT -> "Dark Red";
                        case DISABLED -> "Light Gray";
                        case BOTH -> "Blue";
                    };

                    return portName + " (" + slotTypeName + ") (" + side + ")";
                },
                instance -> {
                    Map<BlockFace, PortType> ports = switch (networkType) {
                        case ENERGY -> instance.get(EnergyComponent.class).orElseThrow().getPorts();
                        case ITEM -> instance.get(ItemComponent.class).orElseThrow().getPorts();
                    };

                    var face = relativeFace.toWorldFace(instance.getFacing());

                    PortType portType = ports.get(face);
                    return portToSlotIndicatorItem.get(portType);
                }
        );
    }


    private @NotNull ItemSlotSupplierWidget mainSlotWidget(int slot) {
        return new ItemSlotSupplierWidget(slot, instance -> {
            GenericSlotsComponent component = instance.get(GenericSlotsComponent.class).orElseThrow();
            return new GenericMainSlotAccessor(component);
        });
    }

    private @NotNull ItemSlotSupplierWidget outputSlotWidget(int slot) {
        return new ItemSlotSupplierWidget(slot, instance -> {
            GenericSlotsComponent component = instance.get(GenericSlotsComponent.class).orElseThrow();
            return new GenericOutputSlotAccessor(component);
        });
    }

    private @NotNull MultiSlotIndicatorWidget energyIndicatorWidget(
            @NotNull List<Integer> slots, int sourceSlot, int rowCount
    ) {
        return new MultiSlotIndicatorWidget(
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
                itemRegistry.verticalEnergyIndicatorFrames()
        );
    }

    private @NotNull MultiSlotIndicatorWidget smelterIndicatorWidget(
            @NotNull List<Integer> slots, int sourceSlot, int rowCount
    ) {
        return new MultiSlotIndicatorWidget(
                slots,
                sourceSlot,
                rowCount,
                instance -> {
                    SmelterComponent component = instance.get(SmelterComponent.class).orElseThrow();
                    if (component.getMaxProgress() == 0) return 0.0;
                    return 1.0 * component.getProgress() / component.getMaxProgress();
                },
                instance -> {
                    SmelterComponent component = instance.get(SmelterComponent.class).orElseThrow();
                    if (component.getMaxProgress() == 0) return "0%";
                    return 100.0 * component.getProgress() / component.getMaxProgress() + "%";
                },
                itemRegistry.thickIndicatorFrames()
        );
    }

    private @NotNull MultiSlotIndicatorWidget infusingIndicatorWidget(
            @NotNull List<Integer> slots, int sourceSlot, int rowCount
    ) {
        return new MultiSlotIndicatorWidget(
                slots,
                sourceSlot,
                rowCount,
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

                    // We dont want it to each 25%, so there-for 0.2499. Really stupid I know
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
                itemRegistry.infusingIndicatorFrames()
        );
    }

    private @NotNull SubMenuWidget sideConfigWidget(
            int slot
    ) {
        return new SubMenuWidget(
                this.energyConfig,
                slot,
                itemRegistry.sideConfigIcon()
        );
    }
}
