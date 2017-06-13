package com.gmail.trentech.pjw.commands;

import java.util.HashMap;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.ChunkPreGenerate;
import org.spongepowered.api.world.ChunkPreGenerate.Builder;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.Main;

public class CMDFill implements CommandExecutor {

	public static HashMap<String, ChunkPreGenerate> list = new HashMap<>();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("world fill").get();
		
		if (args.hasAny("help")) {		
			help.execute(src);
			return CommandResult.empty();
		}
		
		if (!args.hasAny("world")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!Sponge.getServer().getWorld(properties.getUniqueId()).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " must be loaded"), false);
		}
		World world = Sponge.getServer().getWorld(properties.getUniqueId()).get();
		
		if (!args.hasAny("diameter")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		String value = args.<String> getOne("diameter").get();

		if (value.equalsIgnoreCase("stop")) {
			if (!list.containsKey(properties.getWorldName())) {
				throw new CommandException(Text.of(TextColors.YELLOW, "Pre-Generator not running for this world"), false);
			}
			list.get(properties.getWorldName()).cancel();
			list.remove(properties.getWorldName());

			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Pre-Generator cancelled for ", properties.getWorldName()));
			return CommandResult.success();
		}
		
		if (list.containsKey(properties.getWorldName())) {
			if (Sponge.getScheduler().getScheduledTasks(Main.getPlugin()).contains(list.get(properties.getWorldName()))) {
				throw new CommandException(Text.of(TextColors.YELLOW, "Pre-Generator already running for this world"), false);
			}
			list.remove(properties.getWorldName());
		}
		
		double diameter;
		try {
			diameter = Double.parseDouble(value);
		} catch (Exception e) {
			throw new CommandException(Text.of(TextColors.RED, value, " is not valid"), true);
		}

		WorldBorder border = world.getWorldBorder();

		Vector3d center = border.getCenter();
		double diam = border.getDiameter();

		border.setCenter(world.getSpawnLocation().getX(), world.getSpawnLocation().getZ());
		border.setDiameter(diameter);

		Builder generator = border.newChunkPreGenerate(world).owner(Main.getPlugin());
		generator.logger(Main.instance().getLog());
		
		if (args.hasAny("interval")) {
			generator.tickInterval(args.<Integer> getOne("interval").get());		
		}

		ChunkPreGenerate task = generator.start();

		list.put(properties.getWorldName(), task);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Pre-Generator starting for ", properties.getWorldName()));
		src.sendMessage(Text.of(TextColors.GOLD, "This can cause significant lag while running"));

		status(src, task);

		border.setDiameter(diam);
		border.setCenter(center.getX(), center.getZ());

		return CommandResult.success();
	}

	private void status(CommandSource src, ChunkPreGenerate task) {
		Sponge.getScheduler().createTaskBuilder().delayTicks(100).execute(c -> {
			if (!Sponge.getScheduler().getScheduledTasks(Main.getPlugin()).contains(task)) {
				src.sendMessage(Text.of(TextColors.DARK_GREEN, "Pre-Generator finished"));
			} else {
				status(src, task);
			}
		}).submit(Main.getPlugin());
	}
}
