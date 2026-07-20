package me.adamix.mekanism.block.registry;

import me.adamix.mekanism.block.MekanismBlockType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockRegistry {
    private final Map<MekanismBlockType, BlockDefinition> registry = new HashMap<>();

    public void register(
            @NotNull MekanismBlockType type,
            @NotNull BlockDefinition definition
    ) {
        registry.put(type, definition);
    }

    public @NotNull Optional<BlockDefinition> get(@NotNull MekanismBlockType type) {
        return Optional.ofNullable(registry.get(type));
    }

    public @NotNull BlockDefinition getOrThrow(@NotNull MekanismBlockType type) {
        if (!registry.containsKey(type)) {
            throw new IllegalArgumentException("Block type not registered: " + type.name());
        }
        return registry.get(type);
    }
}
