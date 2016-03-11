package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList.Builder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;

public class CMDKeepSpawnLoaded implements CommandExecutor {

	public CMDKeepSpawnLoaded(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "world").getString();
		
		Help help = new Help("keepspawnloaded", " Keeps spawn point of world loaded in memory");
		help.setSyntax(" /world keepspawnloaded <world> [value]\n /" + alias + " k <world> [value]");
		help.setExample(" /world keepspawnloaded MyWorld\n /world keepspawnloaded MyWorld true\n /world keepspawnloaded @w false");
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
		
		Collection<WorldProperties> worlds = new ArrayList<>();
		
		if(worldName.equalsIgnoreCase("@a")){
			worlds = Main.getGame().getServer().getAllWorldProperties();
		}else{
			if(!Main.getGame().getServer().getWorldProperties(worldName).isPresent()){
				src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
				return CommandResult.empty();
			}
			worlds.add(Main.getGame().getServer().getWorldProperties(worldName).get());
		}

		Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Hardcore")).build());

		List<Text> list = new ArrayList<>();
		
		for(WorldProperties properties : worlds){
			if(!args.hasAny("value")) {
				list.add(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.WHITE, Boolean.toString(properties.isHardcore()).toUpperCase()));
				continue;
			}
			String value = args.<String>getOne("value").get();
			
			if((!value.equalsIgnoreCase("true")) && (!value.equalsIgnoreCase("false"))){
				src.sendMessage(invalidArg());
				return CommandResult.empty();	
			}

			properties.setKeepSpawnLoaded(Boolean.getBoolean(value));
			
			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set keep spawn loaded of ", worldName, " to ", TextColors.YELLOW, value.toUpperCase()));
			src.sendMessage(Text.of(TextColors.DARK_RED, "[WARNING]", TextColors.YELLOW, " Setting this to false can cause worlds to randomly unload when no players are occupying them"));
		}

		if(!list.isEmpty()){
			pages.contents(list);
			pages.sendTo(src);
		}

		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Text.of(TextColors.GREEN, "/world keepspawnloaded ");
		Text t2 = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of("Enter world or @w for current world or @a for all worlds"))).append(Text.of("<world> ")).build();
		Text t3 = Text.of(TextColors.GREEN, "[true/false]");
		return Text.of(t1,t2,t3);
	}
}
