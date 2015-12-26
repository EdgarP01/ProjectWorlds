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
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;

public class CMDList implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Texts.builder().color(TextColors.DARK_GREEN).append(Texts.of(TextColors.AQUA, "Worlds")).build());
		
		List<Text> list = new ArrayList<>();
		
		for(World world : Main.getGame().getServer().getWorlds()){
			TextBuilder builder = Texts.builder().color(TextColors.AQUA).onHover(TextActions.showText(Texts.of(TextColors.WHITE, "Click to view properies")));
			builder.onClick(TextActions.runCommand("/world properties " + world.getName())).append(Texts.of(TextColors.AQUA, world.getName(), ": ", TextColors.GREEN, world.getEntities().size(), " Entities"));
			list.add(builder.build());
		}
		for(WorldProperties world : Main.getGame().getServer().getUnloadedWorlds()){
			TextBuilder builder = Texts.builder().color(TextColors.DARK_GRAY).onHover(TextActions.showText(Texts.of(TextColors.WHITE, "Click to load world")));
			builder.onClick(TextActions.runCommand("/world load " + world.getWorldName())).append(Texts.of(TextColors.AQUA, world.getWorldName(), ": ", TextColors.GRAY, " Unloaded"));
			list.add(builder.build());
		}

		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
