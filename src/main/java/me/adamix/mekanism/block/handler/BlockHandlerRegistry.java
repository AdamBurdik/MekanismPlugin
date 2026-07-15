package me.adamix.mekanism.block.handler;

import me.adamix.mekanism.block.MekanismBlockType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockHandlerRegistry {
    private final Map<MekanismBlockType, BlockHandler> registry = new HashMap<>();

    public BlockHandlerRegistry() {
        UniversalCableHandler cableHandler = new UniversalCableHandler();
        register(MekanismBlockType.BASIC_UNIVERSAL_CABLE, cableHandler);
        register(MekanismBlockType.ADVANCED_UNIVERSAL_CABLE, cableHandler);
        register(MekanismBlockType.ELITE_UNIVERSAL_CABLE, cableHandler);
        register(MekanismBlockType.ULTIMATE_UNIVERSAL_CABLE, cableHandler);
        register(MekanismBlockType.BASIC_ENERGY_CUBE, new EnergyCubeHandler());
    }

    public void register(@NotNull MekanismBlockType type, @NotNull BlockHandler handler) {
        registry.put(type, handler);
    }

    public @NotNull Optional<BlockHandler> get(@NotNull MekanismBlockType type) {
        return Optional.ofNullable(registry.get(type));
    }

    public @NotNull BlockHandler getOrThrow(@NotNull MekanismBlockType type) {
        if (!registry.containsKey(type)) {
            throw new IllegalStateException("No block handler found for: " + type.name());
        }
        return registry.get(type);
    }
}
