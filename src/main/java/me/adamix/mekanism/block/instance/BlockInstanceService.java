package me.adamix.mekanism.block.instance;

import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.handler.BlockHandler;
import me.adamix.mekanism.block.handler.BlockHandlerRegistry;
import me.adamix.mekanism.network.NetworkContext;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BlockInstanceService {
    private final BlockHandlerRegistry handlerRegistry;
    private final Map<Location, BlockInstance> instanceMap = new HashMap<>();

    public @NotNull BlockInstance create(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull NetworkContext networkContext
    ) {
        BlockHandler handler = handlerRegistry.getOrThrow(type);
        BlockInstance instance = handler.createBlockInstance(block, type, networkContext);
        instanceMap.put(block.getLocation(), instance);
        return instance;
    }

    public @NotNull Optional<BlockInstance> get(@NotNull Location location) {
        return Optional.ofNullable(instanceMap.get(location));
    }
}
