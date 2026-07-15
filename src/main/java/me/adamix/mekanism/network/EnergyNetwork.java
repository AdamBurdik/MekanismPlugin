package me.adamix.mekanism.network;

import lombok.Getter;
import me.adamix.mekanism.network.port.NetworkPort;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class EnergyNetwork extends AbstractNetwork {
    public EnergyNetwork(@NotNull UUID id) {
        super(id);
    }

    @Override
    public @NotNull NetworkType type() {
        return NetworkType.ENERGY;
    }

    @Override
    public void tick() {
        List<NetworkPort> producers = this.producers.stream()
//                .filter(port -> port.getLocation().getChunk().isLoaded())
                .toList();

        List<NetworkPort> consumers = this.consumers.stream()
//                .filter(port -> port.getLocation().getChunk().isLoaded())
                .toList();

        if (producers.isEmpty() || consumers.isEmpty()) return;

        long totalAvailable = producers.stream()
                .mapToLong(p -> p.getEnergyComponent().orElseThrow().extract(Long.MAX_VALUE, true))
                .sum();

        if (totalAvailable == 0) return;


    }
}
