package me.adamix.mekanism.network;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public record NetworkContext(
        @NotNull Map<BlockFace, AbstractNetwork> networkMap
) {
    public @NotNull Optional<AbstractNetwork> get(@NotNull BlockFace face) {
        return Optional.ofNullable(networkMap.get(face));
    }
}
