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
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;

public class CMDDifficulty implements CommandExecutor {

	public CMDDifficulty(){
		String alias = new ConfigManager().getConfig().getNode("Options", "Command-Alias", "world").getString();
		
		Help help = new Help("difficulty", " Set the difficulty level for each world");
		help.setSyntax(" /world difficulty <world> [value]\n /" + alias + " df <world> [value]");
		help.setExample(" /world difficulty MyWorld\n /world difficulty MyWorld HARD\n /world difficulty @w PEACEFUL");
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
			
			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, properties.getWorldName())).build());
			
			List<Text> list = new ArrayList<>();
			list.add(Text.of(TextColors.AQUA, "Difficulty: ", TextColors.GREEN, properties.getDifficulty().getName().toUpperCase()));
			list.add(Text.of(TextColors.AQUA, "Command: ", invalidArg()));
			
			pages.contents(list);
			
			pages.sendTo(src);
			
			return CommandResult.success();
		}

		Difficulty difficulty = Difficulties.NORMAL;
		if(Main.getGame().getRegistry().getType(Difficulty.class, args.<String>getOne("value").get()).isPresent()){
			difficulty = Main.getGame().getRegistry().getType(Difficulty.class, args.<String>getOne("value").get()).get();
		}

		properties.setDifficulty(difficulty);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set difficulty of ", worldName, " to ", difficulty.getName().toUpperCase()));
		
		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Text.of(TextColors.GREEN, "/world difficulty ");
		Text t2 = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Enter world or @w for current world"))).append(Text.of("<world> ")).build();
		Text t3 = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("PEACEFUL\nEASY\nNORMAL\nHARD"))).append(Text.of("[value]")).build();
		return Text.of(t1,t2,t3);
	}

}
