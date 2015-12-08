package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.managers.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDHardcore implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
			src.sendMessage(Texts.of(TextColors.GOLD, "/world hardcore <world>"));
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		ConfigManager loader = new ConfigManager();
		ConfigurationNode config = loader.getConfig();
		
		if(world.getProperties().isHardcore()){
			world.getProperties().setHardcore(false);
			config.getNode("Worlds", world.getName(), "Hardcore").setValue(false);
			src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Set hardcore of world ", worldName, " to false"));
		}else{
			world.getProperties().setHardcore(true);
			config.getNode("Worlds", world.getName(), "Hardcore").setValue(true);
			src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Set hardcore of world ", worldName, " to true"));
		}
		
		loader.save();
		return CommandResult.success();
	}

}
