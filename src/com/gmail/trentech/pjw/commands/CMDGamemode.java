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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;

public class CMDGamemode implements CommandExecutor {

	public CMDGamemode(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "world").getString();
		
		Help help = new Help("gamemode", " Change gamemode of the specified world");
		help.setSyntax(" /world gamemode <world> <gamemode>\n /" + alias + " g <world> <gamemode>");
		help.setExample(" /world gamemode\n /world gamemode MyWorld SURVIVAL\n /world gamemode @w CREATIVE");
		CMDHelp.getList().add(help);
	}
	
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
		
		if(!Main.getGame().getServer().getWorldProperties(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
			return CommandResult.empty();
		}
		WorldProperties properties = Main.getGame().getServer().getWorldProperties(worldName).get();

		if(!args.hasAny("value")) {
			PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
			
			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, properties.getWorldName().toUpperCase())).build());
			
			List<Text> list = new ArrayList<>();
			list.add(Text.of(TextColors.AQUA, "GameMode: ", TextColors.GREEN, properties.getGameMode().getName().toUpperCase()));
			list.add(Text.of(TextColors.AQUA, "Command: ", invalidArg()));
			
			pages.contents(list);
			
			pages.sendTo(src);
			
			return CommandResult.empty();
		}

		GameMode gamemode = GameModes.CREATIVE;
		if(Main.getGame().getRegistry().getType(GameMode.class, args.<String>getOne("value").get().toUpperCase()).isPresent()){
			gamemode = Main.getGame().getRegistry().getType(GameMode.class, args.<String>getOne("value").get().toUpperCase()).get();
		}

		properties.setGameMode(gamemode);
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set gamemode of world ", worldName, " to ", gamemode.getName().toUpperCase()));
		
		return CommandResult.success();
	}

	private Text invalidArg(){
		Text t1 = Text.of(TextColors.GREEN, "/world gamemode ");
		Text t2 = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Enter world or @w for current world"))).append(Text.of("<world> ")).build();
		org.spongepowered.api.text.Text.Builder gamemodes = null;
		for(GameMode gamemode : Main.getGame().getRegistry().getAllOf(GameMode.class)){
			if(gamemodes == null){
				gamemodes = Text.builder().append(Text.of(gamemode.getName()));
			}else{
				gamemodes.append(Text.of("\n", gamemode.getName()));
			}
		}
		Text t3 = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(gamemodes.build())).append(Text.of("[value]")).build();
		return Text.of(t1,t2,t3);
	}
}