package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
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
				Text t1 = Texts.of(TextColors.GOLD, "/world properties ");
				Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter world or @w for current world"))).append(Texts.of("[world] ")).build();
				src.sendMessage(Texts.of(t1,t2));
				return CommandResult.empty();
			}
			Player player = (Player) src;		
			worldName = player.getWorld().getName();
		}

		if(config.getNode("Worlds", worldName) == null){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		if(!Main.getGame().getServer().getWorldProperties(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		WorldProperties properties = Main.getGame().getServer().getWorldProperties(worldName).get();
		
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
		src.sendMessage(Texts.of(TextColors.GOLD, "                    World Properties"));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Name: ", TextColors.GOLD, worldName));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "UUID: ", TextColors.GOLD, properties.getUniqueId().toString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Dimension Type: ", TextColors.GOLD, properties.getDimensionType().getName().toUpperCase()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Generator Type: ", TextColors.GOLD, properties.getGeneratorType().getName().toUpperCase()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Difficulty: ", TextColors.GOLD, properties.getDifficulty().getName().toUpperCase()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "PVP: ", TextColors.GOLD, config.getNode("Worlds", worldName, "PVP").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Respawn World: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Respawn-World").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "GameMode: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Gamemode").getString()));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Seed: ", TextColors.GOLD, properties.getSeed()));
		if(properties.doesKeepSpawnLoaded()){
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Keep Spawn Loaded: ", TextColors.GOLD, "true"));
		}else{
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Keep Spawn Loaded: ", TextColors.GOLD, "false"));
		}
		if(properties.isHardcore()){
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Hardcore: ", TextColors.GOLD, "true"));
		}else{
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Hardcore: ", TextColors.GOLD, "false"));
		}
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
