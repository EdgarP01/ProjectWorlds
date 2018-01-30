package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3i;
import com.gmail.trentech.pjc.help.Help;

public class CommandSetSpawn implements CommandCallable {
	
	private final Help help = Help.get("world setspawn").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("setspawn")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		String worldName;
		WorldProperties properties;
		
		try {
			worldName = args[0];
			
			Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(worldName);
			
			if(!optionalProperties.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, worldName, " does not exist"), false);
			}
			properties = optionalProperties.get();
		} catch(Exception e) {
			if(source instanceof Player) {
				properties = ((Player) source).getWorld().getProperties();
			} else {
				source.sendMessage(Text.of(TextColors.YELLOW, "Console must provide <world> and <x,y,z>"));
				throw new CommandException(getHelp().getUsageText());
			}
		}

		String coords;
		Vector3i vector3i;
		
		try {
			coords = args[1];
			String[] coordinates = args[1].split(",");

			if (!isValidLocation(coordinates)) {
				throw new CommandException(Text.of(TextColors.RED, coords.toString(), " is not valid"), true);
			}
			
			vector3i = new Vector3i().add(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), Integer.parseInt(coordinates[2]));
		} catch (Exception e) {
			if(source instanceof Player) {
				vector3i = ((Player) source).getLocation().getBlockPosition();
			} else {
				source.sendMessage(Text.of(TextColors.YELLOW, "Console must provide <world> and <x,y,z>"));
				throw new CommandException(getHelp().getUsageText());
			}
		}

		properties.setSpawnPosition(vector3i);
		Sponge.getServer().saveWorldProperties(properties);
		
		source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set spawn of world ", properties.getWorldName(), " to x: ", properties.getSpawnPosition().getX(), ", y: ", properties.getSpawnPosition().getY(), ", z: ", properties.getSpawnPosition().getZ()));

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("setspawn")) {
			return list;
		}

		for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
			if(world.getWorldName().equalsIgnoreCase(arguments)) {
				return list;
			}
			
			if(world.getWorldName().toLowerCase().startsWith(arguments.toLowerCase())) {
				list.add(world.getWorldName());
			}
		}
		
		return list;
	}

	@Override
	public boolean testPermission(CommandSource source) {
		Optional<String> permission = getHelp().getPermission();
		
		if(permission.isPresent()) {
			return source.hasPermission(permission.get());
		}
		return true;
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return Optional.of(Text.of(getHelp().getDescription()));
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return Optional.of(Text.of(getHelp().getDescription()));
	}

	@Override
	public Text getUsage(CommandSource source) {
		return getHelp().getUsageText();
	}
	
	public Help getHelp() {
		return help;
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
