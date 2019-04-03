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

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.utils.Utils;

public class CommandTime implements CommandCallable {
	
	private final Help help = Help.get("world time").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("time")) {
			throw new CommandException(getHelp().getUsageText());
		}

		List<String> args = Arrays.asList(arguments.split(" "));
		
		if(args.contains("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		if(args.size() > 1) {
			throw new CommandException(getHelp().getUsageText());
		}

		Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(args.get(0));
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, args.get(0), " does not exist"), false);
		}
		WorldProperties world = optionalWorld.get();
		
		if(args.size() != 2) {
			source.sendMessage(Text.of(TextColors.GREEN, "Time: ", TextColors.WHITE, Utils.getTime(world.getWorldTime()), TextColors.GREEN, " Ticks: ", TextColors.WHITE, world.getWorldTime() % 24000));			
			return CommandResult.success();
		}

		long time = 0;
		try {
			time = Long.parseLong(args.get(1));
		} catch(Exception e) {
			throw new CommandException(Text.of(TextColors.RED, args.get(1), "is not a valid number"), false);
		}
		
		if(time < 0 || time > 24000) {
			throw new CommandException(Text.of(TextColors.RED, "Time value must be between 0 - 24000"), false);
		}

		world.setWorldTime(time);
		Sponge.getServer().saveWorldProperties(world);
		
		source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set time of ", world.getWorldName(), " to ", TextColors.YELLOW, Utils.getTime(world.getWorldTime())));
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

}
