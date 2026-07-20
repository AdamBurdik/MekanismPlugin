package me.adamix.mekanism.network;

import me.adamix.mekanism.block.component.network.EnergyComponent;
import me.adamix.mekanism.energy.EnergyStorage;
import me.adamix.mekanism.network.port.NetworkPort;
import me.adamix.mekanism.network.port.PortType;
import me.adamix.mekanism.type.BlockPos;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.block.BlockFace;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnergyNetworkTest {

    private EnergyComponent componentWithCapacity(long capacity, long energy) {
        return new EnergyComponent(Map.of(), new EnergyStorage(capacity, capacity, capacity, energy));
    }

    @Test
    public void redistributeEnergySimpleEqualDivision() {
        EnergyNetwork network = new EnergyNetwork(UUID.randomUUID(), "world");

        EnergyComponent consumer1 = componentWithCapacity(50, 0);
        EnergyComponent consumer2 = componentWithCapacity(50, 0);

        List<NetworkPort> consumers = List.of(
            createConsumerPort(consumer1),
            createConsumerPort(consumer2)
        );

        Map<EnergyComponent, Long> result = network.redistributeEnergy(100, consumers);

        assertEquals(50, (long) result.get(consumer1));
        assertEquals(50, (long) result.get(consumer2));
    }

    @Test
    public void redistributeEnergyHonorsConsumerCapacity() {
        EnergyNetwork network = new EnergyNetwork(UUID.randomUUID(), "world");

        EnergyComponent small = componentWithCapacity(10, 0);
        EnergyComponent large = componentWithCapacity(100, 0);

        List<NetworkPort> consumers = List.of(
            createConsumerPort(small),
            createConsumerPort(large)
        );

        Map<EnergyComponent, Long> result = network.redistributeEnergy(200, consumers);

        assertEquals(10, (long) result.get(small));
        assertEquals(190, (long) result.get(large));
    }

    @Test
    public void redistributeEnergyNotEnoughForAll() {
        EnergyNetwork network = new EnergyNetwork(UUID.randomUUID(), "world");

        EnergyComponent c1 = componentWithCapacity(100, 0);
        EnergyComponent c2 = componentWithCapacity(100, 0);
        EnergyComponent c3 = componentWithCapacity(100, 0);

        List<NetworkPort> consumers = List.of(
            createConsumerPort(c1),
            createConsumerPort(c2),
            createConsumerPort(c3)
        );

        Map<EnergyComponent, Long> result = network.redistributeEnergy(15, consumers);

        long total = result.values().stream().mapToLong(Long::longValue).sum();
        assertEquals(15, total);
    }

    @Test
    public void redistributeEnergyWithNoAvailableEnergy() {
        EnergyNetwork network = new EnergyNetwork(UUID.randomUUID(), "world");

        EnergyComponent c1 = componentWithCapacity(100, 0);
        EnergyComponent c2 = componentWithCapacity(100, 0);

        List<NetworkPort> consumers = List.of(
            createConsumerPort(c1),
            createConsumerPort(c2)
        );

        Map<EnergyComponent, Long> result = network.redistributeEnergy(0, consumers);

        assertTrue(result.isEmpty());
    }

    @Test
    public void redistributeEnergyFullConsumersAreSkipped() {
        EnergyNetwork network = new EnergyNetwork(UUID.randomUUID(), "world");

        EnergyComponent full = componentWithCapacity(10, 10);
        EnergyComponent empty = componentWithCapacity(50, 0);

        List<NetworkPort> consumers = List.of(
            createConsumerPort(full),
            createConsumerPort(empty)
        );

        Map<EnergyComponent, Long> result = network.redistributeEnergy(50, consumers);

        assertEquals(0, (long) result.get(full));
        assertEquals(50, (long) result.get(empty));
    }

    private NetworkPort createConsumerPort(EnergyComponent component) {
        BlockInstanceStub stub = new BlockInstanceStub(component, new WorldPos("world", new BlockPos(0, 0, 0)), BlockFace.NORTH);
        return new NetworkPort(new BlockPos(0, 0, 0), "world", BlockFace.NORTH, PortType.INPUT, stub, UUID.randomUUID());
    }
}
