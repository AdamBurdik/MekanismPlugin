package me.adamix.mekanism.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.adamix.mekanism.block.MekanismBlockType;
import me.adamix.mekanism.data.MekanismKeys;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand implements BasicCommand {
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

        itemStack.setData(DataComponentTypes.ITEM_MODEL, Key.key("mekanism", args[0]));

        MekanismBlockType type = MekanismBlockType.valueOf(args[0].toUpperCase());

        itemStack.editMeta(meta -> {
            var pdc = meta.getPersistentDataContainer();
            pdc.set(MekanismKeys.BLOCK_TYPE_KEY, MekanismKeys.BLOCK_TYPE, type);
        });

        player.getInventory().addItem(itemStack);
    }
}
