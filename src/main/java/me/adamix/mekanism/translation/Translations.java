package me.adamix.mekanism.translation;

import org.jetbrains.annotations.NotNull;

public class Translations {
    public enum Titles {
        ENERGY_CONFIG('\uEE02'),

        SOLAR_GENERATOR('\uEE01'),

        ENERGY_CUBE('\uEE00'),

        ENERGIZED_SMELTER('\uEE04'),
        METALLURGIC_INFUSER('\uEE03');

        private final char character;

        Titles(char character) {
            this.character = character;
        }
    }

    public enum Spaces {
        NEG_1('\uEE01'),
        NEG_2('\uEE02'),
        NEG_3('\uEE03'),
        NEG_4('\uEE04'),
        NEG_5('\uEE05'),
        NEG_6('\uEE06'),
        NEG_7('\uEE07'),
        NEG_8('\uEE08'),
        NEG_9('\uEE09');

        private final char character;

        Spaces(char character) {
            this.character = character;
        }
    }

    public @NotNull String menuTitle(@NotNull Spaces spaces, @NotNull Titles titles) {
        return "<font:mekanism:spaces>" + spaces.character + "</font><font:mekanism:menu_titles>" + titles.character + "</font>";
    }
}
