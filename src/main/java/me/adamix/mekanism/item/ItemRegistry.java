package me.adamix.mekanism.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.adamix.mekanism.infusion.InfusionType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@Getter
@Accessors(fluent = true)
public class ItemRegistry {
    // Icons
    private final ItemStack closeIcon = paperOne("Close", "close_icon");
    private final ItemStack ejectIcon = paperOne("Eject is not yet supported", "eject_icon");
    private final ItemStack sideConfigIcon = paperOne("Side Config", "slots_icon");

    // Indicator frames
    private final ItemStack[] thickIndicatorFrames = frames(24, "thick_indicator");
    private final ItemStack[] infuserArrowIndicatorFrames = frames(33, "infuser_arrow_indicator");
    private final ItemStack[] infusingIndicatorFrames = createInfusingIndicatorFrames();
    private final ItemStack[][] energyCubeIndicatorFrames = createEnergyCubeIndicatorFrames();
    private final ItemStack[] verticalEnergyIndicatorFrames = frames(49, "vertical_energy_indicator");

    private @NotNull ItemStack paperOne(
            @NotNull String name,
            @NotNull String modelId
    ) {
        var closeIcon = ItemStack.of(Material.PAPER);
        closeIcon.editMeta(meta -> {
            meta.customName(Component.text(name));
        });
        closeIcon.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", modelId));

        return closeIcon;
    }

    private @NotNull ItemStack[] frames(int count, @NotNull String itemModel) {
        ItemStack[] frames = new ItemStack[count];
        for (int i = 0; i < count; i++) {
            ItemStack item = ItemStack.of(Material.PAPER);
            CustomModelData customModelData = CustomModelData.customModelData()
                    .addString(Integer.toString(i))
                    .build();
            item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
            item.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", itemModel));

            frames[i] = item;
        }
        return frames;
    }

    // Doing this manually, cause creating generic function would be too complex. Im too lazy rn :(
    private ItemStack[] createInfusingIndicatorFrames() {
        List<ItemStack> infusionFrames = new ArrayList<>();

        for (InfusionType infusionType : InfusionType.values()) {
            for (int i = 0; i < 49; i++) {
                ItemStack item = ItemStack.of(Material.PAPER);
                CustomModelData customModelData = CustomModelData.customModelData()
                        .addString(Integer.toString(i))
                        .build();
                item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
                item.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "infuser_indicator/" + infusionType.name().toLowerCase()));

                infusionFrames.add(item);
            }
        }

        return infusionFrames.toArray(new ItemStack[0]);
    }

    private ItemStack[][] createEnergyCubeIndicatorFrames() {
        ItemStack[][] frames = new ItemStack[15][11];

        for (int i = 0; i < 15; i++) {
            ItemStack[] slotFrames = new ItemStack[11];

            for (int lvl = 0; lvl < 11; lvl++) {
                int cmd = i << 4 | lvl;

                ItemStack item = ItemStack.of(Material.PAPER);
                item.editMeta(meta -> {
                    meta.customName(
                            Component.text("Indicator")
                    );
                });
                CustomModelData customModelData = CustomModelData.customModelData()
                        .addString(Integer.toString(cmd))
                        .build();
                item.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);
                item.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", "energy_indicator"));

                slotFrames[lvl] = item;
            }

            frames[i] = slotFrames;
        }
        return frames;
    }
}
