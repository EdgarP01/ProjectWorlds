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

public class CMDTeleport implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		if(!args.hasAny("name")) {
			src.sendMessage(Texts.of(TextColors.GOLD, "/world teleport <world>"));
			return CommandResult.empty();
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
