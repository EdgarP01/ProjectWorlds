package com.gmail.trentech.pjw.commands;

import java.io.IOException;
import java.util.ArrayList;
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

		if (Sponge.getServer().getWorld(properties.getWorldName()).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " must be unloaded before you can regenerate"), false);
		}
		
		WorldArchetype.Builder builder = WorldArchetype.builder().from(properties);
		
		String bool;

		try {
			bool = args[1];
			
			if(!bool.equalsIgnoreCase("true") && !bool.equalsIgnoreCase("false")) {
				source.sendMessage(Text.of(TextColors.YELLOW, bool, " is not a valid Boolean"));
				throw new CommandException(getHelp().getUsageText());
			}
			
			String seed;
			
			try {
				seed = args[2];

				try {
					Long s = Long.parseLong(seed);
					builder.seed(s);
				} catch (Exception e) {
					builder.seed(seed.hashCode());
				}
			} catch (Exception e) {
				builder.randomSeed();
			}
		} catch(Exception e) {}

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
		
		if(arguments.equalsIgnoreCase("regen")) {
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
		
		if(args.length >= 2) {
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
