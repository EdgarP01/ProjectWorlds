package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.utils.Help;

public class CMDKeepSpawnLoaded implements CommandExecutor {

	public CMDKeepSpawnLoaded() {
		Help help = new Help("keepspawnloaded", "keepspawnloaded", " Keeps spawn point of world loaded in memory");
		help.setPermission("pjw.cmd.world.keepspawnloaded");
		help.setSyntax(" /world keepspawnloaded <world> [value]\n /w k <world> [value]");
		help.setExample(" /world keepspawnloaded MyWorld\n /world keepspawnloaded MyWorld true");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!args.hasAny("value")) {
			src.sendMessage(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.WHITE, Boolean.toString(properties.isHardcore()).toUpperCase()));
			return CommandResult.success();
		}
		boolean value = args.<Boolean> getOne("boolean").get();

		properties.setKeepSpawnLoaded(value);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set keep spawn loaded of ", properties.getWorldName(), " to ", TextColors.YELLOW, value));

		return CommandResult.success();
	}
}
