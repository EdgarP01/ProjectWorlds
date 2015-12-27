package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

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
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;

public class CMDGamerule implements CommandExecutor {

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
		
		if(!args.hasAny("rule")) {
			PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
			
			pages.title(Texts.builder().color(TextColors.DARK_GREEN).append(Texts.of(TextColors.AQUA, world.getName().toUpperCase())).build());
			
			List<Text> list = new ArrayList<>();
			
			for(Entry<String, String> gamerule : world.getGameRules().entrySet()){
				list.add(Texts.of(TextColors.AQUA, gamerule.getKey(), ": ", TextColors.GREEN, gamerule.getValue()));
			}

			list.add(Texts.of(TextColors.AQUA, "Command: ", invalidArg()));
			
			pages.contents(list);
			
			pages.sendTo(src);

			return CommandResult.empty();
		}
		String rule = args.<String>getOne("rule").get();

		if(!world.getProperties().getGameRule(rule).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Gamerule  ", rule, " does not exist"));
			return CommandResult.empty();
		}
		
		if(!args.hasAny("value")) {
			PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
			
			pages.title(Texts.builder().color(TextColors.DARK_GREEN).append(Texts.of(TextColors.AQUA, world.getName())).build());
			
			List<Text> list = new ArrayList<>();
			list.add(Texts.of(TextColors.AQUA, rule, ": ", TextColors.GREEN, world.getGameRule(rule).get()));
			list.add(Texts.of(TextColors.AQUA, "Command: ", invalidArg()));
			
			pages.contents(list);
			
			pages.sendTo(src);

			return CommandResult.success();
		}
		String value = args.<String>getOne("value").get();
		
		if(!isValid(rule, value)){
			src.sendMessage(Texts.of(TextColors.DARK_RED, value, " is not a valid value for gamerule ", rule));
			return CommandResult.empty();
		}
		
		world.getProperties().setGameRule(rule, value.toLowerCase());

		src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Set gamerule ", rule, " to ", value));
		
		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.GREEN, "/gamerule ");
		Text t2 = Texts.builder().color(TextColors.GREEN).onHover(TextActions.showText(Texts.of("Enter world or @w for current world"))).append(Texts.of("<world> ")).build();
		Text t3 = Texts.of(TextColors.GREEN, "<rule> ");
		Text t4 = Texts.of(TextColors.GREEN, "[value]");
		return Texts.of(t1,t2,t3,t4);
	}
	
	private boolean isValid(String rule, String value){
		switch(rule){
		case "commandBlockOutput":
			return validBool(value);
		case "defaultWeather":
			if(value.equalsIgnoreCase("CLEAR") || value.equalsIgnoreCase("RAIN") || value.equalsIgnoreCase("THUNDER_STORM") || value.equalsIgnoreCase("NORMAL")){
				return true;
			}
			return false;
		case "doDaylightCycle":
			return validBool(value);
		case "doFireTick":
			return validBool(value);
		case "doMobLoot":
			return validBool(value);
		case "doMobSpawning":
			return validBool(value);
		case "doTileDrops":
			return validBool(value);
		case "gamemode":
			if(value.equalsIgnoreCase("SURVIVAL") || value.equalsIgnoreCase("CREATIVE") || value.equalsIgnoreCase("ADVENTURE") || value.equalsIgnoreCase("SPECTATOR")){
				return true;
			}
			return false;
		case "keepInventory":
			return validBool(value);
		case "logAdminCommands":
			return validBool(value);
		case "mobGriefing":
			return validBool(value);
		case "naturalRegeneration":
			return validBool(value);
		case "pvp":
			return validBool(value);
		case "randomTick":
			try{
				Long.parseLong(value);
				return true;
			}catch(Exception e){
				return false;
			}
		case "reducedDebugInfo":
			return validBool(value);
		case "respawnWorld":
			if(Main.getGame().getServer().getWorld(value).isPresent()){
				return true;
			}
			return false;
		case "sendCommandFeedback":
			return validBool(value);
		case "showDeathMessages":
			return validBool(value);
		default: return false;
		}
	}
	
	private boolean validBool(String value){
		if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")){
			return true;
		}
		return false;
	}

}
