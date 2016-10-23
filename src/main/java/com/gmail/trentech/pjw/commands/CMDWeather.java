package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

public class CMDWeather implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!args.hasAny("clear|rain|thunder")) {
			if(properties.isRaining()) {
				src.sendMessage(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.WHITE, "rain"));
			} else if(properties.isThundering()) {
				src.sendMessage(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.WHITE, "thunder"));
			} else {
				src.sendMessage(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.WHITE, "clear"));
			}
			
			return CommandResult.success();
		}
		String value = args.<String> getOne("clear|rain|thunder").get();

		if(value.equalsIgnoreCase("clear")) {
			properties.setRaining(false);
			properties.setThundering(false);
		} else if(value.equalsIgnoreCase("rain")) {
			properties.setRaining(true);
		} else if(value.equalsIgnoreCase("thunder")) {
			properties.setThundering(true);
		} else {
			throw new CommandException(Text.of(TextColors.RED, "Not a valid value"), true);
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set wheather of ", properties.getWorldName(), " to ", TextColors.YELLOW, value));

		return CommandResult.success();
	}
}
