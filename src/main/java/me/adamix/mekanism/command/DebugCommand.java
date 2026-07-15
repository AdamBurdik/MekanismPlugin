package me.adamix.mekanism.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.adamix.mekanism.network.NetworkService;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DebugCommand implements BasicCommand {
    private final NetworkService networkService;

    public DebugCommand(@NotNull NetworkService networkService) {
        this.networkService = networkService;
    }

    @Override
    public void execute(
            @NotNull CommandSourceStack source,
            String @NotNull [] args
    ) {
        if (!(source.getSender() instanceof Player player)) {
            return;
        }

        Block block = player.getTargetBlockExact(10);
        BlockFace face = player.getTargetBlockFace(10);
        if (block == null) {
            player.sendMessage("No block found");
            return;
        }

        var opt = networkService.getNetwork(block.getLocation(), face);
        if (opt.isEmpty()) {
            player.sendMessage("No network found for this block");
            return;
        }

        var network = opt.get();
        player.sendMessage("===================");
        player.sendMessage("Network id: " + network.getId());
        player.sendMessage("===================");
    }
}
