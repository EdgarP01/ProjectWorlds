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

public class CMDHardcore implements CommandExecutor {

	public CMDHardcore() {
		Help help = new Help("hardcore", "hardcore", " Toggle on and off hardcore mode for world");
		help.setPermission("pjw.cmd.world.hardcore");
		help.setSyntax(" /world hardcore <world> [value]\n /w h <world> [value]");
		help.setExample(" /world hardcore MyWorld\n /world hardcore MyWorld false\n /world hardcore @w true");
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

		properties.setHardcore(value);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set hardcore of ", properties.getWorldName(), " to ", TextColors.YELLOW, value));

		return CommandResult.success();
	}
}
