package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDProperties implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		ConfigurationNode config = new ConfigManager("worlds.conf").getConfig();
		
		String worldName;
		if(args.hasAny("name")) {
			worldName = args.<String>getOne("name").get();
		}else{
			if(!(src instanceof Player)){
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
				src.sendMessage(Texts.of(TextColors.GOLD, "/world properties [world]"));
				return CommandResult.empty();
			}
			Player player = (Player) src;		
			worldName = player.getWorld().getName();
		}

		if(config.getNode("Worlds", worldName) == null){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
		src.sendMessage(Texts.of(TextColors.GOLD, "                 ",worldName, " Properties:"));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "UUID: ", TextColors.GOLD, config.getNode("Worlds", worldName, "UUID").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Dimension Type: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Dimension-Type").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Generator Type: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Generator-Type").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Difficulty: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Difficulty").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "GameMode: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Gamemode").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Seed: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Seed").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Keep Spawn Loaded: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Keep-Spawn-Loaded").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Hardcore: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Hardcore").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Time:"));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "    - Lock: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Time", "Lock").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "    - Set: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Time", "Set").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Weather:"));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "    - Lock: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Weather", "Lock").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "    - Set: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Weather", "Set").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
		
		return CommandResult.success();
	}

}
