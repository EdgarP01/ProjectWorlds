package com.gmail.trentech.pjw.commands;

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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.core.TeleportManager;
import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.utils.Rotation;

public class CommandTeleport implements CommandCallable {
	
	private final Help help = Help.get("world teleport").get();
	public static List<String> worlds = new ArrayList<>();
	
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if (!(source instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"), false);
		}
		Player player = (Player) source;
		
		if(arguments.equalsIgnoreCase("teleport")) {
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

		Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(args.get(0));
		
		if (!optionalProperties.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, args.get(0), " does not exist"), false);
		}
		WorldProperties properties = optionalProperties.get();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getWorldName());

		if (!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " is not loaded"), false);
		}
		World world = optionalWorld.get();

		Location<World> location = world.getSpawnLocation();
		Rotation rotation = Rotation.getClosest(player.getRotation().getFloorY());
		
		boolean skip = false;

		for(String arg : args) {
			if(arg.equalsIgnoreCase(args.get(0)) || arg.equalsIgnoreCase("--force")) {
				continue;
			}
			
			if(skip) {
				skip = false;
				continue;
			}
			
			String value;
			try {
				value = args.get(args.indexOf(arg) + 1);
			} catch(Exception e) {
				throw new CommandException(getHelp().getUsageText());
			}
			
			if(arg.equalsIgnoreCase("-coords")) {
				if(value.equalsIgnoreCase("random")) {
					Optional<Location<World>> optionalLocation = TeleportManager.getRandomLocation(world, 2000);
					
					if(!optionalLocation.isPresent()) {
						throw new CommandException(Text.of(TextColors.RED, "Took to long to find a safe random location. Try again"), false);
					}
					
					location = optionalLocation.get();
				} else {
					String[] coords = value.split(",");

					try {
						int x = Integer.parseInt(coords[0]);
						int y = Integer.parseInt(coords[1]);
						int z = Integer.parseInt(coords[2]);

						location = world.getLocation(x, y, z);
					} catch (Exception e) {
						throw new CommandException(Text.of(TextColors.RED, coords.toString(), " is not a valid Coordinate"), true);
					}
				}
				skip = true;
			} else if (arg.equalsIgnoreCase("-direction")) {
				Optional<Rotation> optionalRotation = Rotation.get(value);

				if (!optionalRotation.isPresent()) {
					throw new CommandException(Text.of(TextColors.RED, "Incorrect direction"));
				}

				rotation = optionalRotation.get();
				skip = true;
			} else {
				source.sendMessage(Text.of(TextColors.YELLOW, arg, " is not a valid Flag"));
				throw new CommandException(getHelp().getUsageText());
			}
		}
		
		if(!args.contains("--force")) {
			Optional<Location<World>> optionalLocation = TeleportManager.getSafeLocation(location);

			if (!optionalLocation.isPresent()) {
				throw new CommandException(Text.of(Text.builder().color(TextColors.RED).append(Text.of("Unsafe spawn point detected. ")).onClick(TextActions.executeCallback(TeleportManager.setUnsafeLocation(location))).append(Text.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build(), TextColors.RED, " or use the --force flag to force teleport."));
			}
			
			location = optionalLocation.get();
		}
		
		player.setLocationAndRotation(location, rotation.toVector3d());

		player.sendTitle(Title.of(Text.of(TextColors.DARK_GREEN, properties.getWorldName()), Text.of(TextColors.AQUA, "x: ", location.getBlockX(), ", y: ", location.getBlockY(), ", z: ", location.getBlockZ())));

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
				list.add("-coords");
				list.add("-direction");
				list.add("--force");
			}
			
			return list;
		}
		
		if(args.size() > 1) {
			String arg = args.get(args.size() - 1);
			
			if(!arg.equalsIgnoreCase("-coords") && !arg.equalsIgnoreCase("-direction")) {
				if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
					if("-coords".startsWith(arg)) {
						list.add("-coords");
					}
					if("-direction".startsWith(arg)) {
						list.add("-direction");
					}
					if("--force".startsWith(arg)) {
						list.add("--force");
					}
				} else {
					if(!args.contains("-coords")) {
						list.add("-coords");
					}
					if(!args.contains("-direction")) {
						list.add("-direction");
					}
					if(!args.contains("--force")) {
						list.add("--force");
					}
				}
			} else {
				if(arg.equalsIgnoreCase("-direction")) {
					for(Rotation direction : Rotation.values()) {
						list.add(direction.getName());
					}
				} else {
					String parent = args.get(args.size() - 2);
					
					if(parent.equalsIgnoreCase("-direction")) {
						for(Rotation direction : Rotation.values()) {
							if(direction.getName().toLowerCase().startsWith(arg.toLowerCase())) {
								list.add(direction.getName());
							}
						}
					}
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
