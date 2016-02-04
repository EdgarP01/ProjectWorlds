package com.gmail.trentech.pjw.commands;

import java.util.concurrent.ExecutionException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;
import com.gmail.trentech.pjw.utils.Zip;

public class CMDDelete implements CommandExecutor {

	public CMDDelete(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "world").getString();
		
		Help help = new Help("delete", " Delete worlds you no longer need. Worlds must be unloaded before you can delete them");
		help.setSyntax(" /world delete <world>\n /" + alias + " dl <world>");
		help.setExample(" /world delete OldWorld");
		CMDHelp.getList().add(help);
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/world delete <world>"));
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();

		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " must be unloaded before you can delete"));
			return CommandResult.empty();
		}
		
		if(!Main.getGame().getServer().getWorldProperties(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}		

		new Zip(worldName).save();

		WorldProperties properties = Main.getGame().getServer().getWorldProperties(worldName).get();
		
		try {
			if(Main.getGame().getServer().deleteWorld(properties).get()){
				src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " deleted successfully"));
				return CommandResult.success();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		src.sendMessage(Text.of(TextColors.DARK_RED, "Could not locate ", worldName));
		
		return CommandResult.empty();
	}

}
