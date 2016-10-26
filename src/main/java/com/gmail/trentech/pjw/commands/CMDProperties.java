package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

public class CMDProperties implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		List<Text> list = new ArrayList<>();

		list.add(Text.of(TextColors.GREEN, "Name: ", TextColors.WHITE, properties.getWorldName()));
		list.add(Text.of(TextColors.GREEN, "UUID: ", TextColors.WHITE, properties.getUniqueId().toString()));
		list.add(Text.of(TextColors.GREEN, "Dimension Id: ", TextColors.WHITE, (int) properties.getAdditionalProperties().getView(DataQuery.of("SpongeData")).get().get(DataQuery.of("dimensionId")).get()));
		list.add(Text.of(TextColors.GREEN, "Enabled: ", TextColors.WHITE, properties.isEnabled()));
		list.add(Text.of(TextColors.GREEN, "Dimension Type: ", TextColors.WHITE, properties.getDimensionType().getName().toUpperCase()));
		list.add(Text.of(TextColors.GREEN, "Generator Type: ", TextColors.WHITE, properties.getGeneratorType().getName()));
		
		Collection<WorldGeneratorModifier> modifiers = properties.getGeneratorModifiers();
		
		if(!modifiers.isEmpty()) {
			list.add(Text.of(TextColors.GREEN, "Generator Modifiers: "));
		}
		for(WorldGeneratorModifier modifier : modifiers) {
			list.add(Text.of(TextColors.GREEN, "  - ", TextColors.WHITE, modifier.getId()));
		}
		
		list.add(Text.of(TextColors.GREEN, "Seed: ", TextColors.WHITE, properties.getSeed()));
		list.add(Text.of(TextColors.GREEN, "GameMode: ", TextColors.WHITE, properties.getGameMode().getName().toUpperCase()));
		list.add(Text.of(TextColors.GREEN, "Difficulty: ", TextColors.WHITE, properties.getDifficulty().getName().toUpperCase()));
		list.add(Text.of(TextColors.GREEN, "PVP: ", TextColors.WHITE, properties.isPVPEnabled()));
		list.add(Text.of(TextColors.GREEN, "Keep Spawn Loaded: ", TextColors.WHITE, properties.doesKeepSpawnLoaded()));
		list.add(Text.of(TextColors.GREEN, "Load on Startup: ", TextColors.WHITE, properties.loadOnStartup()));
		
		long time = properties.getWorldTime();

		if(time >= 24000) {
			for(int i = 0;(time / 24000) > i; i++) {
				time = time - 24000;
			}
		}

		list.add(Text.of(TextColors.GREEN, "Time: ", TextColors.WHITE, properties.getWorldTime()));
		
		if (src instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Settings")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}

		return CommandResult.success();
	}

}
