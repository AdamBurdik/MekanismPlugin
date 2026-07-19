package me.adamix.mekanism.menu;

import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.menu.widget.ButtonWidget;
import me.adamix.mekanism.menu.widget.IndicatorWidget;
import me.adamix.mekanism.menu.widget.ItemSlotWidget;
import me.adamix.mekanism.menu.widget.WidgetDefinition;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MenuService {
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Map<UUID, OpenMenuContext> openMenus = new HashMap<>();

    public void open(
            @NotNull Player player,
            @NotNull Location location,
            @NotNull MenuDefinition definition,
            @NotNull BlockInstance instance
    ) {
        Inventory inv = Bukkit.createInventory(null, definition.rows() * 9,
                mm.deserialize(definition.title())
                        .color(TextColor.color(0xFFFFFF))
        );

        for (WidgetDefinition widget : definition.widgets()) {
            render(inv, widget, instance);
        }

        openMenus.put(player.getUniqueId(), new OpenMenuContext(location, definition, instance, inv));
        player.openInventory(inv);
    }

    public void update(@NotNull Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();

        var ctx = openMenus.get(player.getUniqueId());

        for (WidgetDefinition widget : ctx.definition().widgets()) {
            render(inventory, widget, ctx.instance());
        }
    }

    private void render(
            @NotNull Inventory inventory,
            @NotNull WidgetDefinition widget,
            @NotNull BlockInstance instance
    ) {
        switch (widget) {
            case IndicatorWidget indicator -> renderIndicator(inventory, indicator, instance);
            case ButtonWidget buttonWidget -> {
            }
            case ItemSlotWidget itemSlotWidget -> {
            }
        }
    }

    private void renderIndicator(
            @NotNull Inventory inventory,
            @NotNull IndicatorWidget widget,
            @NotNull BlockInstance instance
    ) {
        double value = widget.valueProvider().apply(instance); // 0.0 - 1.0
        //
        //
        //    66.6 / 3
        //
        //    20
        //
        //


        int totalSlots = widget.slots().size();
        int rows = widget.rowCount();
        int columns = totalSlots / rows;
        double perSlot = 1.0 / rows;

        double current = value;

        for (int y = rows - 1; y >= 0; y--) {

            double pct = current / perSlot;
            pct = Math.clamp(pct, 0.0, 1.0);

            current -= perSlot;
            current = Math.clamp(current, 0, 1);

            for (int x = 0; x < columns; x++) {

                int i = y * columns + x;
                var frames = widget.frames().get(i);

                int frameIndex = (int) Math.round(pct * (frames.size() - 1));

                ItemStack item = frames.get(frameIndex);

                if (widget.labelProvider() != null) {
                    item.editMeta(meta -> {
                        meta.customName(
                                mm.deserialize(widget.labelProvider().apply(instance))
                        );
                    });
                }

                inventory.setItem(widget.slots().get(i), item);
            }
        }
//
//        double filledSlotsExact = value * totalSlots; // kolik slotů "celkem" má být vyplněných
//
//        for (int i = 0; i < totalSlots; i++) {
//
//
//
//
//            double fillRatio = Math.clamp(filledSlotsExact - i, 0.0, 1.0);
//
//            List<ItemStack> frames = widget.frames().get(i);
//            int frameIndex = (int) Math.round(fillRatio * (frames.size() - 1));
//
//            inventory.setItem(widget.slots().get(i), frames.get(frameIndex));
//        }
    }


    public void tickOpenMenus() {
        Iterator<Map.Entry<UUID, OpenMenuContext>> iterator = openMenus.entrySet().iterator();

        while (iterator.hasNext()) {
            var entry = iterator.next();
            Player player = Bukkit.getPlayer(entry.getKey());

            if (player == null) {
                iterator.remove();
                continue;
            }

            OpenMenuContext context = entry.getValue();
            InventoryView view = player.getOpenInventory();
            Inventory topInventory = view.getTopInventory();

            if (topInventory != context.inventory()) {
                iterator.remove();
                continue;
            }

            for (WidgetDefinition widget : context.definition().widgets()) {
                if (widget instanceof IndicatorWidget indicator) {
                    render(topInventory, indicator, context.instance());
                }
            }
        }
    }
}