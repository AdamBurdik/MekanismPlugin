package me.adamix.mekanism.network;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NetworkTypeTest {

    @Test
    public void shouldHaveEnergyValue() {
        assertEquals(NetworkType.ENERGY, NetworkType.valueOf("ENERGY"));
    }

    @Test
    public void allValuesShouldBeNonNull() {
        for (NetworkType type : NetworkType.values()) {
            assertNotNull(type);
        }
    }
}
