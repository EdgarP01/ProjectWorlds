package com.gmail.trentech.pjw.commands;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDCopy implements CommandExecutor {
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("old")) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
			src.sendMessage(Texts.of(TextColors.GOLD, "/world copy <world> <world>"));
			return CommandResult.empty();
		}

		String oldWorldName = args.<String>getOne("old").get();
		
		if(oldWorldName.equalsIgnoreCase("@w")){
			if(src instanceof Player){
				oldWorldName = ((Player) src).getWorld().getName();
			}
		}
		
		if(!args.hasAny("new")) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
			src.sendMessage(Texts.of(TextColors.GOLD, "/world copy <world> <world>"));
			return CommandResult.empty();
		}

		String newWorldName = args.<String>getOne("new").get();
		
		for(WorldProperties world : Main.getGame().getServer().getAllWorldProperties()){
			if(world.getWorldName().equalsIgnoreCase(newWorldName)){
				src.sendMessage(Texts.of(TextColors.DARK_RED, newWorldName, " already exists"));
				return CommandResult.empty();
			}
		}
		
		if(!Main.getGame().getServer().getWorld(oldWorldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", oldWorldName, " does not exists"));
			return CommandResult.empty();
		}

		Optional<WorldProperties> copy = null;
		try {
			copy = Main.getGame().getServer().copyWorld(Main.getGame().getServer().getWorld(oldWorldName).get().getProperties(), newWorldName).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		if(!copy.isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not copy ", oldWorldName));
			return CommandResult.empty();
		}

		Optional<World> load = Main.getGame().getServer().loadWorld(newWorldName);
		if(!load.isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not load ", newWorldName));
			return CommandResult.empty();
		}

		World world = load.get();
		WorldProperties properties = world.getProperties();
		
		ConfigManager loader = new ConfigManager("worlds.conf");
		ConfigurationNode config = loader.getConfig();

		config.getNode("Worlds", newWorldName, "UUID").setValue(world.getUniqueId().toString());
		config.getNode("Worlds", newWorldName, "Dimension-Type").setValue(properties.getDimensionType().getName().toUpperCase());
		config.getNode("Worlds", newWorldName, "Generator-Type").setValue(properties.getGeneratorType().getName().toUpperCase());
		config.getNode("Worlds", newWorldName, "Seed").setValue(properties.getSeed());
		config.getNode("Worlds", newWorldName, "Difficulty").setValue(properties.getDifficulty().getName().toUpperCase());
		config.getNode("Worlds", newWorldName, "Gamemode").setValue(properties.getGameMode().getName().toUpperCase());
		config.getNode("Worlds", newWorldName, "Keep-Spawn-Loaded").setValue(config.getNode("Worlds", oldWorldName, "Keep-Spawn-Loaded").getBoolean());
		config.getNode("Worlds", newWorldName, "Hardcore").setValue(config.getNode("Worlds", oldWorldName, "Hardcore").getBoolean());
		config.getNode("Worlds", newWorldName, "Time", "Lock").setValue(config.getNode("Worlds", oldWorldName, "Time", "Lock").getBoolean());
		config.getNode("Worlds", newWorldName, "Time", "Set").setValue(config.getNode("Worlds", oldWorldName, "Time", "Set").getString());
		config.getNode("Worlds", newWorldName, "Weather", "Lock").setValue(config.getNode("Worlds", oldWorldName, "Weather", "Lock").getBoolean());
		config.getNode("Worlds", newWorldName, "Weather", "Set").setValue(config.getNode("Worlds", oldWorldName, "Weather", "Set").getString());	

		loader.save();

		src.sendMessage(Texts.of(TextColors.DARK_GREEN, oldWorldName, " copied to ", newWorldName));
		
		return CommandResult.success();
	}
}
