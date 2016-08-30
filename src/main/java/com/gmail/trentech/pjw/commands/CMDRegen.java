package com.gmail.trentech.pjw.commands;

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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.utils.Help;
import com.gmail.trentech.pjw.utils.Utils;

public class CMDRegen implements CommandExecutor {

	public CMDRegen() {
		Help help = new Help("regen", "regen", " Regenerates a world. You can preserve the seed or generate new random");
		help.setSyntax(" /world regen <world> [true/false]\n /w r <world>  [true/false]");
		help.setExample(" /world regen MyWorld\n /world regen MyWorld true");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (Sponge.getServer().getWorld(properties.getWorldName()).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, properties.getWorldName(), " must be unloaded before you can rename"));
			return CommandResult.empty();
		}
		
		WorldArchetype.Builder builder = WorldArchetype.builder().dimension(properties.getDimensionType()).generatorSettings(properties.getGeneratorSettings());

		if (args.hasAny("value") && args.<Boolean> getOne("value").get()) {
			builder.seed(properties.getSeed());
		}

		try {
			CompletableFuture<Boolean> delete = Sponge.getServer().deleteWorld(properties);
			while (!delete.isDone()) {
			}
			if (!delete.get()) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Could not delete ", properties.getWorldName()));
				return CommandResult.empty();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Regenerating world.."));		

		WorldArchetype settings = builder.enabled(true).loadsOnStartup(true).build(properties.getWorldName(), properties.getWorldName());

		WorldProperties newProperties;
		try {
			newProperties = Sponge.getServer().createWorldProperties(properties.getWorldName(), settings);
		} catch (IOException e) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Something went wrong. Check server log for details"));
			e.printStackTrace();
			return CommandResult.empty();
		}

		Optional<World> load = Sponge.getServer().loadWorld(newProperties);

		if (!load.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Could not load ", properties.getWorldName()));
			return CommandResult.empty();
		}

		Utils.createPlatform(load.get().getSpawnLocation().getRelative(Direction.DOWN));

		src.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " regenerated successfully"));

		return CommandResult.success();
	}
}
