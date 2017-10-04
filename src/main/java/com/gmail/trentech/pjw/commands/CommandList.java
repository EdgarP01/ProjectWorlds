package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.google.common.collect.Lists;

public class CommandList implements CommandCallable {
	
	private final Help help = Help.get("world list").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("list")) {
			throw new CommandException(getHelp().getUsageText());
		}

		if(arguments.equalsIgnoreCase("--help")) {
			help.execute(source);
			return CommandResult.success();
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

		if (source instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Worlds")).build());

			pages.contents(list);

			pages.sendTo(source);
		} else {
			for (Text text : list) {
				source.sendMessage(text);
			}
		}

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		return new ArrayList<>();
	}

	@Override
	public boolean testPermission(CommandSource source) {
		Optional<String> permission = getHelp().getPermission();
		
		if(permission.isPresent()) {
			return source.hasPermission(permission.get());
		}
		return true;
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return Optional.of(Text.of(getHelp().getDescription()));
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return Optional.of(Text.of(getHelp().getDescription()));
	}

	@Override
	public Text getUsage(CommandSource source) {
		return getHelp().getUsageText();
	}

	public Help getHelp() {
		return help;
	}
}
