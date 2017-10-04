package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.weather.Weathers;

import com.gmail.trentech.pjc.help.Help;

public class CommandWeather implements CommandCallable {
	
	private final Help help = Help.get("world weather").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("weather")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		String worldName;
		String value;
		int duration = 0;
		
		try {
			worldName = args[0];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}

		Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(worldName);
		
		if(!optionalProperties.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " does not exist"), false);
		}
		WorldProperties properties = optionalProperties.get();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getUniqueId());
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " must be loaded"), false);
		}
		World world = optionalWorld.get();
		
		try {
			value = args[1];
		} catch(Exception e) {
			if(properties.isRaining()) {
				source.sendMessage(Text.of(TextColors.GREEN, "Weather: ", TextColors.WHITE, "rain"));
			} else if(properties.isThundering()) {
				source.sendMessage(Text.of(TextColors.GREEN, "Weather: ", TextColors.WHITE, "thunder"));
			} else {
				source.sendMessage(Text.of(TextColors.GREEN, "Weather: ", TextColors.WHITE, "clear"));
			}
			
			return CommandResult.success();
		}
		
		if(args.length == 3) {
			try {
				duration = Integer.parseInt(args[2]);
			} catch(Exception e) {
				throw new CommandException(getHelp().getUsageText());
			}
		}
		
		if(duration == 0) {
			if(value.equalsIgnoreCase("clear")) {
				world.setWeather(Weathers.CLEAR);
			} else if(value.equalsIgnoreCase("rain")) {
				world.setWeather(Weathers.RAIN);
			} else if(value.equalsIgnoreCase("thunder")) {
				world.setWeather(Weathers.THUNDER_STORM);
			} else {
				source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid weather type"));
				throw new CommandException(getHelp().getUsageText());
			}
		} else {
			if(value.equalsIgnoreCase("clear")) {
				world.setWeather(Weathers.CLEAR, duration);
			} else if(value.equalsIgnoreCase("rain")) {
				world.setWeather(Weathers.RAIN, duration);
			} else if(value.equalsIgnoreCase("thunder")) {
				world.setWeather(Weathers.THUNDER_STORM, duration);
			} else {
				source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid weather type"));
				throw new CommandException(getHelp().getUsageText());
			}
		}

		source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set weather of ", properties.getWorldName(), " to ", TextColors.YELLOW, value, TextColors.DARK_GREEN, " for ", TextColors.YELLOW, world.getRemainingDuration()));

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("weather")) {
			return list;
		}

		String[] args = arguments.split(" ");
		
		if(args.length == 1) {
			for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
				if(world.getWorldName().equalsIgnoreCase(args[0])) {
					list.add("clear");
					list.add("thunder");
					list.add("rain");
					return list;
				}
				
				if(world.getWorldName().toLowerCase().startsWith(args[0].toLowerCase())) {
					list.add(world.getWorldName());
				}
			}
		}
		
		if(args.length == 2) {
			if("clear".equalsIgnoreCase(args[1]) || "rain".equalsIgnoreCase(args[1]) || "thunder".equalsIgnoreCase(args[1])) {
				return list;
			}
			if("clear".toLowerCase().startsWith(args[1].toLowerCase())) {
				list.add("clear");
			}
			if("rain".toLowerCase().startsWith(args[1].toLowerCase())) {
				list.add("rain");
			}
			if("thunder".toLowerCase().startsWith(args[1].toLowerCase())) {
				list.add("thunder");
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

}
