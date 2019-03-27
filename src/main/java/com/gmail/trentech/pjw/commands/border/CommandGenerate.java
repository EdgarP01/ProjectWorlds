package com.gmail.trentech.pjw.commands.border;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.ChunkPreGenerate;
import org.spongepowered.api.world.ChunkPreGenerate.Builder;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.Main;

public class CommandGenerate implements CommandCallable {
	
	private final Help help = Help.get("world border generate").get();
	private static HashMap<String, ChunkPreGenerate> list = new HashMap<>();
	
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("generate")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		String worldName;
		
		try {
			worldName = args[0];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		if(worldName.equalsIgnoreCase("stop")) {
			if (!list.containsKey(worldName)) {
				throw new CommandException(Text.of(TextColors.YELLOW, "Pre-Generator not running for this world"), false);
			}
			list.get(worldName).cancel();
			list.remove(worldName);

			source.sendMessage(Text.of(TextColors.DARK_GREEN, "Pre-Generator stopped for ", worldName));
			return CommandResult.success();
		}
		
		if (list.containsKey(worldName)) {
			if (Sponge.getScheduler().getScheduledTasks(Main.getPlugin()).contains(list.get(worldName))) {
				throw new CommandException(Text.of(TextColors.YELLOW, "Pre-Generator already running for this world"), false);
			}
			list.remove(worldName);
		}
		
		Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(worldName);
		
		if(!optionalProperties.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " does not exist"), false);
		}
		WorldProperties properties = optionalProperties.get();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(arguments);

		if (!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " is not loaded"), false);
		}
		World world = optionalWorld.get();
		
		WorldBorder border = world.getWorldBorder();

		Builder generator = border.newChunkPreGenerate(world).owner(Main.getPlugin());

		if(args.length > 2) {
			boolean skip = false;
			
			for(int i = 1; i < args.length - 1; i++) {
				if(skip) {
					skip = false;
					continue;
				}
				
				String arg = args[i];
				String value = null;
				
				try {
					value = args[i+1];
				} catch(Exception e) {
				}
				
				if(arg.equalsIgnoreCase("-i") || arg.equalsIgnoreCase("-tickInterval")) {
					int tickInterval;
					try {
						tickInterval = Integer.parseInt(value);
					}catch (Exception e) {
						throw new CommandException(Text.of(TextColors.RED, value, " is not a valid number"), false);
					}
					
					generator.tickInterval(tickInterval);
					skip = true;
				} else if (arg.equalsIgnoreCase("-p") || arg.equalsIgnoreCase("-tickPercent")) {
					int tickPercent;
					try {
						tickPercent = Integer.parseInt(value);
					}catch (Exception e) {
						throw new CommandException(Text.of(TextColors.RED, value, " is not a valid number"), false);
					}
					
					generator.tickPercentLimit(tickPercent);
					skip = true;
				} else if (arg.equalsIgnoreCase("-c") || arg.equalsIgnoreCase("-chunkCount")) {
					int chunkCount;
					try {
						chunkCount = Integer.parseInt(value);
					}catch (Exception e) {
						throw new CommandException(Text.of(TextColors.RED, value, " is not a valid number"), false);
					}
					
					generator.chunksPerTick(chunkCount);
					skip = true;
				} else if (arg.equalsIgnoreCase("-v") || arg.equalsIgnoreCase("-verbose")) {
					generator.logger(Main.instance().getLog());
					skip = false;
				} else {
					source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Flag"));
					throw new CommandException(getHelp().getUsageText());
				}
			}
		}
		
		ChunkPreGenerate task = generator.start();

		list.put(worldName, task);

		Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.DARK_GREEN, "Pre-Generator starting for ", worldName));
		Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.GOLD, "This can cause significant lag while running"));
		
		status(task, worldName);

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("generate")) {
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
	
	private AtomicReference<Integer> time = new AtomicReference<Integer>(0);
	
	@SuppressWarnings("unlikely-arg-type")
	private void status(ChunkPreGenerate task, String worldName) {
		Sponge.getScheduler().createTaskBuilder().delayTicks(100).execute(c -> {
			if (!Sponge.getScheduler().getScheduledTasks(Main.getPlugin()).contains(task)) {
				Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.DARK_GREEN, "Pre-Generator finished for ", worldName));
			} else {
				if(time.get() == 60) {
					Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.DARK_GREEN, "Pre-Generator is running for ", worldName));
					Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.GOLD, "This can cause significant lag while running"));
					time.set(0);
				} else {
					time.set(time.get() + 5);
				}
				status(task, worldName);
			}			
		}).submit(Main.getPlugin());
	}
}
