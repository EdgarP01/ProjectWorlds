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

public class CMDLoadOnStartup implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("world loadonstartup").get();
		
		if (args.hasAny("help")) {		
			help.execute(src);
			return CommandResult.empty();
		}
		
		if (!args.hasAny("world")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!args.hasAny("true|false")) {
			src.sendMessage(Text.of(TextColors.GREEN, "Load on Startup: ", TextColors.WHITE, Boolean.toString(properties.loadOnStartup()).toUpperCase()));
			return CommandResult.success();
		}
		boolean value = args.<Boolean> getOne("true|false").get();

		properties.setLoadOnStartup(value);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set load on startup of ", properties.getWorldName(), " to ", TextColors.YELLOW, value));

		return CommandResult.success();
	}
}
