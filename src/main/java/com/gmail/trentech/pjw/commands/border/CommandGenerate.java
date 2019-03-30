package com.gmail.trentech.pjw.commands.border;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
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

		List<String> args = Arrays.asList(arguments.split(" "));
		
		if(args.contains("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		String worldName;
		
		try {
			worldName = args.get(0);
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		if(args.contains("--stop")) {
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

		Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getWorldName());

		if (!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " is not loaded"), false);
		}
		World world = optionalWorld.get();
		
		WorldBorder border = world.getWorldBorder();

		Builder generator = border.newChunkPreGenerate(world).owner(Main.getPlugin());

		boolean skip = false;
		boolean log = false;
		
		for(String arg : args) {
			if(arg.equalsIgnoreCase(worldName)) {
				continue;
			}
			
			if(skip) {
				skip = false;
				continue;
			}

			if(arg.equalsIgnoreCase("-i") || arg.equalsIgnoreCase("-tickInterval")) {
				String value;
				try {
					value = args.get(args.indexOf(arg) + 1);
				} catch(Exception e) {
					throw new CommandException(getHelp().getUsageText());
				}
				
				int tickInterval;
				try {
					tickInterval = Integer.parseInt(value);		
				}catch (Exception e) {
					throw new CommandException(Text.of(TextColors.RED, value, " is not a valid number"), false);
				}
				
				generator.tickInterval(tickInterval);
				skip = true;
			} else if (arg.equalsIgnoreCase("-p") || arg.equalsIgnoreCase("-tickPercent")) {
				String value;
				try {
					value = args.get(args.indexOf(arg) + 1);
				} catch(Exception e) {
					throw new CommandException(getHelp().getUsageText());
				}
				
				int tickPercent;
				try {
					tickPercent = Integer.parseInt(value);
				}catch (Exception e) {
					throw new CommandException(Text.of(TextColors.RED, value, " is not a valid number"), false);
				}
				
				generator.tickPercentLimit(tickPercent);
				skip = true;
			} else if (arg.equalsIgnoreCase("-c") || arg.equalsIgnoreCase("-chunkCount")) {
				String value;
				try {
					value = args.get(args.indexOf(arg) + 1);
				} catch(Exception e) {
					throw new CommandException(getHelp().getUsageText());
				}
				
				int chunkCount;
				try {
					chunkCount = Integer.parseInt(value);
				}catch (Exception e) {
					throw new CommandException(Text.of(TextColors.RED, value, " is not a valid number"), false);
				}
				
				generator.chunksPerTick(chunkCount);
				skip = true;
			} else if (arg.equalsIgnoreCase("--verbose")) {
				generator.logger(Main.instance().getLog());
				skip = false;
				log = true;
			} else {
				throw new CommandException(getHelp().getUsageText());
			}
		}

		Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.DARK_GREEN, "Pre-Generator starting for ", worldName));
		Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.GOLD, "This can cause significant lag while running"));
		
		ChunkPreGenerate task = generator.start();
		
		list.put(worldName, task);
		
		update(worldName, log);

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
	
	private void update(String worldName, boolean log) {
		Sponge.getScheduler().createTaskBuilder().delay(10, TimeUnit.SECONDS).execute(c -> {
			if(!list.containsKey(worldName)) {
				return;
			}
			ChunkPreGenerate task = list.get(worldName);
			
			if(task.isCancelled()) { 
				list.remove(worldName);
				Sponge.getServer().getBroadcastChannel().send(Text.of(TextColors.DARK_GREEN, "Pre-Generator finished for ", worldName));
			} else {
				if(log) {
					DecimalFormat df = new DecimalFormat("#.00");
					double percent = (task.getTotalGeneratedChunks() + task.getTotalSkippedChunks()) * 100.0f / task.getTargetTotalChunks();
					if(percent > 100) {
						percent = 100.0;
					}
					
					MessageChannel.TO_PLAYERS.send(Text.of(					
							TextColors.DARK_GREEN, "Chunks Generated: ",
							TextColors.WHITE, (task.getTotalGeneratedChunks() + task.getTotalSkippedChunks()),
							TextColors.DARK_GREEN, ", Elapsed Time: ", 
							TextColors.WHITE, task.getTotalTime().getSeconds() /60, ":",task.getTotalTime().getSeconds() % 60,
							TextColors.DARK_GREEN, ", Complete: ",
							TextColors.WHITE, df.format(percent), "%"));
				}

				update(worldName, log);
			}		
		}).submit(Main.getPlugin());
	}
}
