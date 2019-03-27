package com.gmail.trentech.pjw.commands.border;

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
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;

public class CommandDiameter implements CommandCallable {
	
	private final Help help = Help.get("world border diameter").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("diameter")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		String worldName;

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

		if (!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " must be loaded"));
		}
		World world = optionalWorld.get();

		WorldBorder border = world.getWorldBorder();

		@SuppressWarnings("unused")
		String test;

		double startDiameter;

		try {
			test = args[1];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		try {
			startDiameter = Double.parseDouble(args[1]);
		} catch(Exception e) {
			throw new CommandException(Text.of(TextColors.RED, args[1], " is not a number value"), true);
		}

		long time = 0;
		double endDiameter = 0;
		try {
			test = args[2];
			
			try {
				time = Long.parseLong(args[2]);
			} catch(Exception e) {
				throw new CommandException(Text.of(TextColors.RED, args[2], " is not a number value"), true);
			}
			
			try {
				test = args[3];
				
				try {
					endDiameter = Double.parseDouble(args[3]);
				} catch(Exception e) {
					throw new CommandException(Text.of(TextColors.RED, args[3], " is not a number value"), true);
				}
			} catch(Exception e) { }

		} catch(Exception e) { }

		if (time != 0) {
			if (endDiameter != 0) {
				border.setDiameter(startDiameter, endDiameter, time);
				source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set diameter of ", world.getName(), " to start: ", startDiameter, "end: ", endDiameter, " time: ", time));
			} else {
				border.setDiameter(startDiameter, time);
				source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set diameter of ", world.getName(), " to start: ", startDiameter, " time: ", time));
			}
		} else {
			border.setDiameter(startDiameter);
			source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set diameter of ", world.getName(), " to ", startDiameter));
		}

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("diameter")) {
			return list;
		}

		String[] args = arguments.split(" ");
		
		if(args.length == 1) {
			for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
				if(world.getWorldName().equalsIgnoreCase(args[0])) {
					return list;
				}
				
				if(world.getWorldName().toLowerCase().startsWith(args[0].toLowerCase())) {
					list.add(world.getWorldName());
				}
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
