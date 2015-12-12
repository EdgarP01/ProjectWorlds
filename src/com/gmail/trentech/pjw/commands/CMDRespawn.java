package com.gmail.trentech.pjw.commands;

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

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDRespawn implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
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

		ConfigManager loader = new ConfigManager("worlds.conf");
		ConfigurationNode config = loader.getConfig();
		
		if(!args.hasAny("value")) {
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Respawn World: ", TextColors.GOLD, config.getNode("Worlds", worldName, "PVP").getString()));
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Command: ", invalidArg()));
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			return CommandResult.success();
		}
		String respawnWorldName = args.<String>getOne("value").get();
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", respawnWorldName, " does not exist"));
			return CommandResult.empty();
		}

		config.getNode("Worlds", worldName, "Respawn-World").setValue(respawnWorldName);

		loader.save();

		src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Set respawn world for ", worldName, " to ", respawnWorldName));

		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.GOLD, "/world respawn ");
		Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter world or @w for current world"))).append(Texts.of("<world> ")).build();
		Text t3 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter new world name"))).append(Texts.of("[world]")).build();
		return Texts.of(t1,t2,t3);
	}

}
