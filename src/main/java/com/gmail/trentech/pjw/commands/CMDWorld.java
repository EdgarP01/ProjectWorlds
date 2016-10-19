package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.helpme.Help;

public class CMDWorld implements CommandExecutor {
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(Sponge.getPluginManager().getPlugin("helpme").isPresent()) {
			Help.executeList(src, Help.get("world").get().getChildren());
			
			return CommandResult.success();
		}

		List<Text> list = new ArrayList<>();

		if (src.hasPermission("pjw.cmd.world.create")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world delete")).append(Text.of(" /world create")).build());
		}
		if (src.hasPermission("pjw.cmd.world.delete")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world delete")).append(Text.of(" /world delete")).build());
		}
		if (src.hasPermission("pjw.cmd.world.rename")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world rename")).append(Text.of(" /world rename")).build());
		}
		if (src.hasPermission("pjw.cmd.world.copy")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world copy")).append(Text.of(" /world copy")).build());
		}
		if (src.hasPermission("pjw.cmd.world.import")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world import")).append(Text.of(" /world import")).build());
		}
		if (src.hasPermission("pjw.cmd.world.load")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world load")).append(Text.of(" /world load")).build());
		}
		if (src.hasPermission("pjw.cmd.world.unload")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world unload")).append(Text.of(" /world unload")).build());
		}
		if (src.hasPermission("pjw.cmd.world.enable")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world enable")).append(Text.of(" /world enable")).build());
		}
		if (src.hasPermission("pjw.cmd.world.regen")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world regen")).append(Text.of(" /world regen")).build());
		}
		if (src.hasPermission("pjw.cmd.world.fill")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world fill")).append(Text.of(" /world fill")).build());
		}
		if (src.hasPermission("pjw.cmd.world.gamemode")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world gamemode")).append(Text.of(" /world gamemode")).build());
		}
		if (src.hasPermission("pjw.cmd.world.difficulty")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world difficulty")).append(Text.of(" /world difficulty")).build());
		}
		if (src.hasPermission("pjw.cmd.world.setspawn")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world setspawn")).append(Text.of(" /world setspawn")).build());
		}
		if (src.hasPermission("pjw.cmd.world.pvp")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world pvp")).append(Text.of(" /world pvp")).build());
		}
		if (src.hasPermission("pjw.cmd.world.keepspawnloaded")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world keepspawnloaded")).append(Text.of(" /world keepspawnloaded")).build());
		}
		if (src.hasPermission("pjw.cmd.world.hardcore")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world hardcore")).append(Text.of(" /world hardcore")).build());
		}
		if (src.hasPermission("pjw.cmd.world.list")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world list")).append(Text.of(" /world list")).build());
		}
		if (src.hasPermission("pjw.cmd.world.teleport")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world teleport")).append(Text.of(" /world teleport")).build());
		}
		if (src.hasPermission("pjw.cmd.gamerule")) {
			list.add(Text.builder().color(TextColors.GREEN).onClick(TextActions.runCommand("/pjw:world gamerule")).append(Text.of(" /world gamerule")).build());
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = Sponge.getServiceManager().provide(PaginationService.class).get().builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Command List")).build());

			pages.contents(list);

			pages.sendTo(src);
		} else {
			for (Text text : list) {
				src.sendMessage(text);
			}
		}
		return CommandResult.success();
	}

}
