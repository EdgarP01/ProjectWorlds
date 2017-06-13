package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3i;
import com.gmail.trentech.pjc.help.Help;

public class CMDSetSpawn implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("world setspawn").get();
		
		if (args.hasAny("help")) {		
			help.execute(src);
			return CommandResult.empty();
		}
		
		WorldProperties properties;
		Vector3i vector3i;
		
		if (!args.hasAny("world")) {
			if(src instanceof Player) {
				properties = ((Player) src).getWorld().getProperties();
			} else {
				throw new CommandException(Text.of(TextColors.RED, "Console must provide <world> and <x,y,z>"), true);
			}
		} else {
			properties = args.<WorldProperties> getOne("world").get();
		}
		
		if (!args.hasAny("x,y,z")) {
			if(src instanceof Player) {
				vector3i = ((Player) src).getLocation().getBlockPosition();
			} else {
				throw new CommandException(Text.of(TextColors.RED, "Console must provide <world> and <x,y,z>"), true);
			}
		} else {
			String[] coords = args.<String> getOne("x,y,z").get().split(",");

			if (!isValidLocation(coords)) {
				throw new CommandException(Text.of(TextColors.RED, coords.toString(), " is not valid"), true);
			}
			
			vector3i = new Vector3i().add(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
		}

		properties.setSpawnPosition(vector3i);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set spawn of world ", properties.getWorldName(), " to x: ", properties.getSpawnPosition().getX(), ", y: ", properties.getSpawnPosition().getY(), ", z: ", properties.getSpawnPosition().getZ()));

		return CommandResult.success();
	}

	private boolean isValidLocation(String[] coords) {
		if (coords == null) {
			return false;
		}

		for (String coord : coords) {
			try {
				Integer.parseInt(coord);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

}
