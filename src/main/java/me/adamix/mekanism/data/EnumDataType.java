package me.adamix.mekanism.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class EnumDataType<T extends Enum<T>> implements PersistentDataType<String, T> {

    private final Class<T> enumClass;

    public EnumDataType(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<T> getComplexType() {
        return enumClass;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull T complex, @NotNull PersistentDataAdapterContext context) {
        return complex.name();
    }

    @Override
    public @NotNull T fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return Enum.valueOf(enumClass, primitive);
    }
}