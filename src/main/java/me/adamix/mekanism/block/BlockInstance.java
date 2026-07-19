package me.adamix.mekanism.block;

import me.adamix.mekanism.block.component.Component;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockInstance {
    private final List<Component> components = new ArrayList<>();

    public <T extends Component> void add(@NotNull T component) {
        components.add(component);
    }

    public <T extends Component> Optional<T> get(Class<T> type) {
        return components.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst();
    }

    public boolean has(@NotNull Class<? extends Component> type) {
        return get(type).isPresent();
    }

    public @NotNull List<Component> components() {
        return components;
    }

    public void loadAll(@NotNull PersistentDataContainer pdc) {
        components.forEach(c -> c.load(pdc));
    }
    public void saveAll(PersistentDataContainer pdc) {
        components.forEach(c -> c.save(pdc));
    }
}
