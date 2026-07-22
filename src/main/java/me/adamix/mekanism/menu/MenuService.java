package me.adamix.mekanism.menu;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.menu.widget.ButtonIndicatorWidget;
import me.adamix.mekanism.menu.widget.ButtonWidget;
import me.adamix.mekanism.menu.widget.IndicatorWidget;
import me.adamix.mekanism.menu.widget.ItemSlotSupplierWidget;
import me.adamix.mekanism.menu.widget.ItemSlotWidget;
import me.adamix.mekanism.menu.widget.MultiSlotIndicatorWidget;
import me.adamix.mekanism.menu.widget.SlotAccessor;
import me.adamix.mekanism.menu.widget.SubMenuWidget;
import me.adamix.mekanism.menu.widget.WidgetDefinition;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

public class MenuService {
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Map<UUID, OpenMenuContext> openMenus = new HashMap<>();
    private static final ItemStack emptyIcon = ItemStack.of(Material.PAPER);

    {
        emptyIcon.editMeta(meta -> meta.setHideTooltip(true));
        emptyIcon.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "empty_icon"));
        emptyIcon.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay()
                .hideTooltip(true)
                .build());
    }

    public void open(
            @NotNull Player player,
            @NotNull MenuDefinition definition,
            @NotNull BlockInstance instance
    ) {
        Inventory inv = Bukkit.createInventory(null, definition.rows() * 9,
                mm.deserialize(definition.title())
                        .color(TextColor.color(0xFFFFFF))
        );

        Set<Integer> occupiedSlots = new HashSet<>();

        for (WidgetDefinition widget : definition.widgets()) {
            render(inv, widget, instance, occupiedSlots);
        }

        for (int i = 0; i < inv.getSize(); i++) {
            if (!occupiedSlots.contains(i)) {
                inv.setItem(i, emptyIcon);
            }
        }

        Stack<MenuDefinition> stack = new Stack<>();
        stack.push(definition);

        openMenus.put(
                player.getUniqueId(),
                new OpenMenuContext(
                        definition,
                        instance,
                        inv,
                        stack
                )
        );
        player.openInventory(inv);
    }

    public void openSubmenu(
            @NotNull Player player,
            @NotNull OpenMenuContext ctx,
            @NotNull MenuDefinition submenu
    ) {
        Inventory inv = Bukkit.createInventory(null, submenu.rows() * 9,
                mm.deserialize(submenu.title())
                        .color(TextColor.color(0xFFFFFF))
        );

        Set<Integer> occupiedSlots = new HashSet<>();

        for (WidgetDefinition widget : submenu.widgets()) {
            render(inv, widget, ctx.instance(), occupiedSlots);
        }

        for (int i = 0; i < inv.getSize(); i++) {
            if (!occupiedSlots.contains(i)) {
                inv.setItem(i, emptyIcon);
            }
        }

        ctx.stack().push(submenu);

        openMenus.put(
                player.getUniqueId(),
                new OpenMenuContext(
                        submenu,
                        ctx.instance(),
                        inv,
                        ctx.stack()
                )
        );

        player.openInventory(inv);
    }

    public void closeSubmenu(@NotNull Player player) {
        OpenMenuContext ctx = openMenus.get(player.getUniqueId());
        if (ctx == null) return;

        ctx.stack().pop();
        if (ctx.stack().isEmpty()) {
            openMenus.remove(player.getUniqueId());
        }

        MenuDefinition newMenu = ctx.stack().lastElement();

        Inventory inv = Bukkit.createInventory(null, newMenu.rows() * 9,
                mm.deserialize(newMenu.title())
                        .color(TextColor.color(0xFFFFFF))
        );

        Set<Integer> occupiedSlots = new HashSet<>();

        for (WidgetDefinition widget : newMenu.widgets()) {
            render(inv, widget, ctx.instance(), occupiedSlots);
        }

        for (int i = 0; i < inv.getSize(); i++) {
            if (!occupiedSlots.contains(i)) {
                inv.setItem(i, emptyIcon);
            }
        }

        openMenus.put(
                player.getUniqueId(),
                new OpenMenuContext(
                        newMenu,
                        ctx.instance(),
                        inv,
                        ctx.stack()
                )
        );

        player.openInventory(inv);
    }

    public void update(@NotNull Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();

        var ctx = openMenus.get(player.getUniqueId());

        Set<Integer> occupiedSlots = new HashSet<>();

        for (WidgetDefinition widget : ctx.definition().widgets()) {
            render(inventory, widget, ctx.instance(), occupiedSlots);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            if (!occupiedSlots.contains(i)) {
                inventory.setItem(i, emptyIcon);
            }
        }
    }

    private void render(
            @NotNull Inventory inventory,
            @NotNull WidgetDefinition widget,
            @NotNull BlockInstance instance,
            Set<Integer> occupiedSlots
    ) {
        switch (widget) {
            case IndicatorWidget indicator -> {
                renderIndicator(inventory, indicator, instance);
                occupiedSlots.addAll(indicator.slots());
            }
            case MultiSlotIndicatorWidget indicator -> {
                renderMultiSlotIndicator(inventory, indicator, instance);
                occupiedSlots.addAll(indicator.slots());
            }
            case ButtonWidget button -> {
                inventory.setItem(button.slot(), button.icon());
                occupiedSlots.add(button.slot());
            }
            case ItemSlotWidget item -> {
                occupiedSlots.add(item.slot());
                var itemStack = item.slotAccessor().get();
                if (itemStack == null) itemStack = ItemStack.of(Material.AIR);
                inventory.setItem(item.slot(), itemStack);
            }
            case SubMenuWidget submenu -> {
                inventory.setItem(submenu.slot(), submenu.icon());
                occupiedSlots.add(submenu.slot());
            }
            case ButtonIndicatorWidget button -> {
                ItemStack item = button.iconProvider().apply(instance);
                if (button.labelProvider() != null) {
                    item.editMeta(meta -> {
                        meta.customName(
                                mm.deserialize(button.labelProvider().apply(instance))
                        );
                    });
                }

                inventory.setItem(button.slot(), item);
                occupiedSlots.add(button.slot());
            }
            case ItemSlotSupplierWidget item -> {
                occupiedSlots.add(item.slot());
                var itemStack = item.slotAccessor()
                        .apply(instance)
                        .get();
                if (itemStack == null) itemStack = ItemStack.of(Material.AIR);
                inventory.setItem(item.slot(), itemStack);
            }
        }
    }

    private void renderMultiSlotIndicator(
            @NotNull Inventory inventory,
            @NotNull MultiSlotIndicatorWidget widget,
            @NotNull BlockInstance instance
    ) {
        double value = widget.valueProvider().apply(instance); // 0.0 - 1.0

        int i = 0;
        for (int slot : widget.slots()) {
            ItemStack item;
            if (slot == widget.sourceSlot()) {
                var frames = widget.frames();

                int frameIndex = (int) Math.round(value * (frames.size() - 1));

                item = frames.get(frameIndex);
            } else {
                item = ItemStack.of(Material.PAPER);
            }

            if (widget.labelProvider() != null) {
                item.editMeta(meta -> {
                    meta.customName(
                            mm.deserialize(widget.labelProvider().apply(instance))
                    );
                });
            }

            inventory.setItem(slot, item);

            i++;
        }
    }

    private void renderIndicator(
            @NotNull Inventory inventory,
            @NotNull IndicatorWidget widget,
            @NotNull BlockInstance instance
    ) {
        double value = widget.valueProvider().apply(instance); // 0.0 - 1.0
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
                    render(topInventory, indicator, context.instance(), new HashSet<>());
                } else if (widget instanceof MultiSlotIndicatorWidget indicator) {
                    render(topInventory, indicator, context.instance(), new HashSet<>());
                } else if (widget instanceof ItemSlotWidget item) {
                    render(topInventory, item, context.instance(), new HashSet<>());
                } else if (widget instanceof ItemSlotSupplierWidget item) {
                    render(topInventory, item, context.instance(), new HashSet<>());
                }
            }
        }
    }

    public void onItemClick(@NotNull InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        OpenMenuContext ctx = openMenus.get(player.getUniqueId());
        if (ctx == null) return;

        if (!event.getClickedInventory().equals(ctx.inventory())) {
            return;
        }

        WidgetDefinition widget = findWidgetAt(slot, ctx.definition());
        if (widget == null) {
            event.setCancelled(true);
            return;
        }

        switch (widget) {
            case ButtonWidget button -> {
                event.setCancelled(true);
                button.onClick().accept(player);
            }
            case IndicatorWidget indicator -> event.setCancelled(true);
            case MultiSlotIndicatorWidget indicator -> event.setCancelled(true);
            case SubMenuWidget submenu -> {
                event.setCancelled(true);
                openSubmenu(player, ctx, submenu.subMenu());
            }
            case ItemSlotWidget item -> {
                ItemStack cursor = event.getCursor();
                ItemStack current = item.slotAccessor().get();

                boolean shiftClick = event.isShiftClick();
                boolean takingOut = cursor.getType().isAir();

                if (takingOut) {
                    item.slotAccessor().set(null);
                    event.setCurrentItem(null);
                    if (current != null) {
                        player.getInventory().addItem(current);
                    }
                } else {
                    if (!item.slotAccessor().canAccept(cursor)) {
                        event.setCancelled(true);
                        return;
                    }
                    item.slotAccessor().set(cursor.clone());
                    event.setCurrentItem(cursor.clone());
                    player.setItemOnCursor(null);
                }

                event.setCancelled(true);
            }
            case ButtonIndicatorWidget button -> {
                event.setCancelled(true);
                button.onClick().accept(player, ctx.instance());
            }
            case ItemSlotSupplierWidget item -> {
                ItemStack cursor = event.getCursor();
                SlotAccessor accessor = item.slotAccessor().apply(ctx.instance());
                ItemStack current = accessor.get();

                boolean shiftClick = event.isShiftClick();
                boolean takingOut = cursor.getType().isAir();

                if (takingOut) {
                    accessor.set(null);
                    event.setCurrentItem(null);
                    if (current != null) {
                        player.getInventory().addItem(current);
                    }
                } else {
                    if (!accessor.canAccept(cursor)) {
                        event.setCancelled(true);
                        return;
                    }
                    accessor.set(cursor.clone());
                    event.setCurrentItem(cursor.clone());
                    player.setItemOnCursor(null);
                }

                event.setCancelled(true);
            }
        }
    }

    private @Nullable WidgetDefinition findWidgetAt(int slot, @NotNull MenuDefinition menu) {
        for (WidgetDefinition widget : menu.widgets()) {
            switch (widget) {
                case ButtonWidget button -> {
                    if (button.slot() == slot) return button;
                }
                case IndicatorWidget indicator -> {
                    for (int possibleSlot : indicator.slots()) {
                        if (possibleSlot == slot) return indicator;
                    }
                }
                case ItemSlotWidget item -> {
                    if (item.slot() == slot) return item;
                }
                case MultiSlotIndicatorWidget indicator -> {
                    for (int possibleSlot : indicator.slots()) {
                        if (possibleSlot == slot) return indicator;
                    }
                }
                case SubMenuWidget submenu -> {
                    if (submenu.slot() == slot) return submenu;
                }
                case ButtonIndicatorWidget button -> {
                    if (button.slot() == slot) return button;
                }
                case ItemSlotSupplierWidget item -> {
                    if (item.slot() == slot) return item;
                }
            }
        }
        return null;
    }
}