package me.adamix.mekanism.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.component.Component;
import me.adamix.mekanism.block.instance.BlockInstanceService;
import me.adamix.mekanism.network.NetworkService;
import me.adamix.mekanism.type.WorldPos;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class DebugCommand implements BasicCommand {
    private final NetworkService networkService;
    private final BlockInstanceService blockInstanceService;

    @Override
    public void execute(
            @NotNull CommandSourceStack source,
            String @NotNull [] args
    ) {
        if (!(source.getSender() instanceof Player player)) {
            return;
        }

        network(player);
        instance(player);
    }

    private void network(@NotNull Player player) {
        Block block = player.getTargetBlockExact(10);
        BlockFace face = player.getTargetBlockFace(10);
        if (block == null) {
            player.sendMessage("No block found");
            return;
        }

        var opt = networkService.getNetwork(WorldPos.of(block), face);
        if (opt.isEmpty()) {
            player.sendMessage("No network found for this block");
            return;
        }

        var network = opt.get();
        player.sendMessage("===================");
        player.sendMessage("Network id: " + network.getId());
        player.sendMessage("===================");
    }

    private void instance(@NotNull Player player) {
        Block block = player.getTargetBlockExact(10);
        if (block == null) {
            player.sendMessage("No block found");
            return;
        }

        var opt = blockInstanceService.get(WorldPos.of(block));
        if (opt.isEmpty()) {
            player.sendMessage("No instance found");
            return;
        }

        var instance = opt.get();

        player.sendMessage("Components:");
        for (Component component : instance.components()) {
            player.sendMessage(" - " + component.toString());
        }
    }
}
