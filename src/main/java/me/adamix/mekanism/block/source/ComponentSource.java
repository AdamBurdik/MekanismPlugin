package me.adamix.mekanism.block.source;

import me.adamix.mekanism.block.component.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ComponentSource {
    <T extends Component> Optional<T> get(@NotNull Class<T> type);
}
