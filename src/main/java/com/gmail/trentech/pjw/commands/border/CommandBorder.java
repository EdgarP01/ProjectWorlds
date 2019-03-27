package com.gmail.trentech.pjw.commands.border;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjc.help.Help;

public class CommandBorder implements CommandExecutor {
	
	private CommandSpec commandSpec = CommandSpec.builder()
			.description(Text.of("border command"))
			.permission("pjw.cmd.world.border")
			.child(new CommandCenter(), "center", "cr")
			.child(new CommandDamage(), "damage", "dg")
			.child(new CommandDiameter(), "diameter", "d")
			.child(new CommandGenerate(), "generate", "g")
			.child(new CommandInfo(), "info", "i")
			.child(new CommandWarning(), "warning", "w")
			.executor(this)
			.build();
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help.executeList(src, Help.get("world border").get().getChildren());
		src.sendMessage(Text.of(TextColors.YELLOW, " /world border <rawCommand> --help"));	
		return CommandResult.success();
	}
	
	public CommandSpec getCommandSpec() {
		return commandSpec;
	}

}
