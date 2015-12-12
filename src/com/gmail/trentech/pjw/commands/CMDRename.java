package com.gmail.trentech.pjw.commands;

import java.util.Optional;

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

public class CMDRename implements CommandExecutor {
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("old")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String oldWorldName = args.<String>getOne("old").get();

		if(oldWorldName.equalsIgnoreCase("@w")){
			if(src instanceof Player){
				oldWorldName = ((Player) src).getWorld().getName();
			}
		}
		
		if(Main.getGame().getServer().getWorld(oldWorldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, oldWorldName, " must be unloaded before you can rename"));
			return CommandResult.empty();
		}
		
		if(!args.hasAny("new")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		String newWorldName = args.<String>getOne("new").get();

		for(WorldProperties world : Main.getGame().getServer().getAllWorldProperties()){
			if(world.getWorldName().equalsIgnoreCase(newWorldName)){
				src.sendMessage(Texts.of(TextColors.DARK_RED, newWorldName, " already exists"));
				return CommandResult.empty();
			}
		}

		for(WorldProperties worldInfo : Main.getGame().getServer().getUnloadedWorlds()){
			if(worldInfo.getWorldName().equalsIgnoreCase(oldWorldName)){
				Optional<WorldProperties> rename = Main.getGame().getServer().renameWorld(worldInfo, newWorldName);

				if(!rename.isPresent()){
					src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not rename ", oldWorldName));
					return CommandResult.empty();
				}

				WorldProperties properties = Main.getGame().getServer().getWorldProperties(newWorldName).get();
				
				ConfigManager loader = new ConfigManager("worlds.conf");
				ConfigurationNode config = loader.getConfig();

				config.getNode("Worlds", newWorldName, "UUID").setValue(properties.getUniqueId().toString());
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
				
				config.getNode("Worlds", oldWorldName).setValue(null);

				loader.save();

				src.sendMessage(Texts.of(TextColors.DARK_GREEN, newWorldName, " renamed successfully"));
				
				return CommandResult.success();
			}
		}

		src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not locate ", oldWorldName));
		
		return CommandResult.empty();
	}
	
	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.GOLD, "/world pvp ");
		Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter world or @w for current world"))).append(Texts.of("<world> ")).build();
		Text t3 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter new world name"))).append(Texts.of("<world>")).build();
		return Texts.of(t1,t2,t3);
	}
}
