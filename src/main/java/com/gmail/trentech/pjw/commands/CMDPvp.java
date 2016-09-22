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

public class CMDPvp implements CommandExecutor {

	public CMDPvp() {
		Help help = new Help("pvp", "pvp", " Toggle on and off pvp for world");
		help.setPermission("pjw.cmd.world.pvp");
		help.setSyntax(" /world pvp <world> [value]\n /w p <world> [value]");
		help.setExample(" /world pvp MyWorld\n /world pvp MyWorld true");
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

		properties.setPVPEnabled(value);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set pvp of ", properties.getWorldName(), " to ", TextColors.YELLOW, value));

		return CommandResult.success();
	}
}