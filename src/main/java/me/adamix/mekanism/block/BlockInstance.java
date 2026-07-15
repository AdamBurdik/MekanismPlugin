package me.adamix.mekanism.block;

import me.adamix.mekanism.block.component.Component;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlockInstance {
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    public <T extends Component> void add(Class<T> type, T component) {
        components.put(type, component);
    }

    public <T extends Component> Optional<T> get(@NotNull Class<T> type) {
        return Optional.ofNullable(type.cast(components.get(type)));
    }

    public boolean has(@NotNull Class<? extends Component> type) {
        return components.containsKey(type);
    }

    public @NotNull Collection<Component> components() {
        return components.values();
    }

    public void loadAll(@NotNull PersistentDataContainer pdc) {
        components.values().forEach(c -> c.load(pdc));
    }
    public void saveAll(PersistentDataContainer pdc) {
        components.values().forEach(c -> c.save(pdc));
    }
}
