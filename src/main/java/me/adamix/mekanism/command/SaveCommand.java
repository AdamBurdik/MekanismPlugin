package me.adamix.mekanism.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import me.adamix.mekanism.block.persistence.BlockPersistenceService;

@RequiredArgsConstructor
public class SaveCommand implements BasicCommand {
    private final BlockPersistenceService blockPersistenceService;

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        blockPersistenceService.saveAll();
        source.getSender().sendMessage("Saved");
    }
}
