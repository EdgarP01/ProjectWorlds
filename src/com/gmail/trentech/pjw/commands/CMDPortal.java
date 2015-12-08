package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.portal.PortalBuilder;

public class CMDPortal implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		if(!args.hasAny("name")) {
			player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Right click the portal to remove"));
			Main.getActiveBuilders().put(player, new PortalBuilder());
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		PortalBuilder builder = new PortalBuilder(worldName);

		Main.getActiveBuilders().put(player, builder);

		player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Right click starting point"));

		return CommandResult.success();
	}
}
