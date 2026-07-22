package me.adamix.mekanism.type.pdc;

import me.adamix.mekanism.network.port.PortType;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class PortsDataType implements PersistentDataType<byte[], Map<BlockFace, PortType>> {
    public static final PortsDataType INSTANCE = new PortsDataType();

    @Override
    public Class<byte[]> getPrimitiveType() { return byte[].class; }

    @Override
    public Class<Map<BlockFace, PortType>> getComplexType() {
        return (Class<Map<BlockFace, PortType>>) (Class<?>) Map.class;
    }

    @Override
    public byte[] toPrimitive(@NotNull Map<BlockFace, PortType> ports, @NotNull PersistentDataAdapterContext context) {
        byte[] result = new byte[BlockFace.values().length];
        Arrays.fill(result, (byte) -1);

        for (var entry : ports.entrySet()) {
            result[entry.getKey().ordinal()] = (byte) entry.getValue().ordinal();
        }
        return result;
    }

    @Override
    public Map<BlockFace, PortType> fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
        Map<BlockFace, PortType> ports = new HashMap<>();
        BlockFace[] faces = BlockFace.values();
        PortType[] types = PortType.values();

        for (int i = 0; i < primitive.length; i++) {
            if (primitive[i] != -1) {
                ports.put(faces[i], types[primitive[i]]);
            }
        }
        return ports;
    }
}