package me.adamix.mekanism.type.pdc;

import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.type.BlockPos;
import me.adamix.mekanism.type.StoredBlock;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StoredBlockListDataType implements PersistentDataType<byte[], List<StoredBlock>> {

    public static final StoredBlockListDataType INSTANCE = new StoredBlockListDataType();

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull Class<List<StoredBlock>> getComplexType() {
        return (Class<List<StoredBlock>>) (Class<?>) List.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull List<StoredBlock> complex, @NotNull PersistentDataAdapterContext context) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            dos.writeInt(complex.size());

            for (StoredBlock block : complex) {
                // Position
                dos.writeInt(block.pos().x());
                dos.writeInt(block.pos().y());
                dos.writeInt(block.pos().z());

                // Block Type
                dos.writeUTF(block.type().name());

                // Entity UUID (Most & Least Significant Bits)
                dos.writeLong(block.entityId().getMostSignificantBits());
                dos.writeLong(block.entityId().getLeastSignificantBits());
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize StoredBlock list to PDC", e);
        }
    }

    @Override
    public @NotNull List<StoredBlock> fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(primitive);
             DataInputStream dis = new DataInputStream(bais)) {

            int size = dis.readInt();
            List<StoredBlock> list = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                int x = dis.readInt();
                int y = dis.readInt();
                int z = dis.readInt();
                String typeName = dis.readUTF();

                // Read UUID bits
                long mostSig = dis.readLong();
                long leastSig = dis.readLong();
                UUID entityId = new UUID(mostSig, leastSig);

                BlockPos pos = new BlockPos(x, y, z);
                MekanismBlockType type = MekanismBlockType.valueOf(typeName);

                list.add(new StoredBlock(pos, type, entityId));
            }

            return list;
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize StoredBlock list from PDC", e);
        }
    }
}