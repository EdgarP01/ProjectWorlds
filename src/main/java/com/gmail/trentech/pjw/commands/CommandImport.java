package com.gmail.trentech.pjw.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.CatalogKey;
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

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		String worldName;
		String genType;
		String dimType;
		
		try {
			worldName = args[0];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		if (Sponge.getServer().getWorld(worldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " is already loaded"), false);
		}
		
		WorldData worldData = new WorldData(worldName);

		if (!worldData.exists()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " is not a valid world"), false);
		}

		SpongeData spongeData = new SpongeData(worldName);

		if (spongeData.exists()) {
			source.sendMessage(Text.of(TextColors.RED, "Sponge world detected"));
			source.sendMessage(Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.runCommand("/pjw:world load")).append(Text.of(" /world load")).build());
			return CommandResult.success();
		}
		
		try {
			dimType = args[1];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		String[] dimKey = dimType.split(":");
		Optional<DimensionType> optionalDimensionType = Sponge.getRegistry().getType(DimensionType.class, CatalogKey.of(dimKey[0], dimKey[1]));
		
		if(!optionalDimensionType.isPresent()) {
			source.sendMessage(Text.of(TextColors.YELLOW, dimType, " is not a valid DimensionType"));
			throw new CommandException(getHelp().getUsageText());
		}
		DimensionType dimensionType = optionalDimensionType.get();
		
		try {
			genType = args[2];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		String[] genKey = genType.split(":");
		Optional<GeneratorType> optionalGeneratorType = Sponge.getRegistry().getType(GeneratorType.class, CatalogKey.of(genKey[0], genKey[1]));
		
		if(!optionalGeneratorType.isPresent()) {
			source.sendMessage(Text.of(TextColors.YELLOW, genType, " is not a valid GeneratorType"));
			throw new CommandException(getHelp().getUsageText());
		}
		GeneratorType generatorType = optionalGeneratorType.get();
		
		WorldArchetype.Builder builder = WorldArchetype.builder().dimension(dimensionType).generator(generatorType).enabled(true).keepsSpawnLoaded(true).loadsOnStartup(true);

		Collection<WorldGeneratorModifier> modifiers = Collections.<WorldGeneratorModifier>emptyList();

		if(args.length >= 4) {
			for(int i = 3;i < args.length - 3;i++) {
				String[] key = args[i].split(":");
				Optional<WorldGeneratorModifier> optionalModifier = Sponge.getRegistry().getType(WorldGeneratorModifier.class, CatalogKey.of(key[0], key[1]));
				
				if(!optionalModifier.isPresent()) {
					source.sendMessage(Text.of(TextColors.YELLOW, args[i], " is not a valid WorldGeneratorModifier"));
					throw new CommandException(getHelp().getUsageText());
				}
				modifiers.add(optionalModifier.get());
			}			
		}
		
		WorldArchetype settings = builder.build(worldName, worldName);
		
		WorldProperties properties;
		try {
			properties = Sponge.getServer().createWorldProperties(worldName, settings);
			properties.setGeneratorModifiers(modifiers);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
		}

		Sponge.getServer().saveWorldProperties(properties);

		source.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " imported successfully"));

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("import")) {
			return list;
		}

		String[] args = arguments.split(" ");
		
		if(args.length == 1) {
			for(WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
				if(world.getWorldName().equalsIgnoreCase(args[0])) {
					for(DimensionType type : Sponge.getRegistry().getAllOf(DimensionType.class)) {
						list.add(type.getKey().toString());
					}
					
					return list;
				}
				
				if(world.getWorldName().toLowerCase().startsWith(args[0].toLowerCase())) {
					list.add(world.getWorldName());
				}
			}
		}
		
		if(args.length == 2) {
			for(DimensionType type : Sponge.getRegistry().getAllOf(DimensionType.class)) {
				if(type.getKey().toString().equalsIgnoreCase(args[1])) {
					for(GeneratorType genType : Sponge.getRegistry().getAllOf(GeneratorType.class)) {
						list.add(genType.getKey().toString());
					}
					
					return list;
				}
				
				if(type.getKey().toString().toLowerCase().startsWith(args[1].toLowerCase())) {
					list.add(type.getKey().toString());
				}
			}
		}
		
		if(args.length == 3) {
			for(GeneratorType type : Sponge.getRegistry().getAllOf(GeneratorType.class)) {
				if(type.getKey().toString().equalsIgnoreCase(args[1])) {
					for(WorldGeneratorModifier modType : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
						list.add(modType.getKey().toString());
					}
					
					return list;
				}
				
				if(type.getKey().toString().toLowerCase().startsWith(args[1].toLowerCase())) {
					list.add(type.getKey().toString());
				}
			}
		}
		
		if(args.length >= 4) {
			for(WorldGeneratorModifier type : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
				if(type.getKey().toString().equalsIgnoreCase(args[1])) {
					for(WorldGeneratorModifier modType : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
						list.add(modType.getKey().toString());
					}
					
					return list;
				}
				
				if(type.getKey().toString().toLowerCase().startsWith(args[1].toLowerCase())) {
					list.add(type.getKey().toString());
				}
			}
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
