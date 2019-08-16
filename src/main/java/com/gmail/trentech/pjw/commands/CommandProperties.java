package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.utils.Utils;

public class CommandProperties implements CommandCallable {
	
	private final Help help = Help.get("world properties").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("properties")) {
			throw new CommandException(getHelp().getUsageText());
		}

		if(arguments.contains("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
			
		Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(arguments);
		
		if(!optionalProperties.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, arguments, " does not exist"), false);
		}
		WorldProperties properties = optionalProperties.get();
		
		List<Text> list = new ArrayList<>();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getUniqueId());
		
		if(optionalWorld.isPresent()) {
			list.add(Text.of(TextColors.GREEN, "Name: ", TextColors.WHITE, properties.getWorldName(), TextColors.GREEN, ", DimId: ", TextColors.WHITE, (int) properties.getAdditionalProperties().getView(DataQuery.of("SpongeData")).get().get(DataQuery.of("dimensionId")).get(), TextColors.GREEN, ", Loaded: ", TextColors.WHITE, "true", TextColors.GREEN, ", Enabled: ", TextColors.WHITE, properties.isEnabled()));
		} else {
			list.add(Text.of(TextColors.GREEN, "Name: ", TextColors.WHITE, properties.getWorldName(), TextColors.GREEN, ", Loaded: ", TextColors.WHITE, "false", TextColors.GREEN, ", Enabled: ", TextColors.WHITE, properties.isEnabled()));
		}
		
		list.add(Text.of(TextColors.GREEN, "Generator: ", TextColors.WHITE, properties.getGeneratorType().getName().toLowerCase(), TextColors.GREEN, ", Dimension: ", TextColors.WHITE, properties.getDimensionType().getName().toLowerCase()));

		Collection<WorldGeneratorModifier> modifiers = properties.getGeneratorModifiers();
		
		if(!modifiers.isEmpty()) {
			list.add(Text.of(TextColors.GREEN, "Generator Modifiers: "));
		}
		for(WorldGeneratorModifier modifier : modifiers) {
			list.add(Text.of(TextColors.GREEN, "  - ", TextColors.WHITE, modifier.getId()));
		}

		list.add(Text.of(TextColors.GREEN, "GameMode: ", TextColors.WHITE, properties.getGameMode().getTranslation().get().toLowerCase(), TextColors.GREEN, ", Difficulty: ", TextColors.WHITE, properties.getDifficulty().getTranslation().get().toLowerCase(), TextColors.GREEN, ", PVP: ", TextColors.WHITE, properties.isPVPEnabled()));
		list.add(Text.of(TextColors.GREEN, "Keep Spawn Loaded: ", TextColors.WHITE, properties.doesKeepSpawnLoaded(), TextColors.GREEN, ", Load on Startup: ", TextColors.WHITE, properties.loadOnStartup()));
		list.add(Text.of(TextColors.GREEN, "Bonus Chest: ", TextColors.WHITE, properties.doesGenerateBonusChest(), TextColors.GREEN, " Map Features: ", TextColors.WHITE, properties.usesMapFeatures()));
		list.add(Text.of(TextColors.GREEN, "Commands Allowed: ", TextColors.WHITE, properties.areCommandsAllowed()));
		
		if(optionalWorld.isPresent()) {
			list.add(Text.of(TextColors.GREEN, "Time: ", TextColors.WHITE, Utils.getTime(properties.getWorldTime()), TextColors.GREEN, ", Ticks: ", TextColors.WHITE, (properties.getWorldTime() % 24000), TextColors.GREEN, ", Weather: ", TextColors.WHITE, optionalWorld.get().getWeather().getName()));
		} else {
			list.add(Text.of(TextColors.GREEN, "Time: ", TextColors.WHITE, Utils.getTime(properties.getWorldTime()), TextColors.GREEN, ", Ticks: ", TextColors.WHITE, (properties.getWorldTime() % 24000)));
		}
		
		list.add(Text.of(TextColors.GREEN, "UUID: ", TextColors.WHITE, properties.getUniqueId().toString()));
		list.add(Text.of(TextColors.GREEN, "Seed: ", TextColors.WHITE, properties.getSeed()));	

		if (source instanceof Player) {
			PaginationList.Builder pages = PaginationList.builder();

			pages.title(Text.builder().color(TextColors.DARK_GREEN).append(Text.of(TextColors.GREEN, "Settings")).build());

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
		List<String> list = new ArrayList<>();

		if(arguments.equalsIgnoreCase("")) {
			for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
				list.add(world.getWorldName());
			}
			
			return list;
		}
		
		List<String> args = Arrays.asList(arguments.split(" "));

		if(args.size() == 1) {
			if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
				for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
					if(world.getWorldName().toLowerCase().equalsIgnoreCase(args.get(0).toLowerCase())) {
						list.add(world.getWorldName());
					}
					
					if(world.getWorldName().toLowerCase().startsWith(args.get(0).toLowerCase())) {
						list.add(world.getWorldName());
					}
				}
			}
			
			return list;
		}

		return list;
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
