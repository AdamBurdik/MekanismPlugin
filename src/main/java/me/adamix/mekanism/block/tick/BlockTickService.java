package me.adamix.mekanism.block.tick;

import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.component.TickableComponent;
import me.adamix.mekanism.block.persistence.BlockPersistenceService;
import me.adamix.mekanism.type.WorldPos;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BlockTickService {
    private final Map<WorldPos, TickableComponent> components = new HashMap<>();
    private final BlockPersistenceService blockPersistenceService;

    public void register(
            @NotNull WorldPos pos,
            @NotNull TickableComponent component
    ) {
        components.put(pos, component);
    }

    public void tick() {
        for (Map.Entry<WorldPos, TickableComponent> entry : components.entrySet()) {
            WorldPos pos = entry.getKey();
            TickableComponent component = entry.getValue();

            component.tick();

            blockPersistenceService.markDirty(pos);
        }
    }
}
