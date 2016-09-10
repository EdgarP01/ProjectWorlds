package com.gmail.trentech.pjw.commands;

import java.util.HashMap;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.utils.Help;
import com.gmail.trentech.pjw.utils.Rotation;
import com.gmail.trentech.pjw.utils.Utils;

public class CMDTeleport implements CommandExecutor {

	public static HashMap<Player, Location<World>> players = new HashMap<>();

	public CMDTeleport() {
		Help help = new Help("teleport", "teleport", " Teleport to specified world and location");
		help.setSyntax(" /world teleport <world> [-c <x,y,z>] [-d <direction>]\n /w tp <world> [-c <x,y,z>] [-d <direction>]");
		help.setExample(" /world tp MyWorld\n /world teleport MyWorld -c -153,75,300\n /world tp MyWorld -c -153,75,300 -d WEST");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			throw new CommandException(Text.of(TextColors.RED, "Must be a player"));
		}
		Player player = (Player) src;

		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getWorldName());

		if (!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " does not exist"));
		}
		World world = optionalWorld.get();

		Location<World> location = world.getSpawnLocation();

		if (args.hasAny("x,y,z")) {
			String[] coords = args.<String> getOne("x,y,z").get().split(",");

			try {
				int x = Integer.parseInt(coords[0]);
				int y = Integer.parseInt(coords[1]);
				int z = Integer.parseInt(coords[2]);

				location = world.getLocation(x, y, z);
			} catch (Exception e) {
				throw new CommandException(Text.of(TextColors.RED, "Incorrect coordinates"));
			}
		}

		Rotation rotation = Rotation.getClosest(player.getRotation().getFloorY());

		if (args.hasAny("direction")) {
			String direction = args.<String> getOne("direction").get();

			Optional<Rotation> optionalRotation = Rotation.get(direction);

			if (!optionalRotation.isPresent()) {
				throw new CommandException(Text.of(TextColors.RED, "Incorrect direction"));
			}

			rotation = optionalRotation.get();
		}

		if (!src.hasPermission("pjw.worlds." + properties.getWorldName())) {
			throw new CommandException(Text.of(TextColors.RED, "You do not have permission to travel to ", properties.getWorldName()));
		}

		TeleportHelper teleportHelper = Sponge.getGame().getTeleportHelper();

		Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(location);

		if (!optionalLocation.isPresent()) {
			throw new CommandException(Text.builder().color(TextColors.RED).append(Text.of("Unsafe spawn point detected. Teleport anyway? ")).onClick(TextActions.executeCallback(Utils.unsafe(location))).append(Text.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
		}

		player.setLocationAndRotation(optionalLocation.get(), rotation.toVector3d());

		player.sendTitle(Title.of(Text.of(TextColors.DARK_GREEN, properties.getWorldName()), Text.of(TextColors.AQUA, "x: ", location.getBlockX(), ", y: ", location.getBlockY(), ", z: ", location.getBlockZ())));

		return CommandResult.success();
	}
}
