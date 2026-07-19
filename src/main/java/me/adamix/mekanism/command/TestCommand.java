package me.adamix.mekanism.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.Mekanism;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.block.handler.UniversalCableHandler;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.energy.EnergyStorage;
import me.adamix.mekanism.menu.MenuDefinition;
import me.adamix.mekanism.menu.MenuService;
import me.adamix.mekanism.menu.widget.IndicatorWidget;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.type.BlockPos;
import me.adamix.mekanism.type.WorldPos;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class TestCommand implements BasicCommand {
    private final Mekanism plugin;
    private final BlockRegistry blockRegistry;
    private final MenuService menuService;

    private BukkitTask task;

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) return;

        switch (args[0]) {
            case "energy_indicator" -> testEnergyIndicator(player, args);
        }
    }

    private void testEnergyIndicator(@NotNull Player player, String[] args) {
        Transformation fullBlockTransformation = new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf(0, 0, 0, 1f),
                new Vector3f(1f, 1f, 1f),
                new Quaternionf(0, 0, 0, 1f)
        );

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

        // 100          capacity
        // x            energy

        long period = Long.parseLong(args[1]);
        long increment = Long.parseLong(args[2]);
        long limit = Long.parseLong(args[3]);
        long current = Long.parseLong(args[4]);

        long energy = current < 0 ? 0 : current;

        EnergyComponent energyComponent = new EnergyComponent(
                ports,
                new EnergyStorage(1000, 100, 100, energy)
        );

        var menu = new MenuDefinition(
                "<font:mekanism:spaces>\uFF08</font><font:mekanism:menu_titles>\uFF00</font>",
                4,
                List.of(
                        new IndicatorWidget(
                                List.of(array),
                                3,
                                instance -> 1.0 * energyComponent.getEnergy() / energyComponent.getCapacity(),
                                instance -> "%d FE".formatted(energyComponent.getEnergy()),
                                frames
                        )
                )
        );

        blockRegistry.register(MekanismBlockType.TEST_BLOCK, new BlockDefinition(
                Material.BARRIER,
                null,
                "basic_energy_cube",
                fullBlockTransformation,
                List.of(
                        block -> energyComponent
                ),
                new UniversalCableHandler(),
                menu
        ));

        menuService.open(
                player,
                new WorldPos(
                        player.getLocation().getWorld().getName(),
                        new BlockPos(0, 0, 0)
                ),
                menu,
                new BlockInstance()
        );

        AtomicInteger i = new AtomicInteger();

        if (current < 0) {
            task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                try {
                    energyComponent.insert(increment, false);
                    menuService.update(player);

                    if (i.getAndIncrement() > limit) {
                        task.cancel();
                    }
                } catch (Exception e) {
                    task.cancel();
                }
            }, 10L, period);
        }
    }
}
