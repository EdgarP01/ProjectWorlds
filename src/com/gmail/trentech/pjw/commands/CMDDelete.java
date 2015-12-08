package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
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

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		for(Entity entity : world.getEntities()){
			if(entity instanceof Player){
				Player player = (Player) entity;
				WorldProperties properties = Main.getGame().getServer().getDefaultWorld().get();
				player.setLocationSafely(Main.getGame().getServer().getWorld(properties.getWorldName()).get().getSpawnLocation());
				player.sendMessage(Texts.of(TextColors.GOLD, "World you were in is unloaded"));
			}
		}
		
		if(!Main.getGame().getServer().unloadWorld(world)){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not unload ", worldName));
			return CommandResult.empty();	
		}

		Main.getGame().getServer().deleteWorld(world.getProperties());	
		
		ConfigManager loader = new ConfigManager();
		ConfigurationNode config = loader.getConfig();

		config.getNode("Worlds").removeChild(worldName);
		loader.save();
		
		src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Deleted ", worldName));
		return CommandResult.success();
	}

}
