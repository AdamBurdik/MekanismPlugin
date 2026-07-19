package me.adamix.mekanism.block.instance;

import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.network.NetworkContext;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BlockInstanceService {
    private final BlockRegistry blockRegistry;
    private final Map<Location, BlockInstance> instanceMap = new HashMap<>();

    public @NotNull BlockInstance create(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull NetworkContext networkContext
    ) {
        var definition = blockRegistry.getOrThrow(type);
        BlockInstance instance = definition.handler()
                .createBlockInstance(block, type, networkContext, definition);
        instanceMap.put(block.getLocation(), instance);
        return instance;
    }

    public @NotNull Optional<BlockInstance> get(@NotNull Location location) {
        return Optional.ofNullable(instanceMap.get(location));
    }
}
