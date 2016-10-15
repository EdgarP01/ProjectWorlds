package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

import com.gmail.trentech.pjw.utils.Help;

public class CMDHelp implements CommandExecutor {

	public CMDHelp() {
		Help help = new Help("world help", "help", " Get help with all commands in Project Worlds", false);
		help.setPermission("pjw.cmd.world");
		help.setSyntax(" /world help <rawCommand>");
		help.setExample(" /world help world create");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = args.<Help>getOne("rawCommand").get();
		help.execute(src);

		return CommandResult.success();
	}
}
