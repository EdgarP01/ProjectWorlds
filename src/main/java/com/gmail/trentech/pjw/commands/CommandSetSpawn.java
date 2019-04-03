package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.Arrays;
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
		WorldProperties properties;
		Vector3i vector3i;
		
		if(arguments.equalsIgnoreCase("setspawn")) {
			if(source instanceof Player) {
				properties = ((Player) source).getWorld().getProperties();
				vector3i = ((Player) source).getLocation().getBlockPosition();
			} else {
				source.sendMessage(Text.of(TextColors.YELLOW, "Console must provide <world> and <x,y,z>"));
				throw new CommandException(getHelp().getUsageText());
			}
		} else {
			List<String> args = Arrays.asList(arguments.split(" "));
			
			if(args.contains("--help")) {
				getHelp().execute(source);
				return CommandResult.success();
			}
			
			if(args.size() != 2) {
				throw new CommandException(getHelp().getUsageText());
			}
			
			Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(args.get(0));
			
			if(!optionalProperties.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, args.get(0), " does not exist"), false);
			}
			properties = optionalProperties.get();

			String[] coordinates = args.get(1).split(",");

			if (!isValidLocation(coordinates)) {
				throw new CommandException(Text.of(TextColors.RED, args.get(1), " is not valid"), true);
			}
			
			vector3i = new Vector3i().add(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]), Integer.parseInt(coordinates[2]));
		}

		properties.setSpawnPosition(vector3i);
		Sponge.getServer().saveWorldProperties(properties);
		
		source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set spawn of world ", properties.getWorldName(), " to x: ", properties.getSpawnPosition().getX(), ", y: ", properties.getSpawnPosition().getY(), ", z: ", properties.getSpawnPosition().getZ()));

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();

		if(arguments.equalsIgnoreCase("")) {
			for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
				list.add(world.getWorldName());
			}
			
			return list;
		}
		
		List<String> args = Arrays.asList(arguments.split(" "));

		if(args.size() == 1) {
			if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					if(world.getWorldName().toLowerCase().equalsIgnoreCase(args.get(0).toLowerCase())) {
						list.add(world.getWorldName());
					}
					
					if(world.getWorldName().toLowerCase().startsWith(args.get(0).toLowerCase())) {
						list.add(world.getWorldName());
					}
				}
			}
			
			return list;
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
