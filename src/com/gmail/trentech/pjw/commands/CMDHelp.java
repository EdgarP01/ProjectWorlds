package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjw.Main;

public class CMDHelp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("command")) {
			Text t1 = Text.of(TextColors.YELLOW, "/world help ");
			Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter the command you need help with"))).append(Text.of("<command> ")).build();
			src.sendMessage(Text.of(t1,t2));
			return CommandResult.empty();
		}
		String command = args.<String>getOne("command").get().toUpperCase();
		String description = null;
		String syntax = null;
		String example = null;
		
		switch(command.toLowerCase()){
			case "copy":
				description = " Allows you to make a new world from an existing world";
				syntax = " /world copy <world> <world>\n"
						+ " /w cp <world> <world>";
				example = " /world copy srcWorld newWorld\n"
						+ " /world copy @w newWorld";
				break;
			case "create":
				description = " Allows you to create new worlds with any combination of optional arguments D: for dimension type, G: for generator type, S: for seed and M: for generator modifiers";
				syntax = " /world create <world> [d:type] [g:generator] [m:modifer]  [s:seed]\n"
						+ " /w cr <world> [d:type] [g:generator] [m:modifer]  [s:seed]";
				example = " /world create NewWorld d:-12309830198412353456\n"
						+ " /world create NewWorld d:OVERWORLD g:OVERWORLD\n"
						+ " /world create NewWorld d:NETHER m:SKY\n"
						+ " /world create m:VOID";
				break;
			case "delete":
				description = " Delete worlds you no longer need. Worlds must be unloaded before you can delete them";
				syntax = " /world delete <world>\n"
						+ " /w dl <world>";
				example = " /world delete OldWorld";
				break;
			case "difficulty":
				description = " Set the difficulty level for each world";
				syntax = " /world difficulty <world> [value]\n"
						+ " /w df <world> [value]";
				example = " /world difficulty MyWorld\n"
						+ " /world difficulty MyWorld HARD\n"
						+ " /world difficulty @w PEACEFUL";
				break;
			case "hardcore":
				description = " Toggle on and off hardcore mode for world";
				syntax = " /world hardcore <world> [value]\n"
						+ " /w h <world> [value]";
				example = " /world hardcore MyWorld\n"
						+ " /world hardcore MyWorld false\n"
						+ " /world hardcore @w true";
				break;
			case "keepspawnloaded":
				description = " Keeps spawn point of world loaded in memory";
				syntax = " /world keepspawnloaded <world> [value]\n"
						+ " /w k <world> [value]";
				example = " /world keepspawnloaded MyWorld\n"
						+ " /world keepspawnloaded MyWorld true\n"
						+ " /world keepspawnloaded @w false";
				break;
			case "list":
				description = " Lists all known worlds, loaded or unloaded";
				syntax = " /world list\n"
						+ " /w ls";
				example = " /world list";
				break;
			case "load":
				description = " Loads specified world if exists";
				syntax = " /world load <world>\n"
						+ " /w l <world>";
				example = " /world load NewWorld";
				break;
			case "gamerule":
				description = " Configure varies world properties";
				syntax = " /gamerule <world> [rule] [value]\n"
						+ " /gr <world> [rule] [value]";
				example = " /gamerule MyWorld\n"
						+ " /gamerule MyWorld mobGriefing false\n"
						+ " /gamerule @w doDaylightCycle true";
				break;
			case "properties":
				description = " View all properties associated with a world";
				syntax = " /world properties <world>\n"
						+ " /w p <world>";
				example = " /world properties\n"
						+ " /world properties MyWorld";
				break;
			case "gamemode":
				description = " Change gamemode of the specified world";
				syntax = " /world gamemode <world> <gamemode>\n"
						+ " /w g <world> <gamemode>";
				example = " /world gamemode\n"
						+ " /world gamemode MyWorld SURVIVAL\n"
						+ " /world gamemode @w CREATIVE";
				break;
			case "pvp":
				description = " Toggle on and off pvp for world";
				syntax = " /world pvp <world> [value]";
				example = " /world pvp MyWorld true\n"
						+ " /world pvp @w false";
				break;
			case "rename":
				description = " Allows for renaming worlds. World must be unloaded before you can rename world";
				syntax = " /world rename <world> <world>\n"
						+ " /w rn <world> <world>";
				example = " /world rename MyWorld NewWorldName";
				break;
			case "setspawn":
				description = " Sets the spawn point of specified world. If no arguments present sets spawn of current world to player location";
				syntax = " /world setspawn <world> <x,y,z>\n"
						+ " /w s <world> <x,y,z>";
				example = " /world setspawn\n"
						+ " /world setspawn MyWorld -153,75,300";
				break;
			case "teleport":
				description = " Teleport self or others to specified world and location";
				syntax = " /world teleport <world> <world:[x,y,z]>\n"
						+ " /w tp <world> <world:[x,y,z]>";
				example = " /world teleport MyWorld\n"
						+ " /world teleport MyWorld:-153,75,300\n"
						+ " /world teleport SomePlayer MyWorld\n"
						+ " /world teleport SomePlayer MyWorld:-153,75,300";
				break;
			case "unload":
				description = " Unloads specified world. If players are in world, they will be teleported to default spawn";
				syntax = " /world unload <world>\n"
						+ " /w u <world>";
				example = " /world unload MyWorld";
				break;
			case "regen":
				description = " Regenerates a world. You can preserve the seed or generate new random";
				syntax = " /world regen <world> [true/false]\n"
						+ " /w r <world>  [true/false]";
				example = " /world regen MyWorld\n"
						+ " /world regen MyWorld true";
				break;
			case "enable":
				description = " Enable and disable worlds from loading";
				syntax = " /world enable <world> <value>\n"
						+ " /w e <world> <value>";
				example = " /world enable MyWorld true\n"
						+ " /world enable MyWorld false";
				break;
			default:
				src.sendMessage(Text.of(TextColors.DARK_RED, "Not a valid command"));
				return CommandResult.empty();
		}
		
		help(command, description, syntax, example).sendTo(src);
		return CommandResult.success();
	}
	
	private PaginationBuilder help(String command, String description, String syntax, String example){
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.AQUA, command)).build());
		
		List<Text> list = new ArrayList<>();

		list.add(Text.of(TextColors.AQUA, "Description:"));
		list.add(Text.of(TextColors.GREEN, description));
		list.add(Text.of(TextColors.AQUA, "Syntax:"));
		list.add(Text.of(TextColors.GREEN, syntax));
		list.add(Text.of(TextColors.AQUA, "Example:"));
		list.add(Text.of(TextColors.GREEN,  example, TextColors.DARK_GREEN));
		
		pages.contents(list);
		
		return pages;
	}
}
