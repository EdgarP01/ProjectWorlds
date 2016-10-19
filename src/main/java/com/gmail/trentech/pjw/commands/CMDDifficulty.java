package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;

public class CMDDifficulty implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!args.hasAny("difficulty")) {
			src.sendMessage(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.WHITE, properties.getDifficulty().getName().toUpperCase()));	
			return CommandResult.success();
		}
		Difficulty difficulty = args.<Difficulty> getOne("difficulty").get();
		
		properties.setDifficulty(difficulty);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set difficulty of ", properties.getWorldName(), " to ", TextColors.YELLOW, difficulty.getTranslation().get().toUpperCase()));
		
		return CommandResult.success();
	}
}
