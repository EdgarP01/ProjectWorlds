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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;

public class CMDGamerule implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("world")) {
			Help help = Help.get("world gamerule").get();
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (!args.hasAny("rule")) {
			List<Text> list = new ArrayList<>();

			for (Entry<String, String> gamerule : properties.getGameRules().entrySet()) {
				list.add(Text.of(TextColors.GREEN, gamerule.getKey(), ": ", TextColors.WHITE, gamerule.getValue()));
			}

			if (src instanceof Player) {
				PaginationList.Builder pages = PaginationList.builder();

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
		String rule = args.<String> getOne("rule").get();

		if (!args.hasAny("value")) {
			src.sendMessage(Text.of(TextColors.GREEN, rule, ": ", TextColors.WHITE, properties.getGameRule(rule).get()));
			return CommandResult.success();
		}
		String value = args.<String> getOne("value").get();

		if (!isValid(rule, value)) {
			throw new CommandException(Text.of(TextColors.RED, value, " is not a valid value for gamerule ", rule), false);
		}

		properties.setGameRule(rule, value.toLowerCase());

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set gamerule ", rule, " to ", value));

		return CommandResult.success();
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
			if (Sponge.getServer().getWorld(value).isPresent() || rule.equals("default")) {
				return true;
			}
			return false;
		case "netherPortal":
			if (Sponge.getServer().getWorld(value).isPresent() || rule.equals("default")) {
				return true;
			}
			return false;
		case "endPortal":
			if (Sponge.getServer().getWorld(value).isPresent() || rule.equals("default")) {
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
