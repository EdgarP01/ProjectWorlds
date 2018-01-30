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

import com.gmail.trentech.pjc.help.Help;

public class CommandKeepSpawnLoaded implements CommandCallable {
	
	private final Help help = Help.get("world keepspawnloaded").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("keepspawnloaded")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		String worldName;
		String bool;
		
		try {
			worldName = args[0];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		try {
			bool = args[1];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(worldName);
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " does not exist"), false);
		}
		WorldProperties world = optionalWorld.get();

		if(bool.equalsIgnoreCase("true") || bool.equalsIgnoreCase("false")) {
			world.setKeepSpawnLoaded(Boolean.valueOf(bool));
		}
		Sponge.getServer().saveWorldProperties(world);
		
		source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set keep spawn loaded of ", world.getWorldName(), " to ", TextColors.YELLOW, bool));

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("keepspawnloaded")) {
			return list;
		}

		String[] args = arguments.split(" ");

		if(args.length == 1) {
			for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
				if(world.getWorldName().equalsIgnoreCase(args[0])) {
					list.add("true");
					list.add("false");
					
					return list;
				}

				if(world.getWorldName().toLowerCase().startsWith(args[0].toLowerCase())) {
					list.add(world.getWorldName());
				}
			}
			
			return list;
		}
		
		if(args.length == 2) {
			if("true".equalsIgnoreCase(args[1].toLowerCase()) || "false".equalsIgnoreCase(args[1].toLowerCase()) ) {
				return list;
			}
			
			if("true".startsWith(args[1].toLowerCase())) {
				list.add("true");
			}
			if("false".startsWith(args[1].toLowerCase())) {
				list.add("false");
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
