package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

public class CMDTime implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!args.hasAny("time")) {
			long time = properties.getWorldTime();

			if(time >= 24000) {
				for(int i = 0;(time / 24000) > i; i++) {
					time = time - 24000;
				}
			}
			src.sendMessage(Text.of(TextColors.GREEN, "Time: ", TextColors.WHITE, time));
			
			return CommandResult.success();
		}
		long time = args.<Long> getOne("time").get();

		if(time < 0 || time > 24000) {
			throw new CommandException(Text.of(TextColors.RED, "Time value must be between 0 - 24000"), false);
		}
		
		properties.setWorldTime(time);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set time of ", properties.getWorldName(), " to ", TextColors.YELLOW, time));

		return CommandResult.success();
	}
}
