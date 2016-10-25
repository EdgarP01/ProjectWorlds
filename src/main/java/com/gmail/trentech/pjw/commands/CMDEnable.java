package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

public class CMDEnable implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!args.hasAny("true|false")) {
			src.sendMessage(Text.of(TextColors.GREEN, "Enabled: ", TextColors.WHITE, properties.isEnabled()));
			return CommandResult.success();
		}
		boolean value = args.<Boolean> getOne("true|false").get();
		
		properties.setEnabled(value);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set enabled of ", properties.getWorldName(), " to ", value));

		return CommandResult.success();
	}
}
