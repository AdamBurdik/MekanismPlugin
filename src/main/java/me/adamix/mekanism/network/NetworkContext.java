package me.adamix.mekanism.network;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record NetworkContext(
        @NotNull Map<BlockFace, List<AbstractNetwork>> networkMap
) {
    public @NotNull List<AbstractNetwork> get(@NotNull BlockFace face) {
        return networkMap.getOrDefault(face, Collections.emptyList());
    }

    public @NotNull Map<BlockFace, AbstractNetwork> filter(@NotNull NetworkType type) {
        Map<BlockFace, AbstractNetwork> filtered = new HashMap<>();

        networkMap.forEach((face, networks) -> {
            var first = networks.stream()
                    .filter(network -> network.type() == type)
                    .findFirst();

            first.ifPresent(abstractNetwork -> filtered.put(face, abstractNetwork));
        });

        return filtered;
    }
}
