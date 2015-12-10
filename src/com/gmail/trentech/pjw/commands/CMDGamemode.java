package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Utils;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDGamemode implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
			src.sendMessage(Texts.of(TextColors.GOLD, "/world gamemode <world>"));
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();
		
		if(worldName.equalsIgnoreCase("@w")){
			if(src instanceof Player){
				worldName = ((Player) src).getWorld().getName();
			}
		}
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		ConfigManager loader = new ConfigManager("worlds.conf");
		ConfigurationNode config = loader.getConfig();
		
		if(!args.hasAny("value")) {
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "GameMode: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Gamemode").getString()));
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Command: ",TextColors.GOLD, "/world gamemode <world> [value]"));
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			return CommandResult.success();
		}

		GameMode gamemode = Utils.getGameMode(args.<String>getOne("value").get().toUpperCase());

		config.getNode("Worlds", worldName, "Gamemode").setValue(gamemode.getName().toUpperCase());
		loader.save();
		
		world.getProperties().setGameMode(gamemode);
		
		src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Set gamemode of world ", worldName, " to ", gamemode.getName().toUpperCase()));
		
		return CommandResult.success();
	}

}
