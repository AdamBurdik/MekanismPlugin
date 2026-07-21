package me.adamix.mekanism.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.data.MekanismKeys;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BlockInstance {
    @Getter
    private final WorldPos pos;
    private final List<Component> components = new ArrayList<>();
    @Getter
    private @NotNull BlockFace facing;

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

    public void load(@NotNull PersistentDataContainer pdc) {
        components.forEach(c -> c.load(pdc));
        facing = BlockFace.valueOf(
                pdc.get(MekanismKeys.FACING, PersistentDataType.STRING)
        );
    }

    public void save(PersistentDataContainer pdc) {
        components.forEach(c -> c.save(pdc));
        pdc.set(MekanismKeys.FACING, PersistentDataType.STRING, facing.name());
    }
}
