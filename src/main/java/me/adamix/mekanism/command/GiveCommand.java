package me.adamix.mekanism.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.block.registry.BlockDefinition;
import me.adamix.mekanism.block.registry.BlockRegistry;
import me.adamix.mekanism.data.MekanismKeys;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class GiveCommand implements BasicCommand {
    private final BlockRegistry blockRegistry;

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) {
            return;
        }

        ItemStack itemStack = ItemStack.of(Material.STONE);

        if (args.length < 1) {
            player.sendMessage("go home");
            return;
        }


        MekanismBlockType type = MekanismBlockType.valueOf(args[0].toUpperCase());
        BlockDefinition definition = blockRegistry.get(type).orElseThrow();

        itemStack.setData(DataComponentTypes.ITEM_MODEL, Key.key(definition.itemModel()));
        CustomModelData customModelData = CustomModelData.customModelData()
                .addString("0")
                .build();
        itemStack.setData(DataComponentTypes.CUSTOM_MODEL_DATA, customModelData);

        itemStack.editMeta(meta -> {
            var pdc = meta.getPersistentDataContainer();
            pdc.set(MekanismKeys.BLOCK_TYPE_KEY, MekanismKeys.BLOCK_TYPE, type);
        });

        player.getInventory().addItem(itemStack);
    }
}
