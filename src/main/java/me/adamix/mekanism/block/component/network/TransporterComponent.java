package me.adamix.mekanism.block.component.network;

import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.network.NetworkType;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import static me.adamix.utils.Utils.todo;


public record TransporterComponent(
        @NotNull NetworkType type
) implements Component {


    @Override
    public void load(@NotNull PersistentDataContainer pdc) {

    }

    @Override
    public void save(@NotNull PersistentDataContainer pdc) {
    }
}
