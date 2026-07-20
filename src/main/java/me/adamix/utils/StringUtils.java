package me.adamix.utils;

import org.jetbrains.annotations.NotNull;

public class StringUtils {
    public static @NotNull String capitalizeFirst(@NotNull String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
