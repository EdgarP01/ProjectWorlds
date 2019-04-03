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

		List<String> args = Arrays.asList(arguments.split(" "));
		
		if(args.contains("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		if(args.size() < 1) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(args.get(0));
		
		if(!optionalProperties.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, args.get(0), " does not exist"), false);
		}
		WorldProperties properties = optionalProperties.get();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getUniqueId());
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " must be loaded"), false);
		}
		World world = optionalWorld.get();
		
		if(args.size() == 1) {
			if(properties.isRaining()) {
				source.sendMessage(Text.of(TextColors.GREEN, "Weather: ", TextColors.WHITE, "rain"));
			} else if(properties.isThundering()) {
				source.sendMessage(Text.of(TextColors.GREEN, "Weather: ", TextColors.WHITE, "thunder"));
			} else {
				source.sendMessage(Text.of(TextColors.GREEN, "Weather: ", TextColors.WHITE, "clear"));
			}
			
			return CommandResult.success();
		}

		String value = args.get(1);
		
		int duration = 0;
		
		if(args.size() == 3) {			
			try {
				duration = Integer.parseInt(args.get(2));
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
			} else {
				list.add("clear");
				list.add("thunder");
				list.add("rain");
			}
			
			return list;
		}
		
		if(args.size() == 2) {
			if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
				if("clear".equalsIgnoreCase(args.get(1))) {
					list.add("clear");
				}
				if("rain".equalsIgnoreCase(args.get(1))) {
					list.add("rain");
				}
				if("thunder".equalsIgnoreCase(args.get(1))) {
					list.add("thunder");
				}
				
				if("clear".toLowerCase().startsWith(args.get(1).toLowerCase())) {
					list.add("clear");
				}
				if("rain".toLowerCase().startsWith(args.get(1).toLowerCase())) {
					list.add("rain");
				}
				if("thunder".toLowerCase().startsWith(args.get(1).toLowerCase())) {
					list.add("thunder");
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

}
