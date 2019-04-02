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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;

public class CommandCenter implements CommandCallable {
	
	private final Help help = Help.get("world border center").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("center")) {
			throw new CommandException(getHelp().getUsageText());
		}

		List<String> args = Arrays.asList(arguments.split(" "));
		
		if(args.contains("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		if(args.isEmpty() || args.size() == 2) {
			throw new CommandException(getHelp().getUsageText());
		}

		Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(args.get(0));
		
		if(!optionalProperties.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, args.get(0), " does not exist"), false);
		}
		WorldProperties properties = optionalProperties.get();
		
		Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getUniqueId());

		if (!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " must be loaded"));
		}
		World world = optionalWorld.get();
		
		WorldBorder border = world.getWorldBorder();
		
		double x;
		double z;
		
		if(args.size() == 3) {
			try {
				x = Double.parseDouble(args.get(1));
			} catch(Exception e) {
				throw new CommandException(Text.of(TextColors.RED, args.get(1), " is not a number value"), true);
			}
			
			try {
				z = Double.parseDouble(args.get(2));
			} catch(Exception e) {
				throw new CommandException(Text.of(TextColors.RED, args.get(2), " is not a number value"), true);
			}
		} else {
			if(!(source instanceof Player)) {
				throw new CommandException(Text.of(TextColors.RED, "You need to specify x and z coordinates.", true));
			} else if(!((Player) source).getWorld().equals(world)) {
				throw new CommandException(Text.of(TextColors.RED, "You need to specify x and z coordinates. You are not standing in the provided world."), true);
			}
			Location<World>location = ((Player) source).getLocation();
			
			x = location.getX();
			z = location.getZ();
		}
		
		border.setCenter(x, z);

		source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set center of ", world.getName(), " to x: ", x, " z: ", z));
		
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
