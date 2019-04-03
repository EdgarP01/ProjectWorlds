package com.gmail.trentech.pjw.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Utils;

public class CommandRegen implements CommandCallable {
	
	private final Help help = Help.get("world regen").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("regen")) {
			throw new CommandException(getHelp().getUsageText());
		}

		List<String> args = Arrays.asList(arguments.split(" "));
		
		if(args.contains("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(args.get(0));
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, args.get(0), " does not exist"), false);
		}
		WorldProperties properties = optionalWorld.get();

		if (Sponge.getServer().getWorld(properties.getWorldName()).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " must be unloaded before you can regenerate"), false);
		}
		
		WorldArchetype.Builder builder = WorldArchetype.builder().from(properties);
		
		if(args.size() > 1) {
			if(!args.get(1).equalsIgnoreCase("true") && !args.get(1).equalsIgnoreCase("false")) {
				source.sendMessage(Text.of(TextColors.YELLOW, args.get(1), " is not a valid Boolean"));
				throw new CommandException(getHelp().getUsageText());
			}
			
			if(args.get(1).equalsIgnoreCase("false")) {
				if(args.size() == 2) {
					try {
						Long s = Long.parseLong(args.get(2));
						builder.seed(s);
					} catch (Exception e) {
						builder.seed(args.get(2).hashCode());
					}
				} else {
					builder.randomSeed();
				}
			}
		}
		
		try {
			CompletableFuture<Boolean> delete = Sponge.getServer().deleteWorld(properties);
			while (!delete.isDone()) {
			}
			if (!delete.get()) {
				throw new CommandException(Text.of(TextColors.RED, "Could not delete ", properties.getWorldName()), false);
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		source.sendMessage(Text.of(TextColors.DARK_GREEN, "Regenerating world.."));		

		WorldArchetype settings = builder.enabled(true).loadsOnStartup(true).build(properties.getWorldName() + "_" + (ThreadLocalRandom.current().nextInt(100) + 1), properties.getWorldName());

		WorldProperties newProperties;
		try {
			newProperties = Sponge.getServer().createWorldProperties(properties.getWorldName(), settings);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
		}

		Task.builder().delayTicks(20).execute(c -> {
			Optional<World> load = Sponge.getServer().loadWorld(newProperties);

			if (!load.isPresent()) {
				source.sendMessage(Text.of(TextColors.RED, "Could not load ", properties.getWorldName()));
				return;
			}


			Utils.createPlatform(load.get().getSpawnLocation().getRelative(Direction.DOWN));

			source.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " regenerated successfully"));
		}).submit(Main.getPlugin());

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
