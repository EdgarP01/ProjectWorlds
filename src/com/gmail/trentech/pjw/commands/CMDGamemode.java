package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Gamemode;
import com.gmail.trentech.pjw.utils.Help;

public class CMDGamemode implements CommandExecutor {

	public CMDGamemode(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "world").getString();
		
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
			
			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, properties.getWorldName())).build());
			
			List<Text> list = new ArrayList<>();
			list.add(Text.of(TextColors.GREEN, "GameMode: ", TextColors.WHITE, properties.getGameMode().getName().toUpperCase()));
			list.add(Text.of(TextColors.GREEN, "Command: ", invalidArg()));
			
			pages.contents(list);
			
			pages.sendTo(src);
			
			return CommandResult.empty();
		}
		String value = args.<String>getOne("value").get().toUpperCase();

		Optional<GameMode> optionalGamemode = Optional.empty();
		try{
			int index = Integer.parseInt(value);
			optionalGamemode = Gamemode.get(index);
		}catch(Exception e){
			optionalGamemode = Gamemode.get(value);
		}

		if(!optionalGamemode.isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid GameMode Type"));
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		GameMode gamemode = optionalGamemode.get();

		properties.setGameMode(gamemode);
		
		Main.getGame().getServer().saveWorldProperties(properties);
		
		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set gamemode of ", worldName, " to ", gamemode.getName().toUpperCase()));
		
		return CommandResult.success();
	}

	private Text invalidArg(){
		Text t1 = Text.of(TextColors.YELLOW, "/world gamemode ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world or @w for current world"))).append(Text.of("<world> ")).build();
		
		Text.Builder builder = null;
		
    	Gamemode[] gamemodes = Gamemode.values();
    	
        for (Gamemode gamemode : gamemodes){
			if(builder == null){
				builder = Text.builder().append(Text.of(gamemode.getIndex(), ": ", gamemode.getGameMode().getName()));
			}else{
				builder.append(Text.of("\n", gamemode.getIndex(), ": ", gamemode.getGameMode().getName()));
			}
        }

		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(builder.build())).append(Text.of("[value]")).build();
		
		return Text.of(t1,t2,t3);
	}
}