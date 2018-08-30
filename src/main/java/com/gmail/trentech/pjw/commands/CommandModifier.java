package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;

public class CommandModifier implements CommandCallable {
	
	private final Help help = Help.get("world modifier").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("modifier")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		String worldName;
		String mod;
		
		try {
			worldName = args[0];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}

		Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(worldName);
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " does not exist"), false);
		}
		WorldProperties world = optionalWorld.get();

		Collection<WorldGeneratorModifier> modifiers = world.getGeneratorModifiers();
		
		try {
			mod = args[1];
		} catch(Exception e) {
			List<Text> list = new ArrayList<>();

			for (WorldGeneratorModifier modifier : modifiers) {
				list.add(Text.of(TextColors.WHITE, modifier.getId()));
			}

			if (source instanceof Player) {
				PaginationList.Builder pages = PaginationList.builder();

				pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Current Modifiers")).build());

				pages.contents(list);

				pages.sendTo(source);
			} else {
				for (Text text : list) {
					source.sendMessage(text);
				}
			}
			
			return CommandResult.success();
		}

		Optional<WorldGeneratorModifier> optionalModifier = Sponge.getRegistry().getType(WorldGeneratorModifier.class, mod);
		
		if(!optionalModifier.isPresent()) {
			source.sendMessage(Text.of(TextColors.YELLOW, mod, " is not a valid WorldGeneratorModifier"));
			throw new CommandException(getHelp().getUsageText());
		}	
		WorldGeneratorModifier modifier = optionalModifier.get();

		try {
			if(args[2].equalsIgnoreCase("-r")) {
				modifiers.remove(modifier);
				
				world.setGeneratorModifiers(modifiers);

				source.sendMessage(Text.of(TextColors.DARK_GREEN, "Removed modifier ", modifier.getId(), " to ", world.getWorldName()));
			} else {
				throw new CommandException(getHelp().getUsageText());
			}
		} catch(Exception e) {
			modifiers.add(modifier);
			
			world.setGeneratorModifiers(modifiers);

			source.sendMessage(Text.of(TextColors.DARK_GREEN, "Added modifier ", modifier.getId(), " to ", world.getWorldName()));
		}
		
		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("modifier")) {
			return list;
		}

		String[] args = arguments.split(" ");
		
		if(args.length == 1) {
			for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
				if(world.getWorldName().equalsIgnoreCase(args[0])) {
					for(WorldGeneratorModifier modifier : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
						list.add(modifier.getId());
					}
					
					return list;
				}
				
				if(world.getWorldName().toLowerCase().startsWith(args[0].toLowerCase())) {
					list.add(world.getWorldName());
				}
			}
		}
		
		if(args.length >= 2) {
			for(WorldGeneratorModifier modifier : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
				if(modifier.getId().equalsIgnoreCase(args[1])) {
					return list;
				}

				if(modifier.getId().toLowerCase().startsWith(args[1].toLowerCase())) {
					list.add(modifier.getId());
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
