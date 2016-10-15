package com.gmail.trentech.pjw.commands;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDUnload implements CommandExecutor {

	public CMDUnload() {
		Help help = new Help("world unload", "unload", " Unloads specified world. If players are in world, they will be teleported to default spawn", false);
		help.setPermission("pjw.cmd.world.unload");
		help.setSyntax(" /world unload <world>\n /w u <world>");
		help.setExample(" /world unload MyWorld");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getWorldName());
		
		if (!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " is already unloaded"), false);
		}
		World world = optionalWorld.get();

		if(world.getUniqueId().equals(Sponge.getServer().getDefaultWorld().get().getUniqueId())) {
			throw new CommandException(Text.of(TextColors.RED, "You cannot unload the default world"), false);
		}
		
		ConfigurationNode node = ConfigManager.get().getConfig().getNode("options");

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

		src.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " unloaded successfully"));

		return CommandResult.success();
	}

}
