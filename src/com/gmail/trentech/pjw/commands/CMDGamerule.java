package com.gmail.trentech.pjw.commands;

import java.util.Map.Entry;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
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
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			TextBuilder builder = Texts.builder();
			for(Entry<String, String> gamerule : world.getGameRules().entrySet()){
				src.sendMessage(Texts.of(TextColors.DARK_PURPLE, gamerule.getKey(), ": ", TextColors.GOLD, gamerule.getValue()));
				builder.append(Texts.of(gamerule.getKey() + "\n")); 
			}
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "\nCommand: ",invalidArg()));
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			return CommandResult.empty();
		}
		String rule = args.<String>getOne("rule").get();

		if(!world.getProperties().getGameRule(rule).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Gamerule  ", rule, " does not exist"));
			return CommandResult.empty();
		}
		
		if(!args.hasAny("value")) {
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, rule, ": ", TextColors.GOLD, world.getGameRule(rule).get()));
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Command: ",invalidArg()));
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			
			return CommandResult.success();
		}
		String value = args.<String>getOne("value").get();
		
		world.getProperties().setGameRule(rule, value);

		src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Set gamerule ", rule, " to ", value));
		
		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.GOLD, "/world gamerule ");
		Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter world or @w for current world"))).append(Texts.of("<world> ")).build();
		Text t3 = Texts.of(TextColors.GOLD, "<rule> ");
		Text t4 = Texts.of(TextColors.GOLD, "[value]");
		return Texts.of(t1,t2,t3,t4);
	}

}
