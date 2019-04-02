package com.gmail.trentech.pjw.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.io.WorldData;

public class CommandImport implements CommandCallable {
	
	private final Help help = Help.get("world import").get();

	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("import")) {
			throw new CommandException(getHelp().getUsageText());
		}

		List<String> args = Arrays.asList(arguments.split(" "));
		
		if(args.contains("--help")) {
			getHelp().execute(source);
			return CommandResult.success();
		}
		
		if(args.size() < 3) {
			throw new CommandException(getHelp().getUsageText());
		}

		if (Sponge.getServer().getWorld(args.get(0)).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, args.get(0), " is already loaded"), false);
		}
		
		WorldData worldData = new WorldData(args.get(0));

		if (!worldData.exists()) {
			throw new CommandException(Text.of(TextColors.RED, args.get(0), " is not a valid world"), false);
		}

		SpongeData spongeData = new SpongeData(args.get(0));

		if (spongeData.exists()) {
			source.sendMessage(Text.of(TextColors.RED, "Sponge world detected"));
			source.sendMessage(Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.runCommand("/pjw:world load")).append(Text.of(" /world load")).build());
			return CommandResult.success();
		}

		Optional<DimensionType> optionalDimensionType = Sponge.getRegistry().getType(DimensionType.class, args.get(1));
		
		if(!optionalDimensionType.isPresent()) {
			source.sendMessage(Text.of(TextColors.YELLOW, args.get(1), " is not a valid DimensionType"));
			throw new CommandException(getHelp().getUsageText());
		}
		DimensionType dimensionType = optionalDimensionType.get();

		Optional<GeneratorType> optionalGeneratorType = Sponge.getRegistry().getType(GeneratorType.class, args.get(2));
		
		if(!optionalGeneratorType.isPresent()) {
			source.sendMessage(Text.of(TextColors.YELLOW, args.get(2), " is not a valid GeneratorType"));
			throw new CommandException(getHelp().getUsageText());
		}
		GeneratorType generatorType = optionalGeneratorType.get();
		
		WorldArchetype.Builder builder = WorldArchetype.builder().dimension(dimensionType).generator(generatorType).enabled(true).keepsSpawnLoaded(true).loadsOnStartup(true);

		Collection<WorldGeneratorModifier> modifiers = Collections.<WorldGeneratorModifier>emptyList();

		if(args.size() >= 4) {
			for(int i = 3; i < args.size() - 3; i++) {
				Optional<WorldGeneratorModifier> optionalModifier = Sponge.getRegistry().getType(WorldGeneratorModifier.class, args.get(i));
				
				if(!optionalModifier.isPresent()) {
					source.sendMessage(Text.of(TextColors.YELLOW, args.get(i), " is not a valid WorldGeneratorModifier"));
					throw new CommandException(getHelp().getUsageText());
				}
				modifiers.add(optionalModifier.get());
			}			
		}
		
		WorldArchetype settings = builder.build(args.get(0), args.get(0));
		
		WorldProperties properties;
		try {
			properties = Sponge.getServer().createWorldProperties(args.get(0), settings);
			properties.setGeneratorModifiers(modifiers);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
		}

		Sponge.getServer().saveWorldProperties(properties);

		source.sendMessage(Text.of(TextColors.DARK_GREEN, args.get(0), " imported successfully"));

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();

		if(arguments.equalsIgnoreCase("")) {
			return list;
		}
		
		List<String> args = Arrays.asList(arguments.split(" "));

		if(args.size() == 1) {
			if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
				return list;
			} else {
				for(DimensionType type : Sponge.getRegistry().getAllOf(DimensionType.class)) {
					list.add(type.getId());
				}
			}
			
			return list;
		}

		if(args.size() == 2) {
			if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
				for(DimensionType type : Sponge.getRegistry().getAllOf(DimensionType.class)) {
					if(type.getId().equalsIgnoreCase(args.get(1))) {
						list.add(type.getId());
					}
					
					if(type.getId().toLowerCase().startsWith(args.get(1).toLowerCase())) {
						list.add(type.getId());
					}
				}
			} else {
				for(GeneratorType type : Sponge.getRegistry().getAllOf(GeneratorType.class)) {
					list.add(type.getId());
				}
			}
			
			return list;
		}

		if(args.size() == 3) {
			if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
				for(GeneratorType type : Sponge.getRegistry().getAllOf(GeneratorType.class)) {
					if(type.getId().equalsIgnoreCase(args.get(2))) {
						list.add(type.getId());
					}
					
					if(type.getId().toLowerCase().startsWith(args.get(2).toLowerCase())) {
						list.add(type.getId());
					}
				}
			} else {
				for(WorldGeneratorModifier type : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
					list.add(type.getId());
				}
			}
			
			return list;
		}
		
		if(args.size() == 4) {
			if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
				for(WorldGeneratorModifier type : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
					if(type.getId().equalsIgnoreCase(args.get(2))) {
						list.add(type.getId());
					}
					
					if(type.getId().toLowerCase().startsWith(args.get(2).toLowerCase())) {
						list.add(type.getId());
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
