package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.utils.Gamemode;

public class CommandGamemode implements CommandCallable {
	
	private final Help help = Help.get("world gamemode").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("gamemode")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		String worldName;
		String gm;
		
		try {
			worldName = args[0];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		try {
			gm = args[1];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		Optional<WorldProperties> optionalWorld = Sponge.getServer().getWorldProperties(worldName);
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " does not exist"), false);
		}
		WorldProperties world = optionalWorld.get();

		Optional<GameMode> optionalGameMode = Gamemode.get(gm);
		
		if(!optionalGameMode.isPresent()) {
			try {
				optionalGameMode = Gamemode.get(Integer.parseInt(gm));
			} catch(Exception e) {
				source.sendMessage(Text.of(TextColors.YELLOW, gm, " is not a valid GameMode"));
				throw new CommandException(getHelp().getUsageText());
			}
			
			if(!optionalGameMode.isPresent()) {
				source.sendMessage(Text.of(TextColors.YELLOW, gm, " is not a valid GameMode"));
				throw new CommandException(getHelp().getUsageText());
			}
		}
		
		GameMode gameMode = optionalGameMode.get();
		
		world.setGameMode(gameMode);
		Sponge.getServer().saveWorldProperties(world);
		source.sendMessage(Text.of(TextColors.DARK_GREEN, "Set gamemode of ", world.getWorldName(), " to ", TextColors.YELLOW, gameMode.getTranslation().get().toUpperCase()));
		
		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("gamemode")) {
			return list;
		}

		String[] args = arguments.split(" ");
		
		if(args.length == 1) {
			for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
				if(world.getWorldName().equalsIgnoreCase(args[0])) {
					for(Gamemode gamemode : Gamemode.values()) {
						list.add(gamemode.getGameMode().getName());
						list.add(Integer.toString(gamemode.getIndex()));
					}
					
					return list;
				}
				
				if(world.getWorldName().toLowerCase().startsWith(args[0].toLowerCase())) {
					list.add(world.getWorldName());
				}
			}
		}
		
		if(args.length == 2) {
			for(Gamemode gamemode : Gamemode.values()) {
				if(gamemode.getGameMode().getName().equalsIgnoreCase(args[1])) {
					return list;
				}
				try {
					Integer.parseInt(args[1]);
				} catch (Exception e) {
					if(gamemode.getGameMode().getName().toLowerCase().startsWith(args[1].toLowerCase())) {
						list.add(gamemode.getGameMode().getName());
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
