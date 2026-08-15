package me.adamix.mekanism;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.adamix.mekanism.block.BlockFacade;
import me.adamix.mekanism.block.BlockService;
import me.adamix.mekanism.block.instance.BlockInstanceService;
import me.adamix.mekanism.block.persistence.BlockPersistenceService;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.block.tick.BlockTickService;
import me.adamix.mekanism.bootstrap.BlockRegistryBootstrap;
import me.adamix.mekanism.command.DebugCommand;
import me.adamix.mekanism.command.GiveCommand;
import me.adamix.mekanism.command.SaveCommand;
import me.adamix.mekanism.command.TestCommand;
import me.adamix.mekanism.data.MekanismKeys;
import me.adamix.mekanism.event.BlockListener;
import me.adamix.mekanism.event.ChunkListener;
import me.adamix.mekanism.event.InventoryListener;
import me.adamix.mekanism.infusion.InfusionMapping;
import me.adamix.mekanism.infusion.InfusionType;
import me.adamix.mekanism.infusion.InfusionTypeRegistry;
import me.adamix.mekanism.item.ItemRegistry;
import me.adamix.mekanism.menu.MenuRegistry;
import me.adamix.mekanism.menu.MenuService;
import me.adamix.mekanism.network.NetworkService;
import me.adamix.mekanism.recipe.RecipeRegistry;
import me.adamix.mekanism.recipe.matcher.MaterialMatcher;
import me.adamix.mekanism.translation.Translations;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public final class Mekanism extends JavaPlugin {
    private NetworkService networkService;
    private BlockFacade blockFacade;
    private MenuService menuService;
    private BlockPersistenceService blockPersistenceService;

    private Translations translations;

    private InfusionTypeRegistry infusionTypeRegistry;
    private RecipeRegistry recipeRegistry;
    private ItemRegistry itemRegistry;
    private MenuRegistry menuRegistry;

    private BlockRegistryBootstrap blockRegistryBootstrap;

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

        BlockTickService blockTickService = new BlockTickService(blockPersistenceService);
        networkService = new NetworkService(getSLF4JLogger(), blockPersistenceService, blockInstanceService);
        BlockService blockService = new BlockService(blockRegistry, blockPersistenceService);

        blockFacade = new BlockFacade(
                blockService,
                networkService,
                blockTickService,
                blockInstanceService,
                blockRegistry,
                menuService
        );

        infusionTypeRegistry = new InfusionTypeRegistry();
        infusionTypeRegistry.register(new InfusionMapping(new MaterialMatcher(Material.COAL), InfusionType.CARBON, 10));
        infusionTypeRegistry.register(new InfusionMapping(new MaterialMatcher(Material.REDSTONE), InfusionType.REDSTONE, 10));

        translations = new Translations();
        itemRegistry = new ItemRegistry();

        menuRegistry = new MenuRegistry(
                menuService,
                networkService,
                blockFacade,
                itemRegistry,
                translations
        );

        blockRegistryBootstrap = new BlockRegistryBootstrap(
                blockRegistry,
                recipeRegistry,
                menuRegistry,
                infusionTypeRegistry
        );
        blockRegistryBootstrap.registerBlocks();

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
