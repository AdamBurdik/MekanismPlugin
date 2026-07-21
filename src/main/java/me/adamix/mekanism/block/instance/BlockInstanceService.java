package me.adamix.mekanism.block.instance;

import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BlockInstanceService {
    private final BlockRegistry blockRegistry;
    private final Map<WorldPos, BlockInstance> instanceMap = new HashMap<>();

    public @NotNull BlockInstance create(
            @NotNull Block block,
            @NotNull BlockFace facing,
            @NotNull MekanismBlockType type,
            @NotNull NetworkContext networkContext
    ) {
        var definition = blockRegistry.getOrThrow(type);
        BlockInstance instance = definition.handler()
                .createBlockInstance(block, facing, type, networkContext, definition);
        instanceMap.put(WorldPos.of(block), instance);
        return instance;
    }

    public @NotNull Optional<BlockInstance> get(@NotNull WorldPos pos) {
        return Optional.ofNullable(instanceMap.get(pos));
    }

    public void load(@NotNull BlockInstance instance) {
        instanceMap.put(instance.getPos(), instance);
    }
}
