package com.gmail.trentech.pjw.commands;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.IOManager;

public class CMDLoad implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		String worldName = args.<String>getOne("name").get();
		
		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, "World ", worldName, " is already loaded"));
			return CommandResult.empty();
		}

		File worldDirectory = new File(Main.getGame().getSavesDirectory() + "/" + Main.getGame().getServer().getDefaultWorld().get().getWorldName() + "/" + worldName);
		if(!worldDirectory.exists()){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Could not locate ", worldName));
			return CommandResult.empty();
		}

		File dataFile = new File(worldDirectory.getAbsolutePath(), "level_sponge.dat");
		if(!dataFile.exists()){
			try {
				src.sendMessage(Text.of(TextColors.DARK_RED, "[WARNING]", TextColors.GOLD, " Converting world to Sponge. This could break something"));
				IOManager.init(worldName);
				src.sendMessage(Text.of(TextColors.DARK_GREEN, "World will not load until next restart"));
				return CommandResult.success();
			} catch (IOException e) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Failed to convert world"));
				e.printStackTrace();
				return CommandResult.empty();
			}
		}
		
		try {
			if(IOManager.dimensionIdExists(IOManager.getDimenionId(worldName))){
				src.sendMessage(Text.of(TextColors.DARK_RED, "[WARNING]", TextColors.GOLD, " World contains dimension id conflict. attempting to repair."));
				IOManager.init(worldName);
			}
		} catch (IOException e) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Failed to repair world"));
			e.printStackTrace();
			return CommandResult.empty();
		}

		Optional<World> load = Main.getGame().getServer().loadWorld(worldName);
		
		if(!load.isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Could not load ", worldName));
			return CommandResult.empty();
		}	

		src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " loaded successfully"));
		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Text.of(TextColors.YELLOW, "/world load ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world or @w for current world"))).append(Text.of("<world> ")).build();
		return Text.of(t1,t2);
	}
}
