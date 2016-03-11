package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;

public class CMDList implements CommandExecutor {

	public CMDList(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "world").getString();
		
		Help help = new Help("list", " Lists all known worlds, loaded or unloaded");
		help.setSyntax(" /world list\n /" + alias + " ls");
		CMDHelp.getList().add(help);
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		org.spongepowered.api.service.pagination.PaginationList.Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Worlds")).build());
		
		List<Text> list = new ArrayList<>();
		
		for(World world : Main.getGame().getServer().getWorlds()){
			Builder builder = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of(TextColors.WHITE, "Click to view properies")));
			builder.onClick(TextActions.runCommand("/pjw:world properties " + world.getName())).append(Text.of(TextColors.GREEN, world.getName(), ": ", TextColors.GREEN, world.getEntities().size(), " Entities"));
			list.add(builder.build());
		}
		
		for(WorldProperties world : Main.getGame().getServer().getUnloadedWorlds()){
			Builder builder = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of(TextColors.WHITE, "Click to load world")));
			builder.onClick(TextActions.runCommand("/pjw:world load " + world.getWorldName())).append(Text.of(TextColors.GREEN, world.getWorldName(), ": ", TextColors.GRAY, " Unloaded"));
			list.add(builder.build());
		}

		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
