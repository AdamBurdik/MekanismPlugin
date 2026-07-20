package me.adamix.mekanism.block;

import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.block.component.network.TransporterComponent;
import me.adamix.mekanism.network.NetworkType;
import me.adamix.mekanism.type.BlockPos;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BlockInstanceTest {

    @Test
    public void addAndRetrieveComponent() {
        BlockInstance instance = new BlockInstance(new WorldPos("world", new BlockPos(0, 0, 0)), BlockFace.NORTH);
        TransporterComponent component = new TransporterComponent(NetworkType.ENERGY);
        instance.add(component);

        Optional<TransporterComponent> retrieved = instance.get(TransporterComponent.class);
        assertTrue(retrieved.isPresent());
        assertSame(component, retrieved.get());
    }

    @Test
    public void hasReturnsTrueWhenComponentPresent() {
        BlockInstance instance = new BlockInstance(new WorldPos("world", new BlockPos(0, 0, 0)), BlockFace.NORTH);
        instance.add(new TransporterComponent(NetworkType.ENERGY));
        assertTrue(instance.has(TransporterComponent.class));
    }

    @Test
    public void hasReturnsFalseWhenComponentAbsent() {
        BlockInstance instance = new BlockInstance(new WorldPos("world", new BlockPos(0, 0, 0)), BlockFace.NORTH);
        assertFalse(instance.has(TransporterComponent.class));
    }

    @Test
    public void getReturnsEmptyWhenComponentAbsent() {
        BlockInstance instance = new BlockInstance(new WorldPos("world", new BlockPos(0, 0, 0)), BlockFace.NORTH);
        assertTrue(instance.get(TransporterComponent.class).isEmpty());
    }

    @Test
    public void getReturnsCorrectTypeAmongMultipleComponents() {
        BlockInstance instance = new BlockInstance(new WorldPos("world", new BlockPos(0, 0, 0)), BlockFace.NORTH);
        DummyComponentA a = new DummyComponentA();
        DummyComponentB b = new DummyComponentB();
        instance.add(a);
        instance.add(b);

        assertSame(a, instance.get(DummyComponentA.class).orElseThrow());
        assertSame(b, instance.get(DummyComponentB.class).orElseThrow());
    }

    @Test
    public void componentsReturnsAllAddedComponents() {
        BlockInstance instance = new BlockInstance(new WorldPos("world", new BlockPos(0, 0, 0)), BlockFace.NORTH);
        TransporterComponent c = new TransporterComponent(NetworkType.ENERGY);
        instance.add(c);
        assertEquals(1, instance.components().size());
        assertSame(c, instance.components().get(0));
    }

    private static class DummyComponentA implements Component {
        @Override
        public void load(PersistentDataContainer pdc) {}
        @Override
        public void save(PersistentDataContainer pdc) {}
    }

    private static class DummyComponentB implements Component {
        @Override
        public void load(PersistentDataContainer pdc) {}
        @Override
        public void save(PersistentDataContainer pdc) {}
    }
}
