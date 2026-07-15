package me.adamix.mekanism.block;

import lombok.extern.slf4j.Slf4j;
import me.adamix.mekanism.block.handler.BlockHandler;
import me.adamix.mekanism.block.handler.BlockHandlerRegistry;
import me.adamix.mekanism.network.NetworkContext;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BlockService {
    private final BlockRegistry blockRegistry;
    private final BlockHandlerRegistry handlerRegistry;

    private final Map<Location, MekanismBlockType> locationToType = new HashMap<>();
    private final Map<Location, ItemDisplay> locationToEntity = new HashMap<>();

    public BlockService(
            @NotNull BlockRegistry blockRegistry,
            @NotNull BlockHandlerRegistry handlerRegistry
    ) {
        this.blockRegistry = blockRegistry;
        this.handlerRegistry = handlerRegistry;
    }

    public void placeBlock(
            @NotNull Block block,
            @NotNull MekanismBlockType type
    ) {
        BlockRegistry.Definition reg = blockRegistry.getOrThrow(type);

        block.setType(reg.base());
        if (reg.baseData() != null) {
            block.setBlockData(reg.baseData());
        }

        locationToType.put(block.getLocation(), type);
    }

    public void spawnEntity(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull NetworkContext networkContext
            ) {
        BlockHandler handler = handlerRegistry.getOrThrow(type);
        BlockRegistry.Definition definition = blockRegistry.getOrThrow(type);

        var entity = handler.spawnEntity(
                block,
                type,
                definition,
                networkContext
        );
        locationToEntity.put(block.getLocation(), entity);
    }

    public boolean isMekanismBlock(@NotNull Block block) {
        return locationToType.containsKey(block.getLocation());
    }

    public void updateBlock(
            @NotNull Block block,
            @NotNull NetworkContext networkContext
    ) {
        Location location = block.getLocation();

        MekanismBlockType type = locationToType.get(location);
        if (type == null) return;

        ItemDisplay itemDisplay = locationToEntity.get(location);
        if (itemDisplay == null) {
            throw new IllegalStateException("Block does not contain an entity: " + location);
        }

        BlockRegistry.Definition reg = blockRegistry.getOrThrow(type);

        BlockHandler handler = handlerRegistry.getOrThrow(type);
        handler.updateBlock(
                block,
                type,
                itemDisplay,
                reg,
                networkContext
        );
    }
}
