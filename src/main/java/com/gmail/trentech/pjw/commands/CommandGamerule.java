package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;

public class CommandGamerule implements CommandCallable {
	
	private final Help help = Help.get("world gamerule").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("gamerule")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			getHelp().execute(source);
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

		String rule;
		
		try {
			rule = args[1];
			
			if(!validGamerule(properties, rule)) {
				source.sendMessage(Text.of(TextColors.YELLOW, rule, " is not a valid Gamerule"));
				throw new CommandException(getHelp().getUsageText());
			}
		} catch(Exception e) {
			List<Text> list = new ArrayList<>();

			for (Entry<String, String> gamerule : properties.getGameRules().entrySet()) {
				list.add(Text.of(TextColors.GREEN, gamerule.getKey(), ": ", TextColors.WHITE, gamerule.getValue()));
			}

			if (source instanceof Player) {
				PaginationList.Builder pages = PaginationList.builder();

				pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, properties.getWorldName())).build());
				pages.contents(list);
				pages.sendTo(source);
			} else {
				for (Text text : list) {
					source.sendMessage(text);
				}
			}

			return CommandResult.success();
		}
		
		String value;
		
		try {
			value = args[2];
			
			if (!isValid(rule, value)) {
				source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Value for Gamerule ", rule));
				throw new CommandException(getHelp().getUsageText());
			}
		} catch(Exception e) {
			source.sendMessage(Text.of(TextColors.GREEN, rule, ": ", TextColors.WHITE, properties.getGameRule(rule).get()));
			return CommandResult.success();
		}
		
		properties.setGameRule(rule, value.toLowerCase());
		Sponge.getServer().saveWorldProperties(properties);
		
		source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set gamerule ", rule, " to ", value));

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("gamerule")) {
			return list;
		}

		String[] args = arguments.split(" ");
		
		if(args.length == 1) {
			for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
				if(world.getWorldName().equalsIgnoreCase(args[0])) {
					for(Entry<String, String> entry : world.getGameRules().entrySet()) {
						list.add(entry.getKey());
					}
					return list;
				}
				
				if(world.getWorldName().toLowerCase().startsWith(args[0].toLowerCase())) {
					list.add(world.getWorldName());
				}
			}
		}
		
		if(args.length == 2) {
			for(Entry<String, String> entry : Sponge.getServer().getWorldProperties(args[0]).get().getGameRules().entrySet()) {
				if(entry.getKey().toLowerCase().equalsIgnoreCase(args[1].toLowerCase())) {
					return list;
				}
				
				if(entry.getKey().toLowerCase().startsWith(args[1].toLowerCase())) {
					list.add(entry.getKey());
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

	private boolean isValid(String rule, String value) {
		switch (rule) {
		case "randomTickSpeed":
			try {
				Long.parseLong(value);
				return true;
			} catch (Exception e) {
				return false;
			}
		case "spawnRadius":
			try {
				Long.parseLong(value);
				return true;
			} catch (Exception e) {
				return false;
			}
		case "spawnOnDeath":
			if (Sponge.getServer().getWorld(value).isPresent() || rule.equals("default")) {
				return true;
			}
			return false;
		case "netherPortal":
			if (Sponge.getServer().getWorld(value).isPresent() || rule.equals("default")) {
				return true;
			}
			return false;
		case "endPortal":
			if (Sponge.getServer().getWorld(value).isPresent() || rule.equals("default")) {
				return true;
			}
			return false;
		default:
			return validBool(value);
		}
	}

	private boolean validBool(String value) {
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
			return true;
		}
		return false;
	}

	private boolean validGamerule(WorldProperties properties, String rule) {
		for(Entry<String, String> entry : properties.getGameRules().entrySet()) {
			if(entry.getKey().equals(rule)) {
				return true;
			}
		}
		
		return false;
	}
}
