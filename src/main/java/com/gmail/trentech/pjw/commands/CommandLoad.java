package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.io.WorldData;
import com.gmail.trentech.pjw.utils.Utils;

public class CommandLoad implements CommandCallable {
	
	private final Help help = Help.get("world load").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("load")) {
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
		
		if (Sponge.getServer().getWorld(world.getUniqueId()).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, world.getWorldName(), " is already loaded"), false);
		}

		WorldData worldData = new WorldData(world.getWorldName());

		if (!worldData.exists()) {
			throw new CommandException(Text.of(TextColors.RED, world.getWorldName(), " does not exist"), false);
		}

		SpongeData spongeData = new SpongeData(world.getWorldName());

		if (!spongeData.exists()) {
			source.sendMessage(Text.of(TextColors.RED, "Foriegn world detected"));
			source.sendMessage(Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.runCommand("/pjw:world import")).append(Text.of(" /world import")).build());
			return CommandResult.success();
		}
		
		source.sendMessage(Text.of(TextColors.YELLOW, "Preparing spawn area. This may take a minute."));

		
		Task.builder().delayTicks(20).execute(c -> {
			Optional<World> load = Sponge.getServer().loadWorld(world);

			if (!load.isPresent()) {
				source.sendMessage(Text.of(TextColors.RED, "Could not load ", world.getWorldName()));
				return;
			}

			if (CommandCreate.worlds.contains(world.getWorldName())) {
				Utils.createPlatform(load.get().getSpawnLocation().getRelative(Direction.DOWN));
				CommandCreate.worlds.remove(world.getWorldName());
			}

			source.sendMessage(Text.of(TextColors.DARK_GREEN, world.getWorldName(), " loaded successfully"));
		}).submit(Main.getPlugin());
		
		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("load")) {
			return list;
		}

		for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
			if(world.getWorldName().equalsIgnoreCase(arguments)) {
				return list;
			}
			
			if(world.getWorldName().toLowerCase().startsWith(arguments.toLowerCase())) {
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
