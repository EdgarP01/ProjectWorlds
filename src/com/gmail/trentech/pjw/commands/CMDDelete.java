package com.gmail.trentech.pjw.commands;

import java.util.concurrent.ExecutionException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDDelete implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
			src.sendMessage(Texts.of(TextColors.GOLD, "/world delete <world>"));
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();

		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, worldName, " must be unloaded before you can rename"));
			return CommandResult.empty();
		}

		for(WorldProperties worldInfo : Main.getGame().getServer().getUnloadedWorlds()){
			if(worldInfo.getWorldName().equalsIgnoreCase(worldName)){
				try {
					if(Main.getGame().getServer().deleteWorld(worldInfo).get()){
						ConfigManager loader = new ConfigManager("worlds.conf");
						ConfigurationNode config = loader.getConfig();

						config.getNode("Worlds", worldName).setValue(null);
						loader.save();
						
						src.sendMessage(Texts.of(TextColors.DARK_GREEN, worldName, " deleted successfully"));
						return CommandResult.success();
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		
		src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not locate ", worldName));
		
		return CommandResult.empty();
	}

}
