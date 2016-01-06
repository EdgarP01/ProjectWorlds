package com.gmail.trentech.pjw.commands;

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
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.io.WorldData;

public class CMDLoad implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}

		String worldName = args.<String>getOne("name").get();
		
		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " is already loaded"));
			return CommandResult.empty();
		}

		SpongeData spongeData = new SpongeData(worldName);
		
		if(!spongeData.exists()){
			src.sendMessage(Text.of(TextColors.YELLOW, "Foreign world detected"));
			if(!args.hasAny("type")){
				src.sendMessage(Text.of(TextColors.DARK_RED, "Must specify dimension type when importing worlds"));
				return CommandResult.empty();
			}
			String type = args.<String>getOne("type").get();
			
			if(!Main.getGame().getRegistry().getType(DimensionType.class, type).isPresent()){
				src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid dimension type"));
				return CommandResult.empty();
			}
			
			try {
				src.sendMessage(Text.of(TextColors.DARK_RED, "[WARNING]", TextColors.YELLOW, " Converting world to Sponge. This could break something"));
				spongeData.createNewConfig(type.toLowerCase());
				src.sendMessage(Text.of(TextColors.DARK_GREEN, "World will not load until next restart"));
				return CommandResult.success();
			} catch (IOException e) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Failed to convert world"));
				e.printStackTrace();
				return CommandResult.empty();
			}
		}else{
			if(!spongeData.isFreeDimId()){
				src.sendMessage(Text.of(TextColors.DARK_RED, "[WARNING]", TextColors.YELLOW, " World contains dimension id conflict. Attempting to repair."));
				try {
					spongeData.setDimId();
				} catch (IOException e) {
					src.sendMessage(Text.of(TextColors.DARK_RED, "Something went wrong"));
					e.printStackTrace();
				}
			}
			if(!spongeData.isCorrectLevelName()){
				src.sendMessage(Text.of(TextColors.DARK_RED, "[WARNING]", TextColors.YELLOW, " Level name mismatch. Attempting to repair."));
				try {
					spongeData.setLevelName();
				} catch (IOException e) {
					src.sendMessage(Text.of(TextColors.DARK_RED, "Something went wrong"));
					e.printStackTrace();
				}
			}
		}

		WorldData worldData = new WorldData(worldName);
		
		if(!worldData.exists()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " is not a valid world"));
			return CommandResult.empty();
		}
		
		if(!worldData.isCorrectLevelName()){
			src.sendMessage(Text.of(TextColors.DARK_RED, "[WARNING]", TextColors.YELLOW, " Level name mismatch. Attempting to repair."));
			try {
				worldData.setLevelName();
			} catch (IOException e) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Something went wrong"));
				e.printStackTrace();
			}
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
		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Required for importing worlds"))).append(Text.of("[type]")).build();
		return Text.of(t1,t2,t3);
	}
}
