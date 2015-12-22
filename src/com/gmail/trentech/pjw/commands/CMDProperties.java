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
import org.spongepowered.api.text.Texts;
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
				Text t1 = Texts.of(TextColors.GOLD, "/world properties ");
				Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter world or @w for current world"))).append(Texts.of("[world] ")).build();
				src.sendMessage(Texts.of(t1,t2));
				return CommandResult.empty();
			}
			Player player = (Player) src;		
			worldName = player.getWorld().getName();
		}

		if(!Main.getGame().getServer().getWorldProperties(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		WorldProperties properties = Main.getGame().getServer().getWorldProperties(worldName).get();

		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Texts.builder().color(TextColors.DARK_PURPLE).append(Texts.of(TextColors.GOLD, "SETTINGS")).build());
		
		List<Text> list = new ArrayList<>();
		
		list.add(Texts.of(TextColors.DARK_PURPLE, "Name: ", TextColors.GOLD, worldName));
		list.add(Texts.of(TextColors.DARK_PURPLE, "UUID: ", TextColors.GOLD, properties.getUniqueId().toString()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Dimension Type: ", TextColors.GOLD, properties.getDimensionType().getName().toUpperCase()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Generator Type: ", TextColors.GOLD, properties.getGeneratorType().getName().toUpperCase()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Seed: ", TextColors.GOLD, properties.getSeed()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "GameMode: ", TextColors.GOLD, properties.getGameRule("gamemode").get()));		
		list.add(Texts.of(TextColors.DARK_PURPLE, "Difficulty: ", TextColors.GOLD, properties.getDifficulty().getName().toUpperCase()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "PVP: ", TextColors.GOLD, properties.getGameRule("pvp").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Keep Spawn Loaded: ", TextColors.GOLD, properties.doesKeepSpawnLoaded()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Hardcore: ", TextColors.GOLD, properties.isHardcore()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Respawn World: ", TextColors.GOLD, properties.getGameRule("respawnWorld").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Default Weather: ", TextColors.GOLD, properties.getGameRule("defaultWeather").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Command Block Output: ", TextColors.GOLD, properties.getGameRule("commandBlockOutput").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Freeze Time: ", TextColors.GOLD, properties.getGameRule("doDaylightCycle").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Fire Spread: ", TextColors.GOLD, properties.getGameRule("doFireTick").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Mob Loot: ", TextColors.GOLD, properties.getGameRule("doMobLoot").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Mob Spawning: ", TextColors.GOLD, properties.getGameRule("doMobSpawning").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Tile Drops: ", TextColors.GOLD, properties.getGameRule("doTileDrops").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Keep Inventory: ", TextColors.GOLD, properties.getGameRule("keepInventory").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Log Admin Commands: ", TextColors.GOLD, properties.getGameRule("logAdminCommands").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Mob Griefing: ", TextColors.GOLD, properties.getGameRule("mobGriefing").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Natural Regeneration: ", TextColors.GOLD, properties.getGameRule("naturalRegeneration").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Tick Speed: ", TextColors.GOLD, properties.getGameRule("randomTickSpeed").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Reduced Debug Info: ", TextColors.GOLD, properties.getGameRule("reducedDebugInfo").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Send Command Feedback: ", TextColors.GOLD, properties.getGameRule("sendCommandFeedback").get()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Show Death Messages: ", TextColors.GOLD, properties.getGameRule("showDeathMessages").get()));

		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
