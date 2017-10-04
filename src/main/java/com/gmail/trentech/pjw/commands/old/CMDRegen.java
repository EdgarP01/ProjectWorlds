package com.gmail.trentech.pjw.commands.old;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Utils;

public class CMDRegen implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("world regen").get();
		
		if (args.hasAny("help")) {		
			help.execute(src);
			return CommandResult.empty();
		}
		
		if (!args.hasAny("world")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (Sponge.getServer().getWorld(properties.getWorldName()).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " must be unloaded before you can regenerate"), false);
		}
		
		WorldArchetype.Builder builder = WorldArchetype.builder().from(properties);

		if (args.hasAny("true|false")) {
			if(!args.<Boolean> getOne("true|false").get()) {
				if (args.hasAny("seed")) {
					String seed = args.<String> getOne("seed").get();

					try {
						Long s = Long.parseLong(seed);
						builder.seed(s);
					} catch (Exception e) {
						builder.seed(seed.hashCode());
					}
				} else {
					builder.randomSeed();
				}
			}
		}

		try {
			CompletableFuture<Boolean> delete = Sponge.getServer().deleteWorld(properties);
			while (!delete.isDone()) {
			}
			if (!delete.get()) {
				throw new CommandException(Text.of(TextColors.RED, "Could not delete ", properties.getWorldName()), false);
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Regenerating world.."));		

		WorldArchetype settings = builder.enabled(true).loadsOnStartup(true).randomSeed().build(properties.getWorldName(), properties.getWorldName());

		WorldProperties newProperties;
		try {
			newProperties = Sponge.getServer().createWorldProperties(properties.getWorldName(), settings);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
		}

		
		Task.builder().async().delayTicks(20).execute(c -> {
			Optional<World> load = Sponge.getServer().loadWorld(newProperties);

			if (!load.isPresent()) {
				src.sendMessage(Text.of(TextColors.RED, "Could not load ", properties.getWorldName()));
				return;
			}


			Utils.createPlatform(load.get().getSpawnLocation().getRelative(Direction.DOWN));

			src.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " regenerated successfully"));
		}).submit(Main.getPlugin());
//		
//		Optional<World> load = Sponge.getServer().loadWorld(newProperties);
//
//		if (!load.isPresent()) {
//			throw new CommandException(Text.of(TextColors.RED, "Could not load ", properties.getWorldName()), false);
//		}
//
//		Utils.createPlatform(load.get().getSpawnLocation().getRelative(Direction.DOWN));
//
//		src.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " regenerated successfully"));

		return CommandResult.success();
	}
}
