package me.adamix.mekanism.infusion;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InfusionTypeRegistry {
    private final List<InfusionMapping> mappings = new ArrayList<>();

    public void register(@NotNull InfusionMapping mapping) {
        mappings.add(mapping);
    }

    public @NotNull Optional<InfusionMapping> getMappingFor(@NotNull ItemStack item) {
        return mappings.stream()
                .filter(m -> m.matcher().matches(item))
                .findFirst();
    }
}