package com.gmail.trentech.pjw.commands.old;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjc.help.Help;

public class CMDWorld implements CommandExecutor {
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help.executeList(src, Help.get("world").get().getChildren());
		src.sendMessage(Text.of(TextColors.YELLOW, " /helpme world <rawCommand>"));	
		return CommandResult.success();
	}

}
