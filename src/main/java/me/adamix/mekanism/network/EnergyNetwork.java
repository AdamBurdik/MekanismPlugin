package me.adamix.mekanism.network;

import lombok.Getter;
import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.network.port.NetworkPort;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class EnergyNetwork extends AbstractNetwork {
    public EnergyNetwork(@NotNull UUID id, @NotNull String worldName) {
        super(id, worldName);
    }

    @Override
    public @NotNull NetworkType type() {
        return NetworkType.ENERGY;
    }

@Override
public void tick() {
    List<NetworkPort> producers = this.producers.stream()
//            .filter(port -> port.getLocation().getChunk().isLoaded())
            .toList();
    List<NetworkPort> consumers = this.consumers.stream()
//            .filter(port -> port.getLocation().getChunk().isLoaded())
            .toList();
    if (producers.isEmpty() || consumers.isEmpty()) return;

    // 1. Check how much each producer can give
    Map<EnergyComponent, Long> offered = new HashMap<>();
    long totalAvailable = 0;
    for (NetworkPort port : producers) {
        EnergyComponent component = port.getEnergyComponent().orElseThrow();
        long available = component.extract(Long.MAX_VALUE, true);
        offered.put(component, available);
        totalAvailable += available;
    }
    if (totalAvailable == 0) return;

    // 2. Divide between consumers
    Map<EnergyComponent, Long> distributed = redistributeEnergy(totalAvailable, consumers);
    long finalTotal = distributed.values().stream().mapToLong(Long::longValue).sum();
    if (finalTotal == 0) return;

    // 3. Device between producers
    double ratio = (double) finalTotal / (double) totalAvailable;
    Map<EnergyComponent, Long> toExtract = new HashMap<>();
    long allocated = 0;
    List<EnergyComponent> producerComponents = new ArrayList<>(offered.keySet());

    for (int i = 0; i < producerComponents.size(); i++) {
        EnergyComponent component = producerComponents.get(i);
        long amount;
        if (i == producerComponents.size() - 1) {
            amount = finalTotal - allocated;
        } else {
            amount = Math.round(offered.get(component) * ratio);
        }
        toExtract.put(component, amount);
        allocated += amount;
    }

    // 4. Actually apply the updates
    for (var entry : toExtract.entrySet()) {
        entry.getKey().extract(entry.getValue(), false);
    }
    for (var entry : distributed.entrySet()) {
        entry.getKey().insert(entry.getValue(), false);
    }
}

Map<EnergyComponent, Long> redistributeEnergy(
        long totalAvailable,
        @NotNull List<NetworkPort> consumers
) {
    long pool = totalAvailable;
    List<EnergyComponent> remaining = new ArrayList<>(
            consumers.stream().map(port -> port.getEnergyComponent().orElseThrow()).toList()
    );
    Map<EnergyComponent, Long> result = new HashMap<>();

    while (pool > 0 && !remaining.isEmpty()) {
        long share = pool / remaining.size();
        if (share == 0) break;

        Iterator<EnergyComponent> iterator = remaining.iterator();
        long distributedThisRound = 0;

        while (iterator.hasNext()) {
            EnergyComponent component = iterator.next();
            long inserted = component.insert(share, true);
            result.merge(component, inserted, Long::sum);
            distributedThisRound += inserted;

            if (inserted < share) {
                iterator.remove();
            }
        }

        pool -= distributedThisRound;
    }

    return result;
}
}
