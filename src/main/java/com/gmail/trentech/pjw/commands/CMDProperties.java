package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.utils.Help;

public class CMDProperties implements CommandExecutor {

	public CMDProperties() {
		Help help = new Help("properties", "properties", " View all properties associated with a world");
		help.setPermission("pjw.cmd.world.properties");
		help.setSyntax(" /world properties <world>\n /w pp <world>");
		help.setExample(" /world properties\n /world properties MyWorld");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		List<Text> list = new ArrayList<>();

		list.add(Text.of(TextColors.GREEN, "Name: ", TextColors.WHITE, properties.getWorldName()));
		list.add(Text.of(TextColors.GREEN, "UUID: ", TextColors.WHITE, properties.getUniqueId().toString()));
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
		list.add(Text.of(TextColors.GREEN, "Hardcore: ", TextColors.WHITE, properties.isHardcore()));

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
