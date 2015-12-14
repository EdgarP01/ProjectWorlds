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
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;

import com.gmail.trentech.pjw.Main;

public class CMDDifficulty implements CommandExecutor {

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

		if(!args.hasAny("value")) {
			PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
			
			pages.title(Texts.builder().color(TextColors.DARK_PURPLE).append(Texts.of(TextColors.GOLD, world.getName().toUpperCase())).build());
			
			List<Text> list = new ArrayList<>();
			list.add(Texts.of(TextColors.DARK_PURPLE, "Difficulty: ", TextColors.GOLD, world.getProperties().getDifficulty().getName().toUpperCase()));
			list.add(Texts.of(TextColors.DARK_PURPLE, "Command: ", invalidArg()));
			
			pages.contents(list);
			
			pages.sendTo(src);
			
			return CommandResult.success();
		}

		Difficulty difficulty = Difficulties.NORMAL;
		if(Main.getGame().getRegistry().getType(Difficulty.class, args.<String>getOne("value").get()).isPresent()){
			difficulty = Main.getGame().getRegistry().getType(Difficulty.class, args.<String>getOne("value").get()).get();
		}

		world.getProperties().setDifficulty(difficulty);

		src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Set difficulty of world ", worldName, " to ", difficulty.getName().toUpperCase()));
		
		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.GOLD, "/world difficulty ");
		Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter world or @w for current world"))).append(Texts.of("<world> ")).build();
		Text t3 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("PEACEFUL\nEASY\nNORMAL\nHARD"))).append(Texts.of("[value]")).build();
		return Texts.of(t1,t2,t3);
	}

}
