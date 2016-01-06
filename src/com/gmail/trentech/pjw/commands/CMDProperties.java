package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;

public class CMDProperties implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String worldName;
		if(args.hasAny("name")) {
			worldName = args.<String>getOne("name").get();
		}else{
			if(!(src instanceof Player)){
				Text t1 = Text.of(TextColors.YELLOW, "/world properties ");
				Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world or @w for current world"))).append(Text.of("[world] ")).build();
				src.sendMessage(Text.of(t1,t2));
				return CommandResult.empty();
			}
			Player player = (Player) src;		
			worldName = player.getWorld().getName();
		}

		if(!Main.getGame().getServer().getWorldProperties(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		WorldProperties properties = Main.getGame().getServer().getWorldProperties(worldName).get();

		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, "Settings")).build());
		
		List<Text> list = new ArrayList<>();
		
		list.add(Text.of(TextColors.AQUA, "Name: ", TextColors.GREEN, worldName));
		list.add(Text.of(TextColors.AQUA, "UUID: ", TextColors.GREEN, properties.getUniqueId().toString()));
		list.add(Text.of(TextColors.AQUA, "Enabled: ", TextColors.GREEN, properties.isEnabled()));
		list.add(Text.of(TextColors.AQUA, "Dimension Type: ", TextColors.GREEN, properties.getDimensionType().getName().toUpperCase()));
		list.add(Text.of(TextColors.AQUA, "Generator Type: ", TextColors.GREEN, properties.getGeneratorType().getName().toUpperCase()));
		list.add(Text.of(TextColors.AQUA, "Seed: ", TextColors.GREEN, properties.getSeed()));
		list.add(Text.of(TextColors.AQUA, "GameMode: ", TextColors.GREEN, properties.getGameRule("gamemode").get()));		
		list.add(Text.of(TextColors.AQUA, "Difficulty: ", TextColors.GREEN, properties.getDifficulty().getName().toUpperCase()));
		list.add(Text.of(TextColors.AQUA, "PVP: ", TextColors.GREEN, properties.isPVPEnabled()));
		list.add(Text.of(TextColors.AQUA, "Keep Spawn Loaded: ", TextColors.GREEN, properties.doesKeepSpawnLoaded()));
		list.add(Text.of(TextColors.AQUA, "Hardcore: ", TextColors.GREEN, properties.isHardcore()));
		list.add(Text.of(TextColors.AQUA, "Respawn World: ", TextColors.GREEN, properties.getGameRule("respawnWorld").get()));
		list.add(Text.of(TextColors.AQUA, "Freeze Weather: ", TextColors.GREEN, properties.getGameRule("doWeatherCycle").get()));
		list.add(Text.of(TextColors.AQUA, "Command Block Output: ", TextColors.GREEN, properties.getGameRule("commandBlockOutput").get()));
		list.add(Text.of(TextColors.AQUA, "Freeze Time: ", TextColors.GREEN, properties.getGameRule("doDaylightCycle").get()));
		list.add(Text.of(TextColors.AQUA, "Fire Spread: ", TextColors.GREEN, properties.getGameRule("doFireTick").get()));
		list.add(Text.of(TextColors.AQUA, "Mob Loot: ", TextColors.GREEN, properties.getGameRule("doMobLoot").get()));
		list.add(Text.of(TextColors.AQUA, "Mob Spawning: ", TextColors.GREEN, properties.getGameRule("doMobSpawning").get()));
		list.add(Text.of(TextColors.AQUA, "Tile Drops: ", TextColors.GREEN, properties.getGameRule("doTileDrops").get()));
		list.add(Text.of(TextColors.AQUA, "Keep Inventory: ", TextColors.GREEN, properties.getGameRule("keepInventory").get()));
		list.add(Text.of(TextColors.AQUA, "Log Admin Commands: ", TextColors.GREEN, properties.getGameRule("logAdminCommands").get()));
		list.add(Text.of(TextColors.AQUA, "Mob Griefing: ", TextColors.GREEN, properties.getGameRule("mobGriefing").get()));
		list.add(Text.of(TextColors.AQUA, "Natural Regeneration: ", TextColors.GREEN, properties.getGameRule("naturalRegeneration").get()));
		list.add(Text.of(TextColors.AQUA, "Tick Speed: ", TextColors.GREEN, properties.getGameRule("randomTickSpeed").get()));
		list.add(Text.of(TextColors.AQUA, "Reduced Debug Info: ", TextColors.GREEN, properties.getGameRule("reducedDebugInfo").get()));
		list.add(Text.of(TextColors.AQUA, "Send Command Feedback: ", TextColors.GREEN, properties.getGameRule("sendCommandFeedback").get()));
		list.add(Text.of(TextColors.AQUA, "Show Death Messages: ", TextColors.GREEN, properties.getGameRule("showDeathMessages").get()));

		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
