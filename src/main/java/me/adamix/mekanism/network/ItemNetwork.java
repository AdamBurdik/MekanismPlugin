package me.adamix.mekanism.network;

import me.adamix.mekanism.block.component.item.ItemComponent;
import me.adamix.mekanism.network.port.NetworkPort;
import me.adamix.mekanism.type.BlockPos;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class ItemNetwork extends AbstractNetwork {
    private final Map<BlockPos, Set<NetworkPort>> routes = new HashMap<>();

    public ItemNetwork(@NotNull UUID id, @NotNull String worldName) {
        super(id, worldName);
    }

    @Override
    public @NotNull NetworkType type() {
        return NetworkType.ITEM;
    }

    @Override
    public void addTransporter(@NotNull Block block) {
        super.addTransporter(block);
        calculateRoutes();
    }

    @Override
    public void addConsumer(@NotNull NetworkPort consumer) {
        super.addConsumer(consumer);
        calculateRoutes();
    }

    @Override
    public void addProducer(@NotNull NetworkPort producer) {
        super.addProducer(producer);
        calculateRoutes();
    }

    @Override
    public void removeProducer(@NotNull NetworkPort producer) {
        super.removeProducer(producer);
        calculateRoutes();
    }

    @Override
    public void removeTransporter(@NotNull Block block) {
        super.removeTransporter(block);
        calculateRoutes();
    }

    private @NotNull Set<NetworkPort> bfs(@NotNull BlockPos starting) {
        Set<NetworkPort> foundPorts = new HashSet<>();

        Map<BlockPos, Integer> visited = new HashMap<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        visited.put(starting, 0);
        queue.add(starting);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            int currentDistance = visited.get(current);

            if (!current.equals(starting) && isNetworkPort(current)) {
                foundPorts.add(getNetworkPortAt(current));
            }

            for (BlockPos surrounding : getSurrounding(current)) {
                if (!visited.containsKey(surrounding)) {
                    visited.put(surrounding, currentDistance + 1);
                    queue.add(surrounding);
                }
            }
        }

        return foundPorts;
    }

    private void calculateRoutes() {
        routes.clear();

        for (NetworkPort producer : getProducers().values()) {
            Set<NetworkPort> route = bfs(producer.getPos());

            routes.put(producer.getPos(), route);
        }
    }

    @Override
    public void update() {
        calculateRoutes();
    }

    @Override
    public @Nullable Set<WorldPos> tick() {
        Set<WorldPos> touched = new HashSet<>();

        for (NetworkPort producer : getProducers().values()) {
            if (!producer.getPos().withWorld(worldName).resolveBlock().getChunk().isLoaded()) {
                continue;
            }

            ItemComponent producerComponent = producer.getItemComponent().orElseThrow();

            Set<NetworkPort> route = routes.get(producer.getPos());
            if (route.isEmpty()) continue;

            int transferRatePool = 32;

            for (NetworkPort consumer : route) {
                if (transferRatePool <= 0) break;
                if (consumer.equals(producer)) continue;
                if (!consumer.getPos().withWorld(worldName).resolveBlock().getChunk().isLoaded()) {
                    continue;
                }

                ItemComponent consumerComponent = consumer.getItemComponent().orElseThrow();

                ItemStack extracted = producerComponent.extract(
                        consumerComponent.getAcceptedMatcher(consumer.getBlockFace()),
                        producer.getBlockFace(),
                        transferRatePool,
                        true
                );
                if (extracted == null) continue;

                ItemStack inserted = consumerComponent.insert(
                        extracted,
                        consumer.getBlockFace(),
                        false
                );

                int insertedAmount = inserted == null ? transferRatePool : extracted.getAmount() - inserted.getAmount();

                if (insertedAmount > 0) {
                    producerComponent.extract(
                            consumerComponent.getAcceptedMatcher(consumer.getBlockFace()),
                            producer.getBlockFace(),
                            insertedAmount,
                            false
                    );
                    transferRatePool -= insertedAmount;
                    touched.add(producer.getPos().withWorld(worldName));
                    touched.add(consumer.getPos().withWorld(worldName));
                }
            }
        }

        return touched;
    }
}
