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
import com.gmail.trentech.pjw.utils.Help;

public class CMDPvp implements CommandExecutor {

	public CMDPvp() {
		Help help = new Help("pvp", "pvp", " Toggle on and off pvp for world");
		help.setSyntax(" /world pvp <world> [value]\n /w p <world> [value]");
		help.setExample(" /world pvp MyWorld true\n /world pvp @w false\n /world pvp @a true");
		help.save();
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();
		
		if(worldName.equalsIgnoreCase("@w")) {
			if(src instanceof Player) {
				worldName = ((Player) src).getWorld().getName();
			}
		}
		
		Collection<WorldProperties> worlds = new ArrayList<>();
		
		if(worldName.equalsIgnoreCase("@a")) {
			worlds = Main.getGame().getServer().getAllWorldProperties();
		}else{
			if(!Main.getGame().getServer().getWorldProperties(worldName).isPresent()) {
				src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " does not exist"));
				return CommandResult.empty();
			}
			worlds.add(Main.getGame().getServer().getWorldProperties(worldName).get());
		}

		Builder pages = Main.getGame().getServiceManager().provide(PaginationService.class).get().builder();
		pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "PVP")).build());

		List<Text> list = new ArrayList<>();
		
		for(WorldProperties properties : worlds) {
			if(!args.hasAny("value")) {
				list.add(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.WHITE, Boolean.toString(properties.isPVPEnabled()).toUpperCase()));
				continue;
			}
			String value = args.<String>getOne("value").get();
			
			if((!value.equalsIgnoreCase("true")) && (!value.equalsIgnoreCase("false"))) {
				src.sendMessage(invalidArg());
				return CommandResult.empty();	
			}

			properties.setPVPEnabled(Boolean.getBoolean(value));
			
			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set pvp of ", worldName, " to ", TextColors.YELLOW, value.toUpperCase()));
		}

		if(!list.isEmpty()) {
			pages.contents(list);
			pages.sendTo(src);
		}

		return CommandResult.success();
	}

	private Text invalidArg() {
		Text t1 = Text.of(TextColors.YELLOW, "/world pvp ");
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Enter world or @w for current world or @a for all worlds"))).append(Text.of("<world> ")).build();
		Text t3 = Text.of(TextColors.YELLOW, "[true/false]");
		return Text.of(t1,t2,t3);
	}
}