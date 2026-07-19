package me.adamix.mekanism.block;

import lombok.extern.slf4j.Slf4j;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.network.NetworkContext;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class BlockService {
    private final BlockRegistry blockRegistry;

    private final Map<WorldPos, MekanismBlockType> locationToType = new HashMap<>();
    private final Map<WorldPos, ItemDisplay> locationToEntity = new HashMap<>();

    public BlockService(
            @NotNull BlockRegistry blockRegistry
    ) {
        this.blockRegistry = blockRegistry;
    }

    public void placeBlock(
            @NotNull Block block,
            @NotNull MekanismBlockType type
    ) {
        BlockDefinition definition = blockRegistry.getOrThrow(type);

        block.setType(definition.base());
        if (definition.baseData() != null) {
            block.setBlockData(definition.baseData());
        }

        locationToType.put(WorldPos.of(block), type);
    }

    public void spawnEntity(
            @NotNull Block block,
            @NotNull MekanismBlockType type,
            @NotNull NetworkContext networkContext
            ) {
        BlockDefinition definition = blockRegistry.getOrThrow(type);

        var entity = definition.handler().spawnEntity(
                block,
                type,
                definition,
                networkContext
        );
        locationToEntity.put(WorldPos.of(block), entity);
    }

    public boolean isMekanismBlock(@NotNull Block block) {
        return locationToType.containsKey(WorldPos.of(block));
    }

    public @NotNull Optional<MekanismBlockType> getType(@NotNull Block block) {
        return Optional.ofNullable(locationToType.get(WorldPos.of(block)));
    }

    public void updateBlock(
            @NotNull Block block,
            @NotNull NetworkContext networkContext
    ) {
        WorldPos location = WorldPos.of(block);

        MekanismBlockType type = locationToType.get(location);
        if (type == null) return;

        ItemDisplay itemDisplay = locationToEntity.get(location);
        if (itemDisplay == null) {
            throw new IllegalStateException("Block does not contain an entity: " + location);
        }

        BlockDefinition reg = blockRegistry.getOrThrow(type);

        reg.handler().updateBlock(
                block,
                type,
                itemDisplay,
                reg,
                networkContext
        );
    }
}
