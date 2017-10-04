package com.gmail.trentech.pjw.commands.old;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.io.WorldData;
import com.gmail.trentech.pjw.utils.Utils;

public class CMDLoad implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("world load").get();
		
		if (args.hasAny("help")) {		
			help.execute(src);
			return CommandResult.empty();
		}
		
		if (!args.hasAny("world")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (Sponge.getServer().getWorld(properties.getUniqueId()).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " is already loaded"), false);
		}

		WorldData worldData = new WorldData(properties.getWorldName());

		if (!worldData.exists()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " does not exist"), false);
		}

		SpongeData spongeData = new SpongeData(properties.getWorldName());

		if (!spongeData.exists()) {
			src.sendMessage(Text.of(TextColors.RED, "Foriegn world detected"));
			src.sendMessage(Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.runCommand("/pjw:world import")).append(Text.of(" /world import")).build());
			return CommandResult.success();
		}
		
		src.sendMessage(Text.of(TextColors.YELLOW, "Preparing spawn area. This may take a minute."));

		
		Task.builder().async().delayTicks(20).execute(c -> {
			Optional<World> load = Sponge.getServer().loadWorld(properties);

			if (!load.isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, "Could not load ", properties.getWorldName()));
				return;
			}

			if (CMDCreate.worlds.contains(properties.getWorldName())) {
				Utils.createPlatform(load.get().getSpawnLocation().getRelative(Direction.DOWN));
				CMDCreate.worlds.remove(properties.getWorldName());
			}

			src.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " loaded successfully"));
		}).submit(Main.getPlugin());

		return CommandResult.success();
	}
}