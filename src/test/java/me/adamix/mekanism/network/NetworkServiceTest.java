package me.adamix.mekanism.network;

import me.adamix.mekanism.type.BlockPos;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class NetworkServiceTest {

    private final NetworkService service = new NetworkService(null);

    @Test
    public void getSurroundingReturnsAllNeighbors() {
        Set<BlockPos> nodes = Set.of(
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 1, 0),
            new BlockPos(0, -1, 0)
        );
        BlockPos center = new BlockPos(0, 0, 0);

        Set<BlockPos> surrounding = service.getSurrounding(nodes, center);
        assertEquals(6, surrounding.size());
        assertTrue(surrounding.containsAll(nodes));
    }

    @Test
    public void getSurroundingReturnsOnlyExistingNodes() {
        Set<BlockPos> nodes = Set.of(
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1)
        );
        BlockPos center = new BlockPos(0, 0, 0);

        Set<BlockPos> surrounding = service.getSurrounding(nodes, center);
        assertEquals(2, surrounding.size());
    }

    @Test
    public void getSurroundingReturnsEmptyWhenNoNeighbors() {
        Set<BlockPos> nodes = Set.of(new BlockPos(10, 10, 10));
        BlockPos center = new BlockPos(0, 0, 0);

        Set<BlockPos> surrounding = service.getSurrounding(nodes, center);
        assertTrue(surrounding.isEmpty());
    }

    @Test
    public void bfsReturnsSingleNodeIsland() {
        Set<BlockPos> nodes = Set.of(new BlockPos(5, 5, 5));
        Set<BlockPos> visited = service.bfs(nodes, new BlockPos(5, 5, 5));

        assertEquals(1, visited.size());
        assertTrue(visited.contains(new BlockPos(5, 5, 5)));
    }

    @Test
    public void bfsFindsConnectedComponent() {
        Set<BlockPos> nodes = Set.of(
            new BlockPos(0, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, 2),
            new BlockPos(10, 10, 10)
        );

        Set<BlockPos> visited = service.bfs(nodes, new BlockPos(0, 0, 0));

        assertEquals(3, visited.size());
        assertTrue(visited.contains(new BlockPos(0, 0, 0)));
        assertTrue(visited.contains(new BlockPos(0, 0, 1)));
        assertTrue(visited.contains(new BlockPos(0, 0, 2)));
        assertFalse(visited.contains(new BlockPos(10, 10, 10)));
    }

    @Test
    public void bfsHandlesLShape() {
        Set<BlockPos> nodes = Set.of(
            new BlockPos(0, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(1, 1, 0),
            new BlockPos(10, 10, 10)
        );

        Set<BlockPos> visited = service.bfs(nodes, new BlockPos(0, 0, 0));

        assertEquals(3, visited.size());
    }

    @Test
    public void bfsDisconnectedNodeReturnsItself() {
        Set<BlockPos> nodes = Set.of(
            new BlockPos(0, 0, 0),
            new BlockPos(5, 5, 5)
        );

        Set<BlockPos> visited = service.bfs(nodes, new BlockPos(5, 5, 5));

        assertEquals(1, visited.size());
        assertTrue(visited.contains(new BlockPos(5, 5, 5)));
    }
}
