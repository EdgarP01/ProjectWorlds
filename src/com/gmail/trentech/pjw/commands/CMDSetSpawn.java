package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;
import com.gmail.trentech.pjw.Main;

public class CMDSetSpawn implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		String worldName = player.getWorld().getName();
		if(args.hasAny("name")) {
			worldName = args.<String>getOne("name").get();
		}
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();

		if(!args.hasAny("value")) {
			world.getProperties().setSpawnPosition(player.getLocation().getBlockPosition());
			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set spawn of world ", worldName, " to x: ", world.getProperties().getSpawnPosition().getX(), ", y: ", world.getProperties().getSpawnPosition().getY(), ", z: ", world.getProperties().getSpawnPosition().getZ()));
			return CommandResult.success();
		}
		
		String[] values = args.<String>getOne("value").get().split(",");

		world.getProperties().setSpawnPosition(new Vector3i().add(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2])));
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set spawn of world ", worldName, " to x: ", world.getProperties().getSpawnPosition().getX(), ", y: ", world.getProperties().getSpawnPosition().getY(), ", z: ", world.getProperties().getSpawnPosition().getZ()));
		
		return CommandResult.success();
	}

}
