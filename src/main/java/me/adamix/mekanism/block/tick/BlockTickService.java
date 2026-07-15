package me.adamix.mekanism.block.tick;

import me.adamix.mekanism.block.component.TickableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockTickService {
    private final List<TickableComponent> components = new ArrayList<>();

    public void register(@NotNull TickableComponent component) {
        components.add(component);
    }

    public void tick() {
        for (TickableComponent component : components) {
            component.tick();
        }
    }
}
