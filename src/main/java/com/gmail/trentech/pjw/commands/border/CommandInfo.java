package com.gmail.trentech.pjw.commands.border;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjc.help.Help;

public class CommandInfo implements CommandCallable {
	
	private final Help help = Help.get("world border info").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("info")) {
			throw new CommandException(getHelp().getUsageText());
		}

		if(arguments.contains("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}

		Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(arguments);
		
		if (!optionalProperties.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, arguments, " does not exist"), false);
		}
		WorldProperties properties = optionalProperties.get();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(arguments);

		if (!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " is not loaded"), false);
		}
		World world = optionalWorld.get();
		
		WorldBorder border = world.getWorldBorder();
		
		List<Text> list = new ArrayList<>();
		
		list.add(Text.of(TextColors.GREEN, "World: ", TextColors.WHITE, world.getName()));
		
		Vector3d center = border.getCenter();
		
		list.add(Text.of(TextColors.GREEN, "Center:"));
		list.add(Text.of(TextColors.GREEN, "  X: ", TextColors.WHITE, center.getFloorX()));
		list.add(Text.of(TextColors.GREEN, "  Y: ", TextColors.WHITE, center.getFloorY()));
		list.add(Text.of(TextColors.GREEN, "  Z: ", TextColors.WHITE, center.getFloorZ()));
		list.add(Text.of(TextColors.GREEN, "Diameter: ", TextColors.WHITE, border.getDiameter()));
		
		if(border.getDiameter() != border.getNewDiameter()) {
			list.add(Text.of(TextColors.GREEN, "New Diameter: ", TextColors.WHITE, border.getNewDiameter()));
		}
		if(border.getTimeRemaining() != 0) {
			list.add(Text.of(TextColors.GREEN, "Time Remaining: ", TextColors.WHITE, border.getTimeRemaining()));
		}
		
		list.add(Text.of(TextColors.GREEN, "Warning Distance: ", TextColors.WHITE, border.getWarningDistance()));
		list.add(Text.of(TextColors.GREEN, "Warning Time: ", TextColors.WHITE, border.getWarningTime()));
		list.add(Text.of(TextColors.GREEN, "Damage Amount: ", TextColors.WHITE, border.getDamageAmount()));
		list.add(Text.of(TextColors.GREEN, "Damage Threshold: ", TextColors.WHITE, border.getDamageThreshold()));
		
		if (source instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Border")).build());

			pages.contents(list);

			pages.sendTo(source);
		} else {
			for (Text text : list) {
				source.sendMessage(text);
			}
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
