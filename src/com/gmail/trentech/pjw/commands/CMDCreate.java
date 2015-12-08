package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBuilder;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Utils;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDCreate implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
			src.sendMessage(Texts.of(TextColors.GOLD, "/world create <world> <type> <generator> [seed]"));
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();

		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " already exists"));
			return CommandResult.empty();
		}
		
		WorldBuilder builder = Main.getGame().getRegistry().createBuilder(WorldBuilder.class).name(worldName);

		if(!args.hasAny("type")) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
			src.sendMessage(Texts.of(TextColors.YELLOW, "/w c <name> <type> <generator> [seed]"));
			return CommandResult.empty();
		}
		builder.dimensionType(Utils.getDimensionType(args.<String>getOne("type").get().toUpperCase()));
		
		if(!args.hasAny("generator")) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
			src.sendMessage(Texts.of(TextColors.YELLOW, "/w c <name> <type> <generator> [seed]"));
		}
		builder.generator(Utils.getGeneratorType(args.<String>getOne("generator").get().toUpperCase()));

		if(args.hasAny("seed")) {
			try{
				long seed = args.<String>getOne("seed").get().hashCode();
				builder.seed(seed);
			}catch(Exception e){}
		}

		builder.enabled(true).loadsOnStartup(true).build();
		
		World world = Main.getGame().getServer().getWorld(worldName).get();
		WorldProperties properties = world.getProperties();
		
		ConfigManager loader = new ConfigManager();
		ConfigurationNode config = loader.getConfig();

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
		
		return CommandResult.success();
	}

}
