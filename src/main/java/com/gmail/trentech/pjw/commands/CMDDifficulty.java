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

import com.gmail.trentech.pjw.utils.Help;

public class CMDDifficulty implements CommandExecutor {

	public CMDDifficulty() {
		Help help = new Help("difficulty", "difficulty", " Set the difficulty level for each world");
		help.setSyntax(" /world difficulty <world> [value]\n /w df <world> [value]");
		help.setExample(" /world difficulty MyWorld\n /world difficulty MyWorld HARD\n /world difficulty @w PEACEFUL\n /world difficulty @a SURVIVAL");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!args.hasAny("difficulty")) {
			src.sendMessage(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.WHITE, properties.getDifficulty().getName().toUpperCase()));	
			return CommandResult.success();
		}
		Difficulty difficulty = args.<Difficulty> getOne("difficulty").get();
		
		properties.setDifficulty(difficulty);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set difficulty of ", properties.getWorldName(), " to ", TextColors.YELLOW, difficulty.getName().toUpperCase()));
		
		return CommandResult.success();
	}
}
