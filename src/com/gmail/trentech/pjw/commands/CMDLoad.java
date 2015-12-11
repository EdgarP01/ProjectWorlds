package com.gmail.trentech.pjw.commands;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.IOManager;

public class CMDLoad implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
			src.sendMessage(Texts.of(TextColors.GOLD, "/world load <world>"));
			return CommandResult.empty();
		}

		String worldName = args.<String>getOne("name").get();
		
		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " is already loaded"));
			return CommandResult.empty();
		}

		File worldDirectory = new File(Main.getGame().getSavesDirectory() + "/" + Main.getGame().getServer().getDefaultWorld().get().getWorldName() + "/" + worldName);
		if(!worldDirectory.exists()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not locate ", worldName));
			return CommandResult.empty();
		}

		File dataFile = new File(worldDirectory.getAbsolutePath(), "level_sponge.dat");
		if(!dataFile.exists()){
			try {
				src.sendMessage(Texts.of(TextColors.DARK_RED, "[WARNING]", TextColors.GOLD, " Converting world to Sponge. This could break something"));
				IOManager.init(worldName);
			} catch (IOException e) {
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Failed to convert world"));
				e.printStackTrace();
				return CommandResult.empty();
			}
		}
		
		try {
			if(IOManager.dimensionIdExists(IOManager.getDimenionId(worldName))){
				src.sendMessage(Texts.of(TextColors.DARK_RED, "[WARNING]", TextColors.GOLD, " World contains dimension id comflict. attempting to repair."));
				IOManager.init(worldName);
			}
		} catch (IOException e) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Failed to repair world"));
			e.printStackTrace();
			return CommandResult.empty();
		}

		Optional<World> load = Main.getGame().getServer().loadWorld(worldName);
		
		if(!load.isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not load ", worldName));
			return CommandResult.empty();
		}	

		src.sendMessage(Texts.of(TextColors.DARK_GREEN, worldName, " loaded successfully"));
		return CommandResult.success();
	}
}
