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

public class CommandRename implements CommandCallable {
	
	private final Help help = Help.get("world rename").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("rename")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		String srcWorldName;
		String newWorldName;
		
		try {
			srcWorldName = args[0];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		try {
			newWorldName = args[1];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}	
		
		Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(srcWorldName);
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, srcWorldName, " does not exist"), false);
		}
		WorldProperties world = optionalWorld.get();

		if (Sponge.getServer().getWorld(world.getWorldName()).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, world.getWorldName(), " must be unloaded before you can rename"), false);
		}
		
		if (Sponge.getServer().getWorldProperties(newWorldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, newWorldName, " already exists"), false);
		}

		Optional<WorldProperties> rename = Sponge.getServer().renameWorld(world, newWorldName);

		if (!rename.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "Could not rename ", world.getWorldName()), false);
		}

		source.sendMessage(Text.of(TextColors.DARK_GREEN, world.getWorldName(), " renamed to ", newWorldName, " successfully"));

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("rename")) {
			return list;
		}

		String[] args = arguments.split(" ");
		
		if(args.length != 1) {
			return list;
		}
		
		for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
			if(world.getWorldName().equalsIgnoreCase(args[args.length - 1])) {
				return list;
			}
			
			if(world.getWorldName().toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
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
}
