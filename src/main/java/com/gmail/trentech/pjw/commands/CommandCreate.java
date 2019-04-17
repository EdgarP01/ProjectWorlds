package com.gmail.trentech.pjw.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.help.Help;
import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.utils.Gamemode;

import ninja.leaping.configurate.ConfigurationNode;

public class CommandCreate implements CommandCallable {
	
	private final Help help = Help.get("world create").get();
//	public static List<String> worlds = new ArrayList<>();
	
	@Override
	public CommandResult process(CommandSource source, String arguments) throws CommandException {
		if(arguments.equalsIgnoreCase("create")) {
			throw new CommandException(getHelp().getUsageText());
		}

		List<String> args = Arrays.asList(arguments.split(" "));
		
		if(args.contains("--help")) {
			help.execute(source);
			return CommandResult.success();
		}
		
		if(args.isEmpty()) {
			throw new CommandException(getHelp().getUsageText());
		}

		if (Sponge.getServer().getWorldProperties(args.get(0)).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, args.get(0), " already exists"), false);
		}

		WorldArchetype.Builder builder = WorldArchetype.builder();
		
		if (args.contains("--loadsOnStartup")) {
			builder.loadsOnStartup(true);
		} else {
			builder.loadsOnStartup(false);
		}
		if (args.contains("--keepsSpawnLoaded")) {
			builder.keepsSpawnLoaded(true);
		} else {
			builder.keepsSpawnLoaded(false);
		}
		if (args.contains("--commandsAllowed")) {
			builder.commandsAllowed(true);
		} else {
			builder.commandsAllowed(false);
		}
		if (args.contains("--generateBonusChest")) {
			builder.generateBonusChest(true);
		} else {
			builder.generateBonusChest(false);
		}
		if (args.contains("--usesMapFeatures")) {
			builder.usesMapFeatures(true);
		} else {
			builder.usesMapFeatures(false);
		}

		List<WorldGeneratorModifier> modifiers = new ArrayList<>();
		
		boolean skip = false;

		for(String arg : args) {
			if(arg.equalsIgnoreCase(args.get(0)) || arg.equalsIgnoreCase("--usesMapFeatures") || arg.equalsIgnoreCase("--generateBonusChest") 
					|| arg.equalsIgnoreCase("--commandsAllowed") || arg.equalsIgnoreCase("--keepsSpawnLoaded") || arg.equalsIgnoreCase("--loadsOnStartup")) {
				continue;
			}
			
			if(skip) {
				skip = false;
				continue;
			}
			
			String value;
			try {
				value = args.get(args.indexOf(arg) + 1);
			} catch(Exception e) {
				throw new CommandException(getHelp().getUsageText());
			}
			
			if(arg.equalsIgnoreCase("-dimension")) {
				Optional<DimensionType> optionalDimension = Sponge.getRegistry().getType(DimensionType.class, value);
				
				if(!optionalDimension.isPresent()) {
					source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid DimensionType"));
					throw new CommandException(getHelp().getUsageText());
				}			
				builder.dimension(optionalDimension.get());
			} else if (arg.equalsIgnoreCase("-generator")) {				
				Optional<GeneratorType> optionalGenerator = Sponge.getRegistry().getType(GeneratorType.class, value);
				
				if(!optionalGenerator.isPresent()) {
					source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid GeneratorType"));
					throw new CommandException(getHelp().getUsageText());
				}
				builder.generator(optionalGenerator.get());
			} else if (arg.equalsIgnoreCase("-options")) {
				source.sendMessage(Text.of(TextColors.YELLOW, "Custom Settings are not validated. Any errors and it will not apply correctly."));
				builder.generatorSettings(DataContainer.createNew().set(DataQuery.of("customSettings"), value));
			} else if (arg.equalsIgnoreCase("-gameMode")) {
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
			} else if (arg.equalsIgnoreCase("-modifier")) {
				Optional<WorldGeneratorModifier> optionalModifier = Sponge.getRegistry().getType(WorldGeneratorModifier.class, value);
				
				if(!optionalModifier.isPresent()) {
					source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid WorldGeneratorModifier"));
					throw new CommandException(getHelp().getUsageText());
				}
				modifiers.add(optionalModifier.get());
			} else if (arg.equalsIgnoreCase("-seed")) {
				try {
					Long s = Long.parseLong(value);
					builder.seed(s);
				} catch (Exception e) {
					builder.seed(value.hashCode());
				}
			} else if (arg.equalsIgnoreCase("-difficulty")) {
				Optional<Difficulty> optionalDifficulty = Sponge.getRegistry().getType(Difficulty.class, value);
				
				if(!optionalDifficulty.isPresent()) {
					source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Difficulty"));
					throw new CommandException(getHelp().getUsageText());
				}			
				builder.difficulty(optionalDifficulty.get());
			} else {
				source.sendMessage(Text.of(TextColors.YELLOW, value, " is not a valid Flag"));
				throw new CommandException(getHelp().getUsageText());
			}
			skip = true;
		}
		
		WorldArchetype settings = builder.enabled(true).keepsSpawnLoaded(true).loadsOnStartup(true).build(args.get(0), args.get(0));

		WorldProperties properties;
		try {
			properties = Sponge.getServer().createWorldProperties(args.get(0), settings);
			
			properties.setGeneratorModifiers(modifiers);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
		}

		Sponge.getServer().saveWorldProperties(properties);

		SpongeData spongeData = new SpongeData(properties.getWorldName());
		
		if (spongeData.exists()) {
			ConfigManager config = ConfigManager.get(Main.getPlugin());
			
			ConfigurationNode node = config.getConfig().getNode("worlds");

			SpongeData.ids.put(properties.getWorldName(), spongeData.getDimId());
			node.getNode(properties.getWorldName()).setValue(spongeData.getDimId());
			config.save();
		}
		
		//worlds.add(args.get(0));

		source.sendMessage(Text.of(TextColors.DARK_GREEN, args.get(0), " created successfully"));

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
				list.add("-dimension");
				list.add("-generator");
				list.add("-gameMode");
				list.add("-modifier");
				list.add("-difficulty");
				list.add("-seed");
				list.add("-options");
				list.add("--loadsOnStartup");
				list.add("--keepsSpawnLoaded");
				list.add("--generateBonusChest");
				list.add("--commandsAllowed");
				list.add("--usesMapFeatures");
			}
			
			return list;
		}

		if(args.size() > 1) {
			String arg = args.get(args.size() - 1);
			
			if(!arg.equalsIgnoreCase("-generator") && !arg.equalsIgnoreCase("-dimension") && !arg.equalsIgnoreCase("-options") && !arg.equalsIgnoreCase("-seed") 
					&& !arg.equalsIgnoreCase("-gameMode") && !arg.equalsIgnoreCase("-modifier") && !arg.equalsIgnoreCase("-difficulty")) {
				if(!arguments.substring(arguments.length() - 1).equalsIgnoreCase(" ")) {
					if("-dimension".startsWith(arg)) {
						list.add("-dimension");
					}
					if("-generator".startsWith(arg)) {
						list.add("-generator");
					}
					if("-gameMode".startsWith(arg)) {
						list.add("-gameMode");
					}
					if("-modifier".startsWith(arg)) {
						list.add("-modifier");
					}
					if("-difficulty".startsWith(arg)) {
						list.add("-difficulty");
					}
					if("-seed".startsWith(arg)) {
						list.add("-seed");
					}
					if("-options".startsWith(arg)) {
						list.add("-options");
					}
					if("--usesMapFeatures".startsWith(arg)) {
						list.add("--usesMapFeatures");
					}
					if("--keepsSpawnLoaded".startsWith(arg)) {
						list.add("--keepsSpawnLoaded");
					}
					if("--loadsOnStartup".startsWith(arg)) {
						list.add("--loadsOnStartup");
					}
					if("--commandsAllowed".startsWith(arg)) {
						list.add("--commandsAllowed");
					}
					if("--generateBonusChest".startsWith(arg)) {
						list.add("--generateBonusChest");
					}
				} else {
					if(!args.contains("-dimension")) {
						list.add("-dimension");
					}
					if(!args.contains("-generator")) {
						list.add("-generator");
					}
					if(!args.contains("-gameMode")) {
						list.add("-gameMode");
					}
					if(!args.contains("-difficulty")) {
						list.add("-difficulty");
					}
					if(!args.contains("-options")) {
						list.add("-options");
					}
					if(!args.contains("-seed")) {
						list.add("-seed");
					}
					if(!args.contains("--usesMapFeatures")) {
						list.add("--usesMapFeatures");
					}
					if(!args.contains("--keepsSpawnLoaded")) {
						list.add("--keepsSpawnLoaded");
					}
					if(!args.contains("--loadsOnStartup")) {
						list.add("--loadsOnStartup");
					}
					if(!args.contains("--commandsAllowed")) {
						list.add("--commandsAllowed");
					}
					if(!args.contains("--generateBonusChest")) {
						list.add("--generateBonusChest");
					}
				}
			} else {
				if(arg.equalsIgnoreCase("-dimension")) {
					for(DimensionType type : Sponge.getRegistry().getAllOf(DimensionType.class)) {
						list.add(type.getId());
					}
				} else if (arg.equalsIgnoreCase("-generator")) {
					for(GeneratorType type : Sponge.getRegistry().getAllOf(GeneratorType.class)) {
						list.add(type.getId());
					}
				} else if (arg.equalsIgnoreCase("-gameMode")) {
					for(Gamemode type : Gamemode.values()) {
						list.add(Integer.toString(type.getIndex()));
						list.add(type.getGameMode().getName());
					}
				} else if (arg.equalsIgnoreCase("-modifier")) {
					for(WorldGeneratorModifier type : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
						list.add(type.getId());
					}
				} else if (arg.equalsIgnoreCase("-difficulty")) {
					for(Difficulty type : Sponge.getRegistry().getAllOf(Difficulty.class)) {
						list.add(type.getId());
					}
				} else if (arg.equalsIgnoreCase("--loadsOnStartup") || arg.equalsIgnoreCase("--keepsSpawnLoaded") || arg.equalsIgnoreCase("--commandsAllowed") 
						|| arg.equalsIgnoreCase("--generateBonusChest") || arg.equalsIgnoreCase("--usesMapFeatures")) {
					return list;
				} else {
					String parent = args.get(args.size() - 2);
					
					if(parent.equalsIgnoreCase("-dimension")) {
						for(DimensionType type : Sponge.getRegistry().getAllOf(DimensionType.class)) {
							if(type.getId().toLowerCase().startsWith(arg.toLowerCase())) {
								list.add(type.getId());
							}
						}
					} else if (parent.equalsIgnoreCase("-generator")) {
						for(GeneratorType type : Sponge.getRegistry().getAllOf(GeneratorType.class)) {
							if(type.getId().toLowerCase().startsWith(arg.toLowerCase())) {
								list.add(type.getId());
							}
						}
					} else if (parent.equalsIgnoreCase("-gameMode")) {
						for(Gamemode type : Gamemode.values()) {
							if(type.getGameMode().getName().toLowerCase().startsWith(arg.toLowerCase())) {
								list.add(type.getGameMode().getName());
							}
						}
					} else if (parent.equalsIgnoreCase("-modifier")) {
						for(WorldGeneratorModifier type : Sponge.getRegistry().getAllOf(WorldGeneratorModifier.class)) {
							if(type.getId().toLowerCase().startsWith(arg.toLowerCase())) {
								list.add(type.getId());
							}
						}
					} else if (parent.equalsIgnoreCase("-difficulty")) {
						for(Difficulty type : Sponge.getRegistry().getAllOf(Difficulty.class)) {
							if(type.getId().toLowerCase().startsWith(arg.toLowerCase())) {
								list.add(type.getId());
							}
						}
					}
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
