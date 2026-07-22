package me.adamix.mekanism.block.tick;

import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.BlockInstance;
import me.adamix.mekanism.block.component.TickableComponent;
import me.adamix.mekanism.block.persistence.BlockPersistenceService;
import me.adamix.mekanism.type.Tuple;
import me.adamix.mekanism.type.WorldPos;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class BlockTickService {
    private final Map<WorldPos, Tuple<BlockInstance, TickableComponent>> components = new HashMap<>();
    private final BlockPersistenceService blockPersistenceService;

    public void register(
            @NotNull WorldPos pos,
            @NotNull BlockInstance instance,
            @NotNull TickableComponent component
    ) {
        components.put(pos, new Tuple<>(instance, component));
    }

    public void tick() {
        for (Map.Entry<WorldPos, Tuple<BlockInstance, TickableComponent>> entry : components.entrySet()) {
            WorldPos pos = entry.getKey();
            Tuple<BlockInstance, TickableComponent> tuple = entry.getValue();
            var instance = tuple.left();
            var component = tuple.right();

            component.tick(instance);

            blockPersistenceService.markDirty(pos);
        }
    }
}
