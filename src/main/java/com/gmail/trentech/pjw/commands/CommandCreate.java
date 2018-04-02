package com.gmail.trentech.pjw.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.utils.Gamemode;

public class CommandCreate implements CommandCallable {
	
	private final Help help = Help.get("world create").get();
	public static List<String> worlds = new ArrayList<>();
	
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("create")) {
			throw new CommandException(getHelp().getUsageText());
		}

		String[] args = arguments.split(" ");
		
		if(args[args.length - 1].equalsIgnoreCase("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		String worldName;
		
		try {
			worldName = args[0];
		} catch(Exception e) {
			throw new CommandException(getHelp().getUsageText());
		}
		
		if (Sponge.getServer().getWorldProperties(worldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " already exists"), false);
		}

		WorldArchetype.Builder builder = WorldArchetype.builder();
		
		List<WorldGeneratorModifier> modifiers = new ArrayList<>();
		
		if(args.length > 2) {
			boolean skip = false;
			
			for(int i = 1; i < args.length - 1; i++) {
				if(skip) {
					skip = false;
					continue;
				}
				
				String arg = args[i];
				String value;
				
				try {
					value = args[i+1];
				} catch(Exception e) {
					throw new CommandException(getHelp().getUsageText());
				}
				
				if(arg.equalsIgnoreCase("-d") || arg.equalsIgnoreCase("-dimension")) {
					Optional<DimensionType> optionalDimension = Sponge.getRegistry().getType(DimensionType.class, value);
					
					if(!optionalDimension.isPresent()) {
						source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid DimensionType"));
						throw new CommandException(getHelp().getUsageText());
					}			
					builder.dimension(optionalDimension.get());
				} else if (arg.equalsIgnoreCase("-g") || arg.equalsIgnoreCase("-generator")) {
					String[] split = value.split("\\{");
					
					Optional<GeneratorType> optionalGenerator = Sponge.getRegistry().getType(GeneratorType.class, split[0]);
					
					if(!optionalGenerator.isPresent()) {
						source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid GeneratorType"));
						throw new CommandException(getHelp().getUsageText());
					}
					builder.generator(optionalGenerator.get());

					if(split.length == 2) {
						source.sendMessage(Text.of(TextColors.YELLOW, "Custom Settings are not validated. Any errors and it will not apply correctly."));
						builder.generatorSettings(DataContainer.createNew().set(DataQuery.of("customSettings"), split[1].replace("\\}", "")));
					}
				} else if (arg.equalsIgnoreCase("-gm") || arg.equalsIgnoreCase("-gamemode")) {
					Optional<GameMode> optionalGamemode = Optional.empty();
					
					try {
						optionalGamemode = Gamemode.get(Integer.parseInt(value));
					} catch(Exception e) {
						optionalGamemode = Gamemode.get(value);
					}

					if(!optionalGamemode.isPresent()) {
						source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid GameMode"));
						throw new CommandException(getHelp().getUsageText());
					}			
					builder.gameMode(optionalGamemode.get());
				} else if (arg.equalsIgnoreCase("-m") || arg.equalsIgnoreCase("-modifier")) {
					Optional<WorldGeneratorModifier> optionalModifier = Sponge.getRegistry().getType(WorldGeneratorModifier.class, value);
					
					if(!optionalModifier.isPresent()) {
						source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid WorldGeneratorModifier"));
						throw new CommandException(getHelp().getUsageText());
					}
					modifiers.add(optionalModifier.get());
				} else if (arg.equalsIgnoreCase("-s") || arg.equalsIgnoreCase("-seed")) {
					try {
						Long s = Long.parseLong(value);
						builder.seed(s);
					} catch (Exception e) {
						builder.seed(value.hashCode());
					}
				} else if (arg.equalsIgnoreCase("-df") || arg.equalsIgnoreCase("-difficulty")) {
					Optional<Difficulty> optionalDifficulty = Sponge.getRegistry().getType(Difficulty.class, value);
					
					if(!optionalDifficulty.isPresent()) {
						source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Difficulty"));
						throw new CommandException(getHelp().getUsageText());
					}			
					builder.difficulty(optionalDifficulty.get());
				} else if (arg.equalsIgnoreCase("-l") || arg.equalsIgnoreCase("-loadonstartup")) {
					if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
						builder.loadsOnStartup(Boolean.valueOf(value));
					} else {
						source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Boolean"));
						throw new CommandException(getHelp().getUsageText());
					}		
				} else if (arg.equalsIgnoreCase("-k") || arg.equalsIgnoreCase("-keepspawnloaded")) {
					if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
						builder.keepsSpawnLoaded(Boolean.valueOf(value));
					} else {
						source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Boolean"));
						throw new CommandException(getHelp().getUsageText());
					}
				} else if (arg.equalsIgnoreCase("-c") || arg.equalsIgnoreCase("-allowcommands")) {
					if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
						builder.commandsAllowed(Boolean.valueOf(value));
					} else {
						source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Boolean"));
						throw new CommandException(getHelp().getUsageText());
					}
				} else if (arg.equalsIgnoreCase("-b") || arg.equalsIgnoreCase("-bonuschest")) {
					if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
						builder.generateBonusChest(Boolean.valueOf(value));
					} else {
						source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Boolean"));
						throw new CommandException(getHelp().getUsageText());
					}
				} else if (arg.equalsIgnoreCase("-f") || arg.equalsIgnoreCase("-mapfeatures")) {
					if(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
						builder.usesMapFeatures(Boolean.valueOf(value));
					} else {
						source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Boolean"));
						throw new CommandException(getHelp().getUsageText());
					}
				} else {
					source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Flag"));
					throw new CommandException(getHelp().getUsageText());
				}
				skip = true;
			}
		}
		
		WorldArchetype settings = builder.enabled(true).keepsSpawnLoaded(true).loadsOnStartup(true).build(worldName, worldName);

		WorldProperties properties;
		try {
			properties = Sponge.getServer().createWorldProperties(worldName, settings);
			
			properties.setGeneratorModifiers(modifiers);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
		}

		Sponge.getServer().saveWorldProperties(properties);

		worlds.add(worldName);

		source.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " created successfully"));

		return CommandResult.success();
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments, Location<World> targetPosition) throws CommandException {
		List<String> list = new ArrayList<>();
		
		if(arguments.equalsIgnoreCase("create")) {
			return list;
		}

		String[] args = arguments.split(" ");
		
		if(args.length <= 1) {
			return list;
		}

		String arg = args[args.length -1];

		if(arg.equalsIgnoreCase("-d") || arg.equalsIgnoreCase("-dimension")) {
			for(DimensionType type : Sponge.getRegistry().getAllOf(DimensionType.class)) {
				list.add(type.getId());
			}
		} else if (arg.equalsIgnoreCase("-g") || arg.equalsIgnoreCase("-generator")) {
			for(GeneratorType type : Sponge.getRegistry().getAllOf(GeneratorType.class)) {
				list.add(type.getId());
			}
		} else if (arg.equalsIgnoreCase("-gm") || arg.equalsIgnoreCase("-gamemode")) {
			for(Gamemode type : Gamemode.values()) {
				list.add(Integer.toString(type.getIndex()));
				list.add(type.getGameMode().getName());
			}
		} else if (arg.equalsIgnoreCase("-m") || arg.equalsIgnoreCase("-modifier")) {
			for(WorldGeneratorModifier type : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
				list.add(type.getId());
			}
		} else if (arg.equalsIgnoreCase("-df") || arg.equalsIgnoreCase("-difficulty")) {
			for(Difficulty type : Sponge.getRegistry().getAllOf(Difficulty.class)) {
				list.add(type.getId());
			}
		} else if (arg.equalsIgnoreCase("-l") || arg.equalsIgnoreCase("-loadonstartup") || arg.equalsIgnoreCase("-k") || arg.equalsIgnoreCase("-keepspawnloaded") 
				|| arg.equalsIgnoreCase("-c") || arg.equalsIgnoreCase("-allowcommands") || arg.equalsIgnoreCase("-b") || arg.equalsIgnoreCase("-bonuschest")
				|| arg.equalsIgnoreCase("-f") || arg.equalsIgnoreCase("-mapfeatures")) {
			list.add("true");
			list.add("false");
		} else {
			String parent = args[args.length - 2];
			
			if(parent.equalsIgnoreCase("-d") || parent.equalsIgnoreCase("-dimension")) {
				for(DimensionType type : Sponge.getRegistry().getAllOf(DimensionType.class)) {
					if(type.getId().toLowerCase().startsWith(arg.toLowerCase())) {
						list.add(type.getId());
					}
				}
			} else if (parent.equalsIgnoreCase("-g") || parent.equalsIgnoreCase("-generator")) {
				for(GeneratorType type : Sponge.getRegistry().getAllOf(GeneratorType.class)) {
					if(type.getId().toLowerCase().startsWith(arg.toLowerCase())) {
						list.add(type.getId());
					}
				}
			} else if (parent.equalsIgnoreCase("-gm") || parent.equalsIgnoreCase("-gamemode")) {
				for(Gamemode type : Gamemode.values()) {
					if(type.getGameMode().getName().toLowerCase().startsWith(arg.toLowerCase())) {
						list.add(type.getGameMode().getName());
					}
				}
			} else if (parent.equalsIgnoreCase("-m") || parent.equalsIgnoreCase("-modifier")) {
				for(WorldGeneratorModifier type : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
					if(type.getId().toLowerCase().startsWith(arg.toLowerCase())) {
						list.add(type.getId());
					}
				}
			} else if (parent.equalsIgnoreCase("-df") || parent.equalsIgnoreCase("-difficulty")) {
				for(Difficulty type : Sponge.getRegistry().getAllOf(Difficulty.class)) {
					if(type.getId().toLowerCase().startsWith(arg.toLowerCase())) {
						list.add(type.getId());
					}
				}
			} else if (arg.equalsIgnoreCase("-l") || arg.equalsIgnoreCase("-loadonstartup") || arg.equalsIgnoreCase("-k") || arg.equalsIgnoreCase("-keepspawnloaded") 
					|| arg.equalsIgnoreCase("-c") || arg.equalsIgnoreCase("-allowcommands") || arg.equalsIgnoreCase("-b") || arg.equalsIgnoreCase("-bonuschest")
					|| arg.equalsIgnoreCase("-f") || arg.equalsIgnoreCase("-mapfeatures")) {
				if("true".startsWith(arg.toLowerCase())) {
					list.add("true");
				}
				if("false".startsWith(arg.toLowerCase())) {
					list.add("false");
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
