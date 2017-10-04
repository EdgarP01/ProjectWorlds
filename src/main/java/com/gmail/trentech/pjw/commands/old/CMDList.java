package com.gmail.trentech.pjw.commands.old;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.google.common.collect.Lists;

public class CMDList implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("world list").get();
		
		if (args.hasAny("help")) {		
			help.execute(src);
			return CommandResult.empty();
		}
		
		List<Text> list = new ArrayList<>();

		for (WorldProperties properties : Sponge.getServer().getAllWorldProperties()) {
			Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getUniqueId());
			
			if(optionalWorld.isPresent()) {
				World world = optionalWorld.get();
				Builder builder = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of(TextColors.WHITE, "Click to view properies")));
				builder.onClick(TextActions.runCommand("/pjw:world properties " + world.getName())).append(Text.of(TextColors.GREEN, world.getName(), ": ", Lists.newArrayList(world.getLoadedChunks()).size(), " Loaded chunks, ", world.getEntities().size(), " Entities"));
				list.add(builder.build());
			} else {
				Builder builder = Text.builder().color(TextColors.GREEN).onHover(TextActions.showText(Text.of(TextColors.WHITE, "Click to load world")));
				builder.onClick(TextActions.runCommand("/pjw:world load " + properties.getWorldName())).append(Text.of(TextColors.GREEN, properties.getWorldName(), ": ", TextColors.GRAY, " Unloaded"));
				list.add(builder.build());
			}
		}

		if (src instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Worlds")).build());

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
