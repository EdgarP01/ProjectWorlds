package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import com.gmail.trentech.pjw.utils.Zip;

public class CommandRemove implements CommandCallable {
	
	private final Help help = Help.get("world remove").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("remove")) {
			throw new CommandException(getHelp().getUsageText());
		}

		if(arguments.equalsIgnoreCase("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(arguments);
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, arguments, " does not exist"), false);
		}
		WorldProperties world = optionalWorld.get();

		if (Sponge.getServer().getWorld(world.getWorldName()).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, world.getWorldName(), " must be unloaded before you can delete"), false);
		}

		new Zip(world.getWorldName()).save();

		try {
			if (Sponge.getServer().deleteWorld(world).get()) {

				source.sendMessage(Text.of(TextColors.DARK_GREEN, world.getWorldName(), " deleted successfully"));

				return CommandResult.success();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
		}

		source.sendMessage(Text.of(TextColors.RED, "Could not delete ", world.getWorldName()));

		return CommandResult.empty();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("remove")) {
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
