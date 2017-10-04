package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

public class CommandCopy implements CommandCallable {
	
	private final Help help = Help.get("world copy").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("copy")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		String oldWorldName;
		String newWorldName;
		
		try {
			oldWorldName = args[0];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		try {
			newWorldName = args[1];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(oldWorldName);
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, oldWorldName, " does not exist"), false);
		}
		WorldProperties world = optionalWorld.get();

		if (Sponge.getServer().getWorldProperties(newWorldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, newWorldName, " already exists"), false);
		}

		Optional<WorldProperties> copy = Optional.empty();

		try {
			CompletableFuture<Optional<WorldProperties>> copyFuture = Sponge.getServer().copyWorld(world, newWorldName);
			while (!copyFuture.isDone()) {
			}
			copy = copyFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
		}
		
		if (!copy.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "Could not copy ", world.getWorldName()), false);
		}

		source.sendMessage(Text.of(TextColors.DARK_GREEN, world.getWorldName(), " copied to ", newWorldName));
		
		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("copy")) {
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
