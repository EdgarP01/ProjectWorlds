package com.gmail.trentech.pjw.commands.old;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.utils.Utils;

public class CMDTime implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("world time").get();
		
		if (args.hasAny("help")) {		
			help.execute(src);
			return CommandResult.empty();
		}
		
		if (!args.hasAny("world")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!args.hasAny("time")) {
			src.sendMessage(Text.of(TextColors.GREEN, "Time: ", TextColors.WHITE, Utils.getTime(properties.getWorldTime()), TextColors.GREEN, " Ticks: ", TextColors.WHITE, properties.getWorldTime() % 24000));			
			return CommandResult.success();
		}
		long time = args.<Long> getOne("time").get();

		if(time < 0 || time > 24000) {
			throw new CommandException(Text.of(TextColors.RED, "Time value must be between 0 - 24000"), false);
		}

		properties.setWorldTime(time);
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set time of ", properties.getWorldName(), " to ", TextColors.YELLOW, Utils.getTime(properties.getWorldTime())));

		return CommandResult.success();
	}
}
