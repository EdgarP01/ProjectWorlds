package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.Arrays;
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

		List<String> args = Arrays.asList(arguments.split(" "));
		
		if(args.contains("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		if(args.size() < 2) {
			throw new CommandException(getHelp().getUsageText());
		}

		Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(args.get(0));
		
		if(!optionalProperties.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, args.get(0), " does not exist"), false);
		}
		WorldProperties properties = optionalProperties.get();

		if(args.size() == 1) {
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
		
		String rule = args.get(1);
		
		if(!validGamerule(properties, rule)) {
			source.sendMessage(Text.of(TextColors.YELLOW, rule, " is not a valid Gamerule"));
			throw new CommandException(getHelp().getUsageText());
		}

		if(args.size() == 2) {
			source.sendMessage(Text.of(TextColors.GREEN, rule, ": ", TextColors.WHITE, properties.getGameRule(rule).get()));
			return CommandResult.success();
		}
		
		String value = args.get(2);
		
		if (!isValid(rule, value)) {
			source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Value for Gamerule ", rule));
			throw new CommandException(getHelp().getUsageText());
		}

		properties.setGameRule(rule, value.toLowerCase());
		Sponge.getServer().saveWorldProperties(properties);
		
		source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set gamerule ", rule, " to ", value));

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
				Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(args.get(0));
				
				if(optionalProperties.isPresent()) {
					WorldProperties properties = optionalProperties.get();
					
					for (Entry<String, String> gamerule : properties.getGameRules().entrySet()) {
						list.add(gamerule.getKey());
					}
				}
			}
			
			return list;
		}

		if(args.size() == 2) {
			if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
				Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(args.get(0));
				
				if(optionalProperties.isPresent()) {
					WorldProperties properties = optionalProperties.get();
					
					for (Entry<String, String> gamerule : properties.getGameRules().entrySet()) {
						if(gamerule.getKey().equalsIgnoreCase(args.get(1))) {
							list.add(gamerule.getKey());
						}
						
						if(gamerule.getKey().toLowerCase().startsWith(args.get(1).toLowerCase())) {
							list.add(gamerule.getKey());
						}
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
