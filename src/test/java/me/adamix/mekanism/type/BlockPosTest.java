package me.adamix.mekanism.type;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class BlockPosTest {

    @Test
    public void offsetShouldAddCoordinates() {
        BlockPos pos = new BlockPos(1, 2, 3);
        BlockPos result = pos.offset(4, 5, 6);
        assertEquals(new BlockPos(5, 7, 9), result);
    }

    @Test
    public void offsetWithNegatives() {
        BlockPos pos = new BlockPos(10, 10, 10);
        BlockPos result = pos.offset(-3, -5, -7);
        assertEquals(new BlockPos(7, 5, 3), result);
    }

    @Test
    public void offsetWithZero() {
        BlockPos pos = new BlockPos(5, 5, 5);
        BlockPos result = pos.offset(0, 0, 0);
        assertEquals(pos, result);
    }

    @Test
    public void offsetDoesNotMutateOriginal() {
        BlockPos pos = new BlockPos(1, 1, 1);
        pos.offset(10, 10, 10);
        assertEquals(new BlockPos(1, 1, 1), pos);
    }

    @Test
    public void equality() {
        assertEquals(new BlockPos(1, 2, 3), new BlockPos(1, 2, 3));
        assertNotEquals(new BlockPos(1, 2, 3), new BlockPos(4, 5, 6));
    }
}
