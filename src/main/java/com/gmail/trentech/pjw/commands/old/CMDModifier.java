package com.gmail.trentech.pjw.commands.old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;

public class CMDModifier implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("world modifier").get();
		
		if (args.hasAny("help")) {		
			help.execute(src);
			return CommandResult.empty();
		}
		
		if (!args.hasAny("world")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		WorldProperties properties = args.<WorldProperties> getOne("world").get();
		
		Collection<WorldGeneratorModifier> modifiers = properties.getGeneratorModifiers();
		
		if (!args.hasAny("modifier")) {
			List<Text> list = new ArrayList<>();

			for (WorldGeneratorModifier modifier : modifiers) {
				list.add(Text.of(TextColors.WHITE, modifier.getId()));
			}

			if (src instanceof Player) {
				PaginationList.Builder pages = PaginationList.builder();

				pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Current Modifiers")).build());

				pages.contents(list);

				pages.sendTo(src);
			} else {
				for (Text text : list) {
					src.sendMessage(text);
				}
			}
			
			return CommandResult.success();
		}
		
		WorldGeneratorModifier modifier = args.<WorldGeneratorModifier> getOne("world").get();
		
		if(args.hasAny("r")) {
			modifiers.remove(modifier);
			
			properties.setGeneratorModifiers(modifiers);

			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Removed modifier ", modifier.getId(), " to ", properties.getWorldName()));
		} else {
			modifiers.add(modifier);
			
			properties.setGeneratorModifiers(modifiers);

			src.sendMessage(Text.of(TextColors.DARK_GREEN, "Added modifier ", modifier.getId(), " to ", properties.getWorldName()));
		}

		return CommandResult.success();
	}
}
