package me.adamix.mekanism.block.component.network;

import me.adamix.mekanism.network.NetworkType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransporterComponentTest {

    @Test
    public void constructorStoresType() {
        TransporterComponent component = new TransporterComponent(NetworkType.ENERGY);
        assertEquals(NetworkType.ENERGY, component.type());
    }
}
