package me.adamix.mekanism.event;

import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.menu.MenuService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class InventoryListener implements Listener {
    private final MenuService menuService;

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        menuService.onItemClick(event);
    }
}
