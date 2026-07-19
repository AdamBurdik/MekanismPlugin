package me.adamix.mekanism.block;

import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.block.component.network.TransporterComponent;
import me.adamix.mekanism.network.NetworkType;
import org.bukkit.persistence.PersistentDataContainer;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class BlockInstanceTest {

    @Test
    public void addAndRetrieveComponent() {
        BlockInstance instance = new BlockInstance();
        TransporterComponent component = new TransporterComponent(NetworkType.ENERGY);
        instance.add(component);

        Optional<TransporterComponent> retrieved = instance.get(TransporterComponent.class);
        assertTrue(retrieved.isPresent());
        assertSame(component, retrieved.get());
    }

    @Test
    public void hasReturnsTrueWhenComponentPresent() {
        BlockInstance instance = new BlockInstance();
        instance.add(new TransporterComponent(NetworkType.ENERGY));
        assertTrue(instance.has(TransporterComponent.class));
    }

    @Test
    public void hasReturnsFalseWhenComponentAbsent() {
        BlockInstance instance = new BlockInstance();
        assertFalse(instance.has(TransporterComponent.class));
    }

    @Test
    public void getReturnsEmptyWhenComponentAbsent() {
        BlockInstance instance = new BlockInstance();
        assertTrue(instance.get(TransporterComponent.class).isEmpty());
    }

    @Test
    public void getReturnsCorrectTypeAmongMultipleComponents() {
        BlockInstance instance = new BlockInstance();
        DummyComponentA a = new DummyComponentA();
        DummyComponentB b = new DummyComponentB();
        instance.add(a);
        instance.add(b);

        assertSame(a, instance.get(DummyComponentA.class).orElseThrow());
        assertSame(b, instance.get(DummyComponentB.class).orElseThrow());
    }

    @Test
    public void componentsReturnsAllAddedComponents() {
        BlockInstance instance = new BlockInstance();
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
