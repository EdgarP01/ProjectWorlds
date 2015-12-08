package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;

public class CMDWorld implements CommandExecutor {
	

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			src.sendMessage(Texts.of(TextColors.GOLD, "                       Command List:"));
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
			if(src.hasPermission("pjw.cmd.world.portal")) {
				src.sendMessage(Texts.of(TextColors.GOLD, "/world portal"));	
			}
			if(src.hasPermission("pjw.cmd.world.difficulty")) {
				src.sendMessage(Texts.of(TextColors.GOLD, "/world difficulty"));	
			}
			if(src.hasPermission("pjw.cmd.world.gamemode")) {
				src.sendMessage(Texts.of(TextColors.GOLD, "/world gamemode"));	
			}
			if(src.hasPermission("pjw.cmd.world.setspawn")) {
				src.sendMessage(Texts.of(TextColors.GOLD, "/world setspawn"));	
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
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			return CommandResult.success();
		}
		String worldName = args.<String>getOne("name").get();
		
		if(!(src instanceof Player)){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();

		player.setLocationSafely(world.getSpawnLocation());
		
		player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, world.getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", world.getSpawnLocation().getBlockX(), ", y: ", world.getSpawnLocation().getBlockY(),", z: ", world.getSpawnLocation().getBlockZ())));
		
		return CommandResult.success();
	}

}
