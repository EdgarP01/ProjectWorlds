package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.utils.Utils;

public class CMDProperties implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("world")) {
			Help help = Help.get("world properties").get();
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		List<Text> list = new ArrayList<>();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getUniqueId());
		
		if(optionalWorld.isPresent()) {
			list.add(Text.of(TextColors.GREEN, "Name: ", TextColors.WHITE, properties.getWorldName(), TextColors.GREEN, ", DimId: ", TextColors.WHITE, (int) properties.getAdditionalProperties().getView(DataQuery.of("SpongeData")).get().get(DataQuery.of("dimensionId")).get(), TextColors.GREEN, ", Loaded: ", TextColors.WHITE, "true", TextColors.GREEN, ", Enabled: ", TextColors.WHITE, properties.isEnabled()));
		} else {
			list.add(Text.of(TextColors.GREEN, "Name: ", TextColors.WHITE, properties.getWorldName(), TextColors.GREEN, ", Loaded: ", TextColors.WHITE, "false", TextColors.GREEN, ", Enabled: ", TextColors.WHITE, properties.isEnabled()));
		}
		
		list.add(Text.of(TextColors.GREEN, "Generator: ", TextColors.WHITE, properties.getGeneratorType().getName().toLowerCase(), TextColors.GREEN, ", Dimension: ", TextColors.WHITE, properties.getDimensionType().getName().toLowerCase()));

		Collection<WorldGeneratorModifier> modifiers = properties.getGeneratorModifiers();
		
		if(!modifiers.isEmpty()) {
			list.add(Text.of(TextColors.GREEN, "Generator Modifiers: "));
		}
		for(WorldGeneratorModifier modifier : modifiers) {
			list.add(Text.of(TextColors.GREEN, "  - ", TextColors.WHITE, modifier.getId()));
		}

		list.add(Text.of(TextColors.GREEN, "GameMode: ", TextColors.WHITE, properties.getGameMode().getTranslation().get().toLowerCase(), TextColors.GREEN, ", Difficulty: ", TextColors.WHITE, properties.getDifficulty().getTranslation().get().toLowerCase(), TextColors.GREEN, ", PVP: ", TextColors.WHITE, properties.isPVPEnabled()));
		list.add(Text.of(TextColors.GREEN, "Keep Spawn Loaded: ", TextColors.WHITE, properties.doesKeepSpawnLoaded(), TextColors.GREEN, ", Load on Startup: ", TextColors.WHITE, properties.loadOnStartup()));
		list.add(Text.of(TextColors.GREEN, "Bonus Chest: ", TextColors.WHITE, properties.doesGenerateBonusChest(), TextColors.GREEN, " Map Features: ", TextColors.WHITE, properties.usesMapFeatures()));
		
		if(optionalWorld.isPresent()) {
			list.add(Text.of(TextColors.GREEN, "Time: ", TextColors.WHITE, Utils.getTime(properties.getWorldTime()), TextColors.GREEN, ", Ticks: ", TextColors.WHITE, (properties.getWorldTime() % 24000), TextColors.GREEN, ", Weather: ", TextColors.WHITE, optionalWorld.get().getWeather().getName()));
		} else {
			list.add(Text.of(TextColors.GREEN, "Time: ", TextColors.WHITE, Utils.getTime(properties.getWorldTime()), TextColors.GREEN, ", Ticks: ", TextColors.WHITE, (properties.getWorldTime() % 24000)));
		}

		list.add(Text.of(TextColors.GREEN, "UUID: ", TextColors.WHITE, properties.getUniqueId().toString()));
		list.add(Text.of(TextColors.GREEN, "Seed: ", TextColors.WHITE, properties.getSeed()));	

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
