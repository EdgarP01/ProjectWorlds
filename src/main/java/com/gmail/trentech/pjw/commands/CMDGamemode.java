package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.utils.Gamemode;
import com.gmail.trentech.pjw.utils.Help;

public class CMDGamemode implements CommandExecutor {

	public CMDGamemode() {
		new Help("world gamemode", "gamemode", "Change gamemode of the specified world", false)
			.setPermission("pjw.cmd.world.gamemode")
			.setUsage("/world gamemode <world> [gamemode]\n /w g <world> [gamemode]")
			.setExample("/world gamemode\n /world gamemode MyWorld SURVIVAL")
			.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!args.hasAny("gamemode")) {
			src.sendMessage(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.WHITE, properties.getGameMode().getName().toUpperCase()));
			return CommandResult.success();
		}
		Gamemode gamemode = args.<Gamemode> getOne("gamemode").get();
		
		properties.setGameMode(gamemode.getGameMode());
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set gamemode of ", properties.getWorldName(), " to ", TextColors.YELLOW, gamemode.getGameMode().getName().toUpperCase()));

		return CommandResult.success();
	}
}