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

public class CommandHardcore implements CommandCallable {
	
	private final Help help = Help.get("world hardcore").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("hardcore")) {
			throw new CommandException(getHelp().getUsageText());
		}

		List<String> args = Arrays.asList(arguments.split(" "));
		
		if(args.contains("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		if(args.isEmpty() || args.size() < 2 || args.size() > 3) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(args.get(0));
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, args.get(0), " does not exist"), false);
		}
		WorldProperties world = optionalWorld.get();

		if(!args.get(1).equalsIgnoreCase("true") && !args.get(1).equalsIgnoreCase("false")) {
			throw new CommandException(Text.of(TextColors.RED, args.get(1), " is not a valid boolean"), false);
		}
		
		world.setHardcore(Boolean.valueOf(args.get(1)));

		Sponge.getServer().saveWorldProperties(world);
		
		source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set hardcore of ", world.getWorldName(), " to ", TextColors.YELLOW, args.get(1)));

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
				list.add("true");
				list.add("false");
			}
			
			return list;
		}

		if(args.size() == 2) {
			if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
				if("true".equalsIgnoreCase(args.get(1).toLowerCase())) {
					list.add("true");
				}
				if("false".equalsIgnoreCase(args.get(1).toLowerCase())) {
					list.add("false");
				}
				if("true".startsWith(args.get(1).toLowerCase())) {
					list.add("true");
				}
				if("false".startsWith(args.get(1).toLowerCase())) {
					list.add("false");
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
