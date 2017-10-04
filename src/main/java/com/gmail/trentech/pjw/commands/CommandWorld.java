package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjc.help.Help;

public class CommandWorld implements CommandExecutor {
	
	private CommandSpec commandSpec = CommandSpec.builder()
			.description(Text.of("Base command"))
			.permission("pjw.cmd.world")
			.child(new CommandCreate(), "create", "cr")
			.child(new CommandRegen(), "regen", "r")
			.child(new CommandRemove(), "remove", "rm")
			.child(new CommandProperties(), "properties", "pp")
			.child(new CommandDifficulty(), "difficulty", "df")
			.child(new CommandSetSpawn(), "setspawn", "s")
			.child(new CommandHardcore(), "hardcore", "h")
			.child(new CommandTime(), "time", "t")
			.child(new CommandWeather(), "weather", "w")
			.child(new CommandPvp(), "pvp", "p")
			.child(new CommandKeepSpawnLoaded(), "keepspawnloaded", "k")
			.child(new CommandUseMapFeatures(), "usemapfeatures", "f")
			.child(new CommandLoadOnStartup(), "loadonstartup", "los")
			.child(new CommandList(), "list", "ls")
			.child(new CommandTeleport(), "teleport", "tp")
			.child(new CommandCopy(), "copy", "cp")
			.child(new CommandRename(), "rename", "rn")
			.child(new CommandUnload(), "unload", "u")
			.child(new CommandLoad(), "load", "l")
			.child(new CommandImport(), "import", "i")
			.child(new CommandEnable(), "enable", "e")
			.child(new CommandGamemode(), "gamemode", "gm")
			.child(new CommandGamerule(), "gamerule", "gr")
			.child(new CommandModifier(), "modifier", "m")
			.executor(this)
			.build();
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help.executeList(src, Help.get("world").get().getChildren());
		src.sendMessage(Text.of(TextColors.YELLOW, " /world <rawCommand> --help"));	
		return CommandResult.success();
	}
	
	public CommandSpec getCommandSpec() {
		return commandSpec;
	}

}
