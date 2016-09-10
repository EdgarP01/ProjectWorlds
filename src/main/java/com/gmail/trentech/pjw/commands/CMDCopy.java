package com.gmail.trentech.pjw.commands;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

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

public class CMDCopy implements CommandExecutor {

	public CMDCopy() {
		Help help = new Help("copy", "copy", " Allows you to make a new world from an existing world");
		help.setSyntax(" /world copy <world> <world>\n /w cp <world> <world>");
		help.setExample(" /world copy srcWorld newWorld\n /world copy @w newWorld");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("oldWorld").get();

		String newWorldName = args.<String> getOne("newWorld").get();

		if (Sponge.getServer().getWorldProperties(newWorldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, newWorldName, " already exists"));
		}

		Optional<WorldProperties> copy = null;
		try {
			copy = Sponge.getServer().copyWorld(properties, newWorldName).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		if (!copy.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, "Could not copy ", properties.getWorldName()));
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " copied to ", newWorldName));

		return CommandResult.success();
	}
}
