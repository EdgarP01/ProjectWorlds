package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.utils.Help;

public class CMDGamerule implements CommandExecutor {

	public CMDGamerule() {
		Help help = new Help("gamerule", "gamerule", " Configure varies world properties");
		help.setSyntax(" /gamerule <world> [rule] [value]\n /gr <world> [rule] [value]");
		help.setExample(" /gamerule MyWorld\n /gamerule MyWorld mobGriefing false\n /gamerule @w doDaylightCycle true");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String worldName = args.<String> getOne("name").get();

		if (worldName.equalsIgnoreCase("@w") && src instanceof Player) {
			worldName = ((Player) src).getWorld().getName();
		}

		if (!Sponge.getServer().getWorldProperties(worldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		WorldProperties properties = Sponge.getServer().getWorldProperties(worldName).get();

		if (!args.hasAny("rule")) {
			List<Text> list = new ArrayList<>();

			for (Entry<String, String> gamerule : properties.getGameRules().entrySet()) {
				list.add(Text.of(TextColors.GREEN, gamerule.getKey(), ": ", TextColors.WHITE, gamerule.getValue()));
			}

			if (src instanceof Player) {
				PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

				pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, properties.getWorldName())).build());

				pages.contents(list);

				pages.sendTo(src);
			} else {
				for (Text text : list) {
					src.sendMessage(text);
				}
			}

			return CommandResult.empty();
		}
		String rule = args.<String> getOne("rule").get();

		if (!properties.getGameRule(rule).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Gamerule  ", rule, " does not exist"));
			return CommandResult.empty();
		}

		if (!args.hasAny("value")) {
			List<Text> list = new ArrayList<>();

			list.add(Text.of(TextColors.GREEN, rule, ": ", TextColors.WHITE, properties.getGameRule(rule).get()));
			list.add(Text.of(TextColors.GREEN, "Command: ", invalidArg()));

			if (src instanceof Player) {
				PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

				pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, properties.getWorldName())).build());

				pages.contents(list);

				pages.sendTo(src);
			} else {
				for (Text text : list) {
					src.sendMessage(text);
				}
			}

			return CommandResult.success();
		}
		String value = args.<String> getOne("value").get();

		if (!isValid(rule, value)) {
			src.sendMessage(Text.of(TextColors.DARK_RED, value, " is not a valid value for gamerule ", rule));
			return CommandResult.empty();
		}

		properties.setGameRule(rule, value.toLowerCase());

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set gamerule ", rule, " to ", value));

		return CommandResult.success();
	}

	private Text invalidArg() {
		Text t1 = Text.of(TextColors.YELLOW, "/gamerule ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world or @w for current world"))).append(Text.of("<world> ")).build();
		Text t3 = Text.of(TextColors.YELLOW, "<rule> ");
		Text t4 = Text.of(TextColors.YELLOW, "[value]");
		return Text.of(t1, t2, t3, t4);
	}

	private boolean isValid(String rule, String value) {
		switch (rule) {
		case "randomTickSpeed":
			try {
				Long.parseLong(value);
				return true;
			} catch (Exception e) {
				return false;
			}
		case "spawnRadius":
			try {
				Long.parseLong(value);
				return true;
			} catch (Exception e) {
				return false;
			}
		case "spawnOnDeath":
			if (Sponge.getServer().getWorld(value).isPresent()) {
				return true;
			}
			return false;
		case "netherPortal":
			if (Sponge.getServer().getWorld(value).isPresent()) {
				return true;
			}
			return false;
		case "endPortal":
			if (Sponge.getServer().getWorld(value).isPresent()) {
				return true;
			}
			return false;
		default:
			return validBool(value);
		}
	}

	private boolean validBool(String value) {
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
			return true;
		}
		return false;
	}

}
