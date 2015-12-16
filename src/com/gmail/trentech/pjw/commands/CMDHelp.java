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
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjw.Main;

public class CMDHelp implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("command")) {
			Text t1 = Texts.of(TextColors.GOLD, "/world help ");
			Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter the command you need help with"))).append(Texts.of("<command> ")).build();
			src.sendMessage(Texts.of(t1,t2));
			return CommandResult.empty();
		}
		String command = args.<String>getOne("command").get().toUpperCase();
		String description = null;
		String example = null;
		
		switch(command.toLowerCase()){
			case "button":
				description = " Use this command to create a button that will teleport you to other worlds";
				example = " /world button MyWorld";
				break;
			case "copy":
				description = " Allows you to make a new world from an existing world.";
				example = " /world copy srcWorld newWorld\n"
						+ " /world copy @w newWorld";
				break;
			case "create":
				description = " Allows you to create new worlds with any combination of optional arguments D: for dimension type, G: for generator type, S: for seed and M: for generator modifiers.";
				example = " /world create NewWorld S:-12309830198412353456\n"
						+ " /world create NewWorld D:OVERWORLD G:OVERWORLD\n"
						+ " /world create NewWorld D:NETHER M:SKY\n"
						+ " /world create M:VOID";
				break;
			case "delete":
				description = " Delete worlds you no longer need. Worlds must be unloaded before you can delete them.";
				example = " /world delete OldWorld";
				break;
			case "difficulty":
				description = " Set the difficulty level for each world.";
				example = " /world difficulty MyWorld HARD\n"
						+ " /world difficulty @w PEACEFUL";
				break;
			case "hardcore":
				description = " Toggle on and off hardcore mode for world";
				example = " /world hardcore MyWorld false\n"
						+ " /world hardcore @w true";
				break;
			case "keepspawnloaded":
				description = " Keeps spawn point of world loaded in memory";
				example = " /world keepspawnloaded MyWorld true\n"
						+ " /world keepspawnloaded @w false";
				break;
			case "list":
				description = " Lists all known worlds, loaded or unloaded";
				example = " /world list";
				break;
			case "load":
				description = " Loads specified world if exists.";
				example = " /world load NewWorld";
				break;
			case "plate":
				description = " Use this command to create a pressire playe that will teleport you to other worlds";
				example = " /world plate MyWorld";
				break;
			case "portal":
				description = " Create portal to another dimension. No arguments allow for deleting portals";
				example = " /world portal MyWorld\n"
						+ " /world portal MyWorld 10,60,45 15,50,45\n"
						+ " /world portal MyWorld 150,66,30 15,75,65 ThisWorld";
				break;
			case "gamerule":
				description = " Configure varies world properties";
				example = " /gamerule MyWorld mobGriefing false\n"
						+ " /gamerule @w pvp true";
				break;
			case "properties":
				description = " View all properties associated with a world";
				example = " /world portal\n"
						+ " /world portal MyWorld";
				break;
			case "pvp":
				description = " Toggle on and off pvp for world";
				example = " /world pvp MyWorld true\n"
						+ " /world pvp @w false";
				break;
			case "rename":
				description = " Allows for renaming worlds. World must be unloaded before you can rename world";
				example = " /world rename MyWorld NewWorldName";
				break;
			case "setspawn":
				description = " Sets the spawn point of specified world. If no arguments present sets spawn of current world to player location.";
				example = " /world setspawn\n"
						+ " /world setspawn MyWorld -153,75,300";
				break;
			case "teleport":
				description = " Teleport self or others to specified world and location";
				example = " /world teleport MyWorld\n"
						+ " /world teleport MyWorld:-153,75,300\n"
						+ " /world teleport SomePlayer MyWorld\n"
						+ " /world teleport SomePlayer MyWorld:-153,75,300";
				break;
			case "unload":
				description = " unloads specified world. If players are in world, they will be teleported to default spawn.";
				example = " /world unload MyWorld";
				break;
			default:
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Not a valid command"));
				return CommandResult.empty();
		}
		
		help(command, description, example).sendTo(src);
		return CommandResult.success();
	}
	
	private PaginationBuilder help(String command, String description, String example){
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		pages.title(Texts.builder().color(TextColors.DARK_PURPLE).append(Texts.of(TextColors.GOLD, command)).build());
		
		List<Text> list = new ArrayList<>();

		list.add(Texts.of(TextColors.DARK_PURPLE, "Description:"));
		list.add(Texts.of(TextColors.GOLD, description));
		list.add(Texts.of(TextColors.DARK_PURPLE, "Example:"));
		list.add(Texts.of(TextColors.GOLD,  example, TextColors.DARK_PURPLE));
		
		pages.contents(list);
		
		return pages;
	}
}
