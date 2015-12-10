package com.gmail.trentech.pjw.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDLoad implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
			src.sendMessage(Texts.of(TextColors.GOLD, "/world load <world>"));
			return CommandResult.empty();
		}

		String worldName = args.<String>getOne("name").get();
		
		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " is already loaded"));
			return CommandResult.empty();
		}
		
		for(WorldProperties world : Main.getGame().getServer().getUnloadedWorlds()){
			if(world.getWorldName().equalsIgnoreCase(worldName)){
				Optional<World> load = Main.getGame().getServer().loadWorld(worldName);
				
				if(!load.isPresent()){
					src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not load ", worldName));
					return CommandResult.empty();
				}
				
				ConfigManager loader = new ConfigManager("worlds.conf");
				ConfigurationNode config = loader.getConfig();
				
				if(config.getNode("Worlds", worldName).getString() == null){
					WorldProperties properties = load.get().getProperties();
					
					config.getNode("Worlds", worldName, "UUID").setValue(world.getUniqueId().toString());
					config.getNode("Worlds", worldName, "Dimension-Type").setValue(properties.getDimensionType().getName().toUpperCase());
					config.getNode("Worlds", worldName, "Generator-Type").setValue(properties.getGeneratorType().getName().toUpperCase());
					config.getNode("Worlds", worldName, "Seed").setValue(properties.getSeed());
					config.getNode("Worlds", worldName, "Difficulty").setValue(properties.getDifficulty().getName().toUpperCase());
					config.getNode("Worlds", worldName, "Gamemode").setValue(properties.getGameMode().getName().toUpperCase());
					config.getNode("Worlds", worldName, "Keep-Spawn-Loaded").setValue(false);
					config.getNode("Worlds", worldName, "Hardcore").setValue(false);
					config.getNode("Worlds", worldName, "Time", "Lock").setValue(false);
					config.getNode("Worlds", worldName, "Time", "Set").setValue(6000);
					config.getNode("Worlds", worldName, "Weather", "Lock").setValue(false);
					config.getNode("Worlds", worldName, "Weather", "Set").setValue("CLEAR");	

					loader.save();
				}
				src.sendMessage(Texts.of(TextColors.DARK_GREEN, worldName, " loaded successfully"));				
				return CommandResult.success();				
			}
		}
		src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not locate ", worldName));
		return CommandResult.empty();
	}

}
