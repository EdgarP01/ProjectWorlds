package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

public class CMDImport implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
//		if(!args.hasAny("name")) {
//			src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
//			src.sendMessage(Texts.of(TextColors.GOLD, "/world import <world>"));
//			return CommandResult.empty();
//		}
//
//		String worldName = args.<String>getOne("name").get();
//		
//		for(WorldProperties world : Main.getGame().getServer().getAllWorldProperties()){
//			if(world.getWorldName().equalsIgnoreCase(worldName)){
//				src.sendMessage(Texts.of(TextColors.DARK_RED, worldName, " already exists"));
//				return CommandResult.empty();
//			}
//		}
//		
//		File worldDirectory = new File(Main.getGame().getSavesDirectory() + "/" + Main.getGame().getServer().getDefaultWorld().get().getWorldName() + "/" + worldName);
//		if(!worldDirectory.exists()){
//			src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not locate ", worldName));
//			return CommandResult.empty();
//		}
//
//		File dataFile = new File(worldDirectory.getAbsolutePath(), "level_sponge.dat");
//		if(!dataFile.exists()){
//			try {
//				src.sendMessage(Texts.of(TextColors.DARK_RED, "[WARNING]", TextColors.GOLD, " Converting world to Sponge. This could break something"));
//				IOManager.init(worldName);
//				src.sendMessage(Texts.of(TextColors.DARK_GREEN, worldName, " imported successfully"));
//				return CommandResult.success();
//			} catch (IOException e) {
//				src.sendMessage(Texts.of(TextColors.DARK_RED, "Failed to convert world"));
//				e.printStackTrace();
//				return CommandResult.empty();
//			}
//		}
//
//		src.sendMessage(Texts.of(TextColors.DARK_GREEN, worldName, " imported successfully"));
		src.sendMessage(Texts.of(TextColors.DARK_RED, "import has been deprecated. use ", TextColors.GOLD, "/world load"));
		return CommandResult.success();
	}
}
