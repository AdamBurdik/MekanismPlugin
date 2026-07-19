package me.adamix.mekanism;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.adamix.mekanism.block.BlockFacade;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.block.BlockService;
import me.adamix.mekanism.block.handler.BlockHandlerRegistry;
import me.adamix.mekanism.block.instance.BlockInstanceService;
import me.adamix.mekanism.block.tick.BlockTickService;
import me.adamix.mekanism.command.DebugCommand;
import me.adamix.mekanism.command.GiveCommand;
import me.adamix.mekanism.command.TestCommand;
import me.adamix.mekanism.data.MekanismKeys;
import me.adamix.mekanism.event.BlockListener;
import me.adamix.mekanism.menu.MenuService;
import me.adamix.mekanism.network.NetworkService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Mekanism extends JavaPlugin {
    private NetworkService networkService;

    @Override
    public void onEnable() {
        MekanismKeys.init(this);

        MenuService menuService = new MenuService();

        BlockRegistry blockRegistry = new BlockRegistry();
        BlockTickService blockTickService = new BlockTickService();
        BlockInstanceService blockInstanceService = new BlockInstanceService(blockRegistry);

        networkService = new NetworkService(getSLF4JLogger());
        BlockService blockService = new BlockService(blockRegistry);

        BlockFacade blockFacade = new BlockFacade(
                blockService,
                networkService,
                blockTickService,
                blockInstanceService,
                blockRegistry,
                menuService
        );

        Bukkit.getPluginManager().registerEvents(new BlockListener(blockFacade), this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                () -> {
                    blockTickService.tick();
                    networkService.tick();
                    menuService.tickOpenMenus();
                },
                0L,
                20
        );

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register("debug", new DebugCommand(networkService, blockInstanceService));
            commands.registrar().register("mgive", new GiveCommand());
            commands.registrar().register("mtest", new TestCommand(this, blockRegistry, menuService));
        });
    }

    @Override
    public void onDisable() {
    }
}
