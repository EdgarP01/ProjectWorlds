package com.gmail.trentech.pjw.commands;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.utils.Help;

public class CMDRename implements CommandExecutor {

	public CMDRename() {
		Help help = new Help("rename", "rename", " Allows for renaming worlds. World must be unloaded before you can rename world");
		help.setSyntax(" /world rename <world> <world>\n /w rn <world> <world>");
		help.setExample(" /world rename MyWorld NewWorldName");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (Sponge.getServer().getWorld(properties.getWorldName()).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, properties.getWorldName(), " must be unloaded before you can rename"));
			return CommandResult.empty();
		}

		String newWorldName = args.<String> getOne("new").get();

		if (Sponge.getServer().getWorldProperties(newWorldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, newWorldName, " already exists"));
			return CommandResult.empty();
		}

		Optional<WorldProperties> rename = Sponge.getServer().renameWorld(properties, newWorldName);

		if (!rename.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Could not rename ", properties.getWorldName()));
			return CommandResult.empty();
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " renamed to ", newWorldName, " successfully"));

		return CommandResult.success();
	}
}
