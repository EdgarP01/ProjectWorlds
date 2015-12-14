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
			
			pages.title(Texts.builder().color(TextColors.DARK_PURPLE).append(Texts.of(TextColors.GOLD, world.getName().toUpperCase())).build());
			
			List<Text> list = new ArrayList<>();
			
			for(Entry<String, String> gamerule : world.getGameRules().entrySet()){
				list.add(Texts.of(TextColors.DARK_PURPLE, gamerule.getKey(), ": ", TextColors.GOLD, gamerule.getValue()));

			}

			list.add(Texts.of(TextColors.DARK_PURPLE, "Command: ", invalidArg()));
			
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
			
			pages.title(Texts.builder().color(TextColors.DARK_PURPLE).append(Texts.of(TextColors.GOLD, world.getName().toUpperCase())).build());
			
			List<Text> list = new ArrayList<>();
			list.add(Texts.of(TextColors.DARK_PURPLE, rule, ": ", TextColors.GOLD, world.getGameRule(rule).get()));
			list.add(Texts.of(TextColors.DARK_PURPLE, "Command: ", invalidArg()));
			
			pages.contents(list);
			
			pages.sendTo(src);

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
