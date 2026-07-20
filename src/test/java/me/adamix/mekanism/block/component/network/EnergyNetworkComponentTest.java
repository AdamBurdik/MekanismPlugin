package me.adamix.mekanism.block.component.network;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class EnergyNetworkComponentTest {

    @Test
    public void constructorStoresNetworkId() {
        UUID id = UUID.randomUUID();
        EnergyNetworkComponent component = new EnergyNetworkComponent(id);
        assertEquals(id, component.getNetworkId());
    }

    @Test
    public void setNetworkIdUpdatesValue() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        EnergyNetworkComponent component = new EnergyNetworkComponent(id1);
        component.setNetworkId(id2);
        assertEquals(id2, component.getNetworkId());
    }
}
