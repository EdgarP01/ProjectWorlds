package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
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
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
		src.sendMessage(Texts.of(TextColors.GOLD, "                           Worlds"));
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
		
		for(World world : Main.getGame().getServer().getWorlds()){
			TextBuilder builder = Texts.builder().color(TextColors.DARK_PURPLE).onHover(TextActions.showText(Texts.of(TextColors.WHITE, "Click to view properies")));
			builder.onClick(TextActions.runCommand("/world properties " + world.getName())).append(Texts.of(TextColors.DARK_PURPLE, world.getName(), ": ", TextColors.GOLD, world.getEntities().size(), " Entities"));
			src.sendMessage(builder.build());
		}
		
		for(WorldProperties world : Main.getGame().getServer().getUnloadedWorlds()){
			TextBuilder builder = Texts.builder().color(TextColors.DARK_GRAY).onHover(TextActions.showText(Texts.of(TextColors.WHITE, "Click to load world")));
			builder.onClick(TextActions.runCommand("/world load " + world.getWorldName())).append(Texts.of(TextColors.DARK_PURPLE, world.getWorldName(), ": ", TextColors.GRAY, " Unloaded"));
			src.sendMessage(builder.build());
		}
		
		src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
		
		return CommandResult.success();
	}

}
