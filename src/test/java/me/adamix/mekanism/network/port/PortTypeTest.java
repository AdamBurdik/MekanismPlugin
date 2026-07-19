package me.adamix.mekanism.network.port;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PortTypeTest {

    @Test
    public void shouldHaveInputOutputDisabled() {
        assertEquals(PortType.INPUT, PortType.valueOf("INPUT"));
        assertEquals(PortType.OUTPUT, PortType.valueOf("OUTPUT"));
        assertEquals(PortType.DISABLED, PortType.valueOf("DISABLED"));
    }

    @Test
    public void allValuesShouldBeNonNull() {
        for (PortType type : PortType.values()) {
            assertNotNull(type);
        }
    }
}
