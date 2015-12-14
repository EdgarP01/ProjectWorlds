package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDGamemode implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();
		
		if(worldName.equalsIgnoreCase("@w")){
			if(src instanceof Player){
				worldName = ((Player) src).getWorld().getName();
			}
		}
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		ConfigManager loader = new ConfigManager("worlds.conf");
		ConfigurationNode config = loader.getConfig();
		
		if(!args.hasAny("value")) {
			PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
			
			pages.title(Texts.builder().color(TextColors.DARK_PURPLE).append(Texts.of(TextColors.GOLD, world.getName().toUpperCase())).build());
			
			List<Text> list = new ArrayList<>();
			list.add(Texts.of(TextColors.DARK_PURPLE, "GameMode: ", TextColors.GOLD, config.getNode("Worlds", worldName, "Gamemode").getString()));
			list.add(Texts.of(TextColors.DARK_PURPLE, "Command: ", invalidArg()));
			
			pages.contents(list);
			
			pages.sendTo(src);
		}

		GameMode gamemode = GameModes.SURVIVAL;
		if(Main.getGame().getRegistry().getType(GameMode.class, args.<String>getOne("value").get().toUpperCase()).isPresent()){
			gamemode = Main.getGame().getRegistry().getType(GameMode.class, args.<String>getOne("value").get().toUpperCase()).get();
		}

		config.getNode("Worlds", worldName, "Gamemode").setValue(gamemode.getName().toUpperCase());
		loader.save();
		
		world.getProperties().setGameMode(gamemode);
		
		src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Set gamemode of world ", worldName, " to ", gamemode.getName().toUpperCase()));
		
		return CommandResult.success();
	}

	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.GOLD, "/world gamemode ");
		Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter world or @w for current world"))).append(Texts.of("<world> ")).build();
		Text t3 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("SURVIVAL\nCREATIVE\nADVENTURE\nSPECTATOR"))).append(Texts.of("[value]")).build();
		return Texts.of(t1,t2,t3);
	}
}
