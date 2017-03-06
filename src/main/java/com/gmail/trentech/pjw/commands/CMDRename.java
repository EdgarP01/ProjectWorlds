package com.gmail.trentech.pjw.commands;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;

public class CMDRename implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!args.hasAny("srcWorld")) {
			Help help = Help.get("world rename").get();
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		WorldProperties properties = args.<WorldProperties> getOne("srcWorld").get();

		if (Sponge.getServer().getWorld(properties.getWorldName()).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " must be unloaded before you can rename"), false);
		}

		if (!args.hasAny("newWorld")) {
			Help help = Help.get("world rename").get();
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		String newWorldName = args.<String> getOne("newWorld").get();

		if (Sponge.getServer().getWorldProperties(newWorldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, newWorldName, " already exists"), false);
		}

		Optional<WorldProperties> rename = Sponge.getServer().renameWorld(properties, newWorldName);

		if (!rename.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "Could not rename ", properties.getWorldName()), false);
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " renamed to ", newWorldName, " successfully"));

		return CommandResult.success();
	}
}
