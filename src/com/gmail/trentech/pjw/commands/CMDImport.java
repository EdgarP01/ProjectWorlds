package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public class CMDImport implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
//		if(!args.hasAny("name")) {
//			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
//			src.sendMessage(Texts.of(TextColors.GOLD, "/world import <world>"));
//			return CommandResult.empty();
//		}
//
//		String worldName = args.<String>getOne("name").get();
//		
//		for(WorldProperties world : Main.getGame().getServer().getAllWorldProperties()){
//			if(world.getWorldName().equalsIgnoreCase(worldName)){
//				src.sendMessage(Texts.of(TextColors.DARK_RED, worldName, " already exists"));
//				return CommandResult.empty();
//			}
//		}
//
//		try {
//			IOManager.init(worldName);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		Optional<World> load = Main.getGame().getServer().loadWorld(worldName);
//		
//		if(!load.isPresent()){
//			src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not load ", worldName));
//			return CommandResult.empty();
//		}
//		
//		World world = load.get();
//		WorldProperties properties = world.getProperties();
//		
//		ConfigManager loader = new ConfigManager("worlds.conf");
//		ConfigurationNode config = loader.getConfig();
//
//		config.getNode("Worlds", worldName, "UUID").setValue(world.getUniqueId().toString());
//		config.getNode("Worlds", worldName, "Dimension-Type").setValue(properties.getDimensionType().getName().toUpperCase());
//		config.getNode("Worlds", worldName, "Generator-Type").setValue(properties.getGeneratorType().getName().toUpperCase());
//		config.getNode("Worlds", worldName, "Seed").setValue(properties.getSeed());
//		config.getNode("Worlds", worldName, "Difficulty").setValue(properties.getDifficulty().getName().toUpperCase());
//		config.getNode("Worlds", worldName, "Gamemode").setValue(properties.getGameMode().getName().toUpperCase());
//		config.getNode("Worlds", worldName, "Keep-Spawn-Loaded").setValue(false);
//		config.getNode("Worlds", worldName, "Hardcore").setValue(false);
//		config.getNode("Worlds", worldName, "Time", "Lock").setValue(false);
//		config.getNode("Worlds", worldName, "Time", "Set").setValue(6000);
//		config.getNode("Worlds", worldName, "Weather", "Lock").setValue(false);
//		config.getNode("Worlds", worldName, "Weather", "Set").setValue("CLEAR");	
//
//		loader.save();
//
//		src.sendMessage(Texts.of(TextColors.DARK_GREEN, worldName, " imported successfully"));
		
		src.sendMessage(Texts.of(TextColors.DARK_RED, "import has been deprecated. use ", TextColors.GOLD, "/world load"));
		
		return CommandResult.success();
	}

}
