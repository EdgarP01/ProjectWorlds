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

public class CMDWorld implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		PaginationBuilder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		
		pages.title(Texts.builder().color(TextColors.DARK_PURPLE).append(Texts.of(TextColors.GOLD, "Command List")).build());
		
		List<Text> list = new ArrayList<>();
		
		if(src.hasPermission("pjw.cmd.world.create")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help create")).append(Texts.of("/world create")).build());
		}
		if(src.hasPermission("pjw.cmd.world.delete")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help delete")).append(Texts.of("/world delete")).build());
		}
		if(src.hasPermission("pjw.cmd.world.rename")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help rename")).append(Texts.of("/world rename")).build());
		}
		if(src.hasPermission("pjw.cmd.world.unload")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help unload")).append(Texts.of("/world unload")).build());
		}
		if(src.hasPermission("pjw.cmd.world.copy")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help copy")).append(Texts.of("/world copy")).build());
		}
		if(src.hasPermission("pjw.cmd.world.load")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help load")).append(Texts.of("/world load")).build());
		}
		if(src.hasPermission("pjw.cmd.world.difficulty")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help difficulty")).append(Texts.of("/world difficulty")).build());
		}
		if(src.hasPermission("pjw.cmd.world.gamemode")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help gamemode")).append(Texts.of("/world gamemode")).build());
		}
		if(src.hasPermission("pjw.cmd.world.setspawn")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help setspawn")).append(Texts.of("/world setspawn")).build());
		}
		if(src.hasPermission("pjw.cmd.world.pvp")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help pvp")).append(Texts.of("/world pvp")).build());
		}
		if(src.hasPermission("pjw.cmd.world.respawn")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help respawn")).append(Texts.of("/world respawn")).build());
		}
		if(src.hasPermission("pjw.cmd.world.keepspawnloaded")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help keepspawnloaded")).append(Texts.of("/world keepspawnloaded")).build());
		}
		if(src.hasPermission("pjw.cmd.world.hardcore")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help hardcore")).append(Texts.of("/world hardcore")).build());
		}
		if(src.hasPermission("pjw.cmd.world.locktime")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help locktime")).append(Texts.of("/world locktime")).build());
		}
		if(src.hasPermission("pjw.cmd.world.lockweather")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help lockweather")).append(Texts.of("/world lockweather")).build());
		}
		if(src.hasPermission("pjw.cmd.world.list")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help list")).append(Texts.of("/world list")).build());
		}
		if(src.hasPermission("pjw.cmd.world.portal")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help portal")).append(Texts.of("/world portal")).build());
		}
		if(src.hasPermission("pjw.cmd.world.plate")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help plate")).append(Texts.of("/world plate")).build());
		}
		if(src.hasPermission("pjw.cmd.world.button")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help button")).append(Texts.of("/world button")).build());
		}
		if(src.hasPermission("pjw.cmd.world.teleport")) {
			list.add(Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Click command for more information ")))
					.onClick(TextActions.runCommand("/world help teleport")).append(Texts.of("/world teleport")).build());
		}
		pages.contents(list);
		
		pages.sendTo(src);

		return CommandResult.success();
	}

}
