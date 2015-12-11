package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public class CMDWorld implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
		src.sendMessage(Texts.of(TextColors.GOLD, "                       Command List"));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
		src.sendMessage(Texts.of(TextColors.GOLD, "/world [world]"));			
		if(src.hasPermission("pjw.cmd.world.properties")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world properties"));
		}
		if(src.hasPermission("pjw.cmd.world.create")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world create"));
		}
		if(src.hasPermission("pjw.cmd.world.delete")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world delete"));
		}
		if(src.hasPermission("pjw.cmd.world.rename")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world rename"));	
		}
		if(src.hasPermission("pjw.cmd.world.unload")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world unload"));	
		}
		if(src.hasPermission("pjw.cmd.world.copy")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world copy"));	
		}
		if(src.hasPermission("pjw.cmd.world.load")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world load"));	
		}
//		if(src.hasPermission("pjw.cmd.world.import")) {
//			src.sendMessage(Texts.of(TextColors.GOLD, "/world import"));	
//		}
		if(src.hasPermission("pjw.cmd.world.difficulty")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world difficulty"));	
		}
		if(src.hasPermission("pjw.cmd.world.gamemode")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world gamemode"));	
		}
		if(src.hasPermission("pjw.cmd.world.setspawn")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world setspawn"));	
		}
		if(src.hasPermission("pjw.cmd.world.pvp")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world pvp"));	
		}
		if(src.hasPermission("pjw.cmd.world.respawn")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world respawn"));	
		}
		if(src.hasPermission("pjw.cmd.world.keepspawnloaded")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world keepspawnloaded"));	
		}
		if(src.hasPermission("pjw.cmd.world.hardcore")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world hardcore"));	
		}
		if(src.hasPermission("pjw.cmd.world.locktime")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world locktime"));	
		}
		if(src.hasPermission("pjw.cmd.world.lockweather")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world lockweather"));	
		}
		if(src.hasPermission("pjw.cmd.world.list")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world list"));	
		}
		if(src.hasPermission("pjw.cmd.world.portal")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world portal"));	
		}
		if(src.hasPermission("pjw.cmd.world.teleport")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world teleport"));	
		}
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));

		return CommandResult.success();
	}

}
