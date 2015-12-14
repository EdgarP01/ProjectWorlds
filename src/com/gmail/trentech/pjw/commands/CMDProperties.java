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
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDProperties implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		ConfigurationNode config = new ConfigManager("worlds.conf").getConfig();
		
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

		if(config.getNode("Worlds", worldName) == null){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
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
		list.add(Texts.of(TextColors.DARK_PURPLE, "Difficulty: ", TextColors.GOLD, properties.getDifficulty().getName().toUpperCase()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "PVP: ", TextColors.GOLD, config.getNode("Worlds", worldName, "PVP").getString()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Respawn World: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Respawn-World").getString()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "GameMode: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Gamemode").getString()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Seed: ", TextColors.GOLD, properties.getSeed()));
		if(properties.doesKeepSpawnLoaded()){
			list.add(Texts.of(TextColors.DARK_PURPLE, "Keep Spawn Loaded: ", TextColors.GOLD, "true"));
		}else{
			list.add(Texts.of(TextColors.DARK_PURPLE, "Keep Spawn Loaded: ", TextColors.GOLD, "false"));
		}
		if(properties.isHardcore()){
			list.add(Texts.of(TextColors.DARK_PURPLE, "Hardcore: ", TextColors.GOLD, "true"));
		}else{
			list.add(Texts.of(TextColors.DARK_PURPLE, "Hardcore: ", TextColors.GOLD, "false"));
		}
		list.add(Texts.of(TextColors.DARK_PURPLE, "Time:"));
		list.add(Texts.of(TextColors.DARK_PURPLE, "    - Lock: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Time", "Lock").getString()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "    - Set: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Time", "Set").getString()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Weather:"));
		list.add(Texts.of(TextColors.DARK_PURPLE, "    - Lock: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Weather", "Lock").getString()));
		list.add(Texts.of(TextColors.DARK_PURPLE, "    - Set: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Weather", "Set").getString()));
		
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
