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
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.io.WorldData;
import com.gmail.trentech.pjw.utils.Help;
import com.gmail.trentech.pjw.utils.Utils;

public class CMDLoad implements CommandExecutor {

	public CMDLoad() {
		Help help = new Help("load", "load", " Loads sepcified world. If world is a non Sponge created world you will need to specify a dimension type to import");
		help.setSyntax(" /world load <world> [type]\n /w l <world> [type]");
		help.setExample(" /world load NewWorld\n /world load BukkitWorld overworld");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/world load <world>"));
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();
		
		if(Main.getGame().getServer().getWorld(worldName).isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " is already loaded"));
			return CommandResult.empty();
		}

		SpongeData spongeData = new SpongeData(worldName);

		if(!spongeData.exists()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Foriegn world detected"));
			src.sendMessage(Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Click command for more information ")))
						.onClick(TextActions.runCommand("/pjw:world import")).append(Text.of(" /world import")).build());
			return CommandResult.empty();
		}
		
//		if(!spongeData.isFreeDimId()) {
//			src.sendMessage(Text.of(TextColors.DARK_RED, "[WARNING]", TextColors.YELLOW, " World contains dimension id conflict. Attempting to repair."));
//			try {
//				spongeData.setDimId();
//			} catch (IOException e) {
//				src.sendMessage(Text.of(TextColors.DARK_RED, "Something went wrong"));
//				e.printStackTrace();
//			}
//		}

		WorldData worldData = new WorldData(worldName);
		
		if(!worldData.exists()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " is not a valid world"));
			return CommandResult.empty();
		}
		
		if(!worldData.isCorrectLevelName()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "[WARNING]", TextColors.YELLOW, " Level name mismatch. Attempting to repair."));
			try {
				worldData.setLevelName();
			} catch (IOException e) {
				src.sendMessage(Text.of(TextColors.DARK_RED, "Something went wrong"));
				e.printStackTrace();
			}
		}
		
		Optional<WorldProperties> optionalProperties = Main.getGame().getServer().getWorldProperties(worldName);
		
		if(!optionalProperties.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "Could not find ", worldName));
		}
		
		WorldProperties properties = optionalProperties.get();
		
        src.sendMessage(Text.of(TextColors.YELLOW, "Preparing spawn area. This may take a minute."));

		Main.getGame().getScheduler().createTaskBuilder().name("PJW" + worldName).delayTicks(20).execute(new Runnable() {

			@Override
			public void run() {
				Optional<World> load = Main.getGame().getServer().loadWorld(properties);

				if(!load.isPresent()) {	
					src.sendMessage(Text.of(TextColors.DARK_RED, "Could not load ", worldName));
					return;
				}

				World world = load.get();
				world.setKeepSpawnLoaded(true);
				
				if(CMDCreate.worlds.contains(worldName)) {
					Utils.createPlatform(load.get().getSpawnLocation().getRelative(Direction.DOWN));
					CMDCreate.worlds.remove(worldName);
				}

				src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " loaded successfully"));
			}
			
		}).submit(Main.getPlugin());

		return CommandResult.success();
	}
}
