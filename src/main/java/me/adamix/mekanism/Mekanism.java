package me.adamix.mekanism;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.adamix.mekanism.block.BlockFacade;
import me.adamix.mekanism.block.BlockRegistry;
import me.adamix.mekanism.block.BlockService;
import me.adamix.mekanism.block.handler.BlockHandlerRegistry;
import me.adamix.mekanism.command.DebugCommand;
import me.adamix.mekanism.command.GiveCommand;
import me.adamix.mekanism.data.MekanismKeys;
import me.adamix.mekanism.event.BlockListener;
import me.adamix.mekanism.network.NetworkService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Mekanism extends JavaPlugin {
    private NetworkService networkService;
    private BlockService blockService;

    @Override
    public void onEnable() {
        MekanismKeys.init(this);

        BlockRegistry blockRegistry = new BlockRegistry();
        BlockHandlerRegistry blockHandlerRegistry = new BlockHandlerRegistry();

        networkService = new NetworkService(getSLF4JLogger());
        blockService = new BlockService(blockRegistry, blockHandlerRegistry);

        BlockFacade blockFacade = new BlockFacade(
                blockService,
                networkService,
                blockHandlerRegistry
        );

        Bukkit.getPluginManager().registerEvents(new BlockListener(blockFacade), this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register("debug", new DebugCommand(networkService));
            commands.registrar().register("mgive", new GiveCommand());
        });
    }

    @Override
    public void onDisable() {
    }
}
