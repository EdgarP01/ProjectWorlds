package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.Arrays;
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

		List<String> args = Arrays.asList(arguments.split(" "));
		
		if(args.contains("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		if(args.size() < 1) {
			throw new CommandException(getHelp().getUsageText());
		}

		Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(args.get(0));
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, args.get(0), " does not exist"), false);
		}
		WorldProperties world = optionalWorld.get();

		Collection<WorldGeneratorModifier> modifiers = world.getGeneratorModifiers();
		
		if(args.size() == 1) {
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

		Optional<WorldGeneratorModifier> optionalModifier = Sponge.getRegistry().getType(WorldGeneratorModifier.class, args.get(1));
		
		if(!optionalModifier.isPresent()) {
			source.sendMessage(Text.of(TextColors.YELLOW, args.get(1), " is not a valid WorldGeneratorModifier"));
			throw new CommandException(getHelp().getUsageText());
		}	
		WorldGeneratorModifier modifier = optionalModifier.get();

		if(args.size() == 3) {
			if(args.get(2).equalsIgnoreCase("--remove")) {
				modifiers.remove(modifier);		
				world.setGeneratorModifiers(modifiers);

				source.sendMessage(Text.of(TextColors.DARK_GREEN, "Removed modifier ", modifier.getId(), " to ", world.getWorldName()));
			} else {
				throw new CommandException(getHelp().getUsageText());
			}
		} else {
			modifiers.add(modifier);
			world.setGeneratorModifiers(modifiers);

			source.sendMessage(Text.of(TextColors.DARK_GREEN, "Added modifier ", modifier.getId(), " to ", world.getWorldName()));
		}
		
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
				for(WorldGeneratorModifier modifier : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
					list.add(modifier.getId());
				}
			}
			
			return list;
		}

		if(args.size() == 2) {
			if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
				for(WorldGeneratorModifier modifier : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
					if(modifier.getId().equalsIgnoreCase(args.get(1))) {
						list.add(modifier.getId());
					}

					if(modifier.getId().toLowerCase().startsWith(args.get(1).toLowerCase())) {
						list.add(modifier.getId());
					}
				}
			} else {
				list.add("--remove");
			}
			
			return list;
		}

		if(args.size() == 3) {
			if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
				if("--remove".equalsIgnoreCase(args.get(2))) {
					list.add("--remove");
				}
				if("--remove".startsWith(args.get(2).toLowerCase())) {
					list.add("--remove");
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
