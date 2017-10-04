package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.Main;

import ninja.leaping.configurate.ConfigurationNode;

public class CommandUnload implements CommandCallable {
	
	private final Help help = Help.get("world unload").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("unload")) {
			throw new CommandException(getHelp().getUsageText());
		}

		if(arguments.equalsIgnoreCase("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(arguments);
		
		if(!optionalProperties.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, arguments, " does not exist"), false);
		}
		WorldProperties properties = optionalProperties.get();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getWorldName());
		
		if (!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " is already unloaded"), false);
		}
		World world = optionalWorld.get();

		if(world.getUniqueId().toString().equals(ConfigManager.get(Main.getPlugin()).getConfig().getNode("options", "world_root").getString())) {
			throw new CommandException(Text.of(TextColors.RED, "You cannot unload the default world"), false);
		}
		
		ConfigurationNode node = ConfigManager.get(Main.getPlugin()).getConfig().getNode("options");

		World defaultWorld = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorld().get().getWorldName()).get();

		String joinWorldName = node.getNode("first_join", "world").getString();

		optionalWorld = Sponge.getServer().getWorld(joinWorldName);

		if (optionalWorld.isPresent()) {
			defaultWorld = optionalWorld.get();
		}

		for (Entity entity : world.getEntities()) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				
				player.setLocationSafely(defaultWorld.getSpawnLocation());
				player.sendMessage(Text.of(TextColors.YELLOW, properties.getWorldName(), " is being unloaded"));
			}
		}

		if (!Sponge.getServer().unloadWorld(world)) {
			throw new CommandException(Text.of(TextColors.RED, "Could not unload ", properties.getWorldName()), false);
		}

		source.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " unloaded successfully"));
		
		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("unload")) {
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
