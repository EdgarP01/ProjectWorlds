package com.gmail.trentech.pjw.commands;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

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

public class CMDCopy implements CommandExecutor {
	
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
		
		if(!args.hasAny("new")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		String newWorldName = args.<String>getOne("new").get();
		
		for(WorldProperties world : Main.getGame().getServer().getAllWorldProperties()){
			if(!world.getWorldName().equalsIgnoreCase(newWorldName)){
				continue;
			}
			
			src.sendMessage(Texts.of(TextColors.DARK_RED, newWorldName, " already exists"));
			return CommandResult.empty();
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

		WorldProperties properties = Main.getGame().getServer().getWorldProperties(newWorldName).get();
		
		ConfigManager loader = new ConfigManager("worlds.conf");
		ConfigurationNode config = loader.getConfig();

		config.getNode("Worlds", newWorldName, "Gamemode").setValue(properties.getGameMode().getName().toUpperCase());
		config.getNode("Worlds", newWorldName, "PVP").setValue(config.getNode("Worlds", oldWorldName, "PVP").getBoolean());
		config.getNode("Worlds", newWorldName, "Respawn-World").setValue(config.getNode("Worlds", oldWorldName, "Respawn-World").getString());
		//config.getNode("Worlds", newWorldName, "Time", "Lock").setValue(config.getNode("Worlds", oldWorldName, "Time", "Lock").getBoolean());
		//config.getNode("Worlds", newWorldName, "Time", "Set").setValue(config.getNode("Worlds", oldWorldName, "Time", "Set").getString());
		config.getNode("Worlds", newWorldName, "Weather", "Lock").setValue(config.getNode("Worlds", oldWorldName, "Weather", "Lock").getBoolean());
		config.getNode("Worlds", newWorldName, "Weather", "Set").setValue(config.getNode("Worlds", oldWorldName, "Weather", "Set").getString());	

		loader.save();

		src.sendMessage(Texts.of(TextColors.DARK_GREEN, oldWorldName, " copied to ", newWorldName));
		
		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.GOLD, "/world copy ");
		Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter source world"))).append(Texts.of("<world> ")).build();
		Text t3 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter new world name"))).append(Texts.of("<world>")).build();
		return Texts.of(t1,t2,t3);
	}
}
