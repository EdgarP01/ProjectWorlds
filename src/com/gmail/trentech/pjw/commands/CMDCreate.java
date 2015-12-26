package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.WorldCreationSettings.Builder;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.modifiers.Modifiers;

public class CMDCreate implements CommandExecutor {

	private Builder builder = WorldCreationSettings.builder();
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();

		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " already exists"));
			return CommandResult.empty();
		}
		
		builder.name(worldName);

		if(args.hasAny("arg0")) {
			if(!add(args.<String>getOne("arg0").get().toUpperCase())){
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
		}

		if(args.hasAny("arg1")) {
			if(!add(args.<String>getOne("arg1").get().toUpperCase())){
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
		}

		if(args.hasAny("arg2")) {
			if(!add(args.<String>getOne("arg2").get().toUpperCase())){
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
		}

		if(args.hasAny("arg3")) {
			if(!add(args.<String>getOne("arg3").get().toUpperCase())){
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
		}

		WorldCreationSettings settings = builder.enabled(true).loadsOnStartup(true).build();

		final Optional<WorldProperties> optProperties = Main.getGame().getServer().createWorldProperties(settings);

        if (!optProperties.isPresent()) {
			src.sendMessage(Texts.of(TextColors.DARK_RED, "something went wrong"));
			return CommandResult.empty();
        }

        src.sendMessage(Texts.of(TextColors.YELLOW, "Preparing spawn area. This may take a minute."));

		Optional<World> load = Main.getGame().getServer().loadWorld(optProperties.get());

		if(!load.isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "something went wrong"));
			return CommandResult.empty();
		}

		World world = load.get();
		
		createPlatform(world.getSpawnLocation().getRelative(Direction.DOWN));
		
		src.sendMessage(Texts.of(TextColors.DARK_GREEN, worldName, " created successfully"));
		return CommandResult.success();
	}

	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.YELLOW, "/world create <world> ");
		Text t2 = Texts.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Texts.of("OVERWORLD\nNETHER\nTHE_END"))).append(Texts.of("[D:type] ")).build();
		Text t3 = Texts.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Texts.of("DEFAULT\nOVERWORLD\nNETHER\nTHE_END\nFLAT\nAMPLIFIED\nLARGE_BIOMES"))).append(Texts.of("[G:generator] ")).build();
		TextBuilder modifierShow = null;
		for(Entry<String, WorldGeneratorModifier> modifiers :Modifiers.getAll().entrySet()){
			if(modifierShow == null){
				modifierShow = Texts.builder().append(Texts.of(modifiers.getKey()));
			}else{
				modifierShow.append(Texts.of("\n", modifiers.getKey()));
			}	
		}
		Text t4 = Texts.builder().color(TextColors.YELLOW).onHover(TextActions.showText(modifierShow.build())).append(Texts.of("[M:modifier] ")).build();
		Text t5 = Texts.of(TextColors.YELLOW, "[S:seed]");
		return Texts.of(t1,t2,t3,t4,t5);
	}
	
	private boolean add(String arg) {
		String[] option = arg.split(":");
		
		switch(option[0]){
			case "D":
				if(Main.getGame().getRegistry().getType(DimensionType.class, option[1]).isPresent()){
					builder.dimension(Main.getGame().getRegistry().getType(DimensionType.class, option[1]).get());
				}else{
					Main.getLog().warn("Dimension type " + option[1] + "does not exist. defaulting to OVERWORLD");
					builder.dimension(DimensionTypes.OVERWORLD);
				}
				return true;				
			case "G":
				if(Main.getGame().getRegistry().getType(GeneratorType.class, option[1]).isPresent()){
					builder.generator(Main.getGame().getRegistry().getType(GeneratorType.class, option[1]).get());
				}else{
					Main.getLog().warn("Generator type " + option[1] + "does not exist. defaulting to DEFAULT");
					builder.generator(GeneratorTypes.DEFAULT);
				}
				return true;
			case "M":
				if(Modifiers.get(option[1]) != null){
					builder.generatorModifiers(Modifiers.get(option[1]));
				}else{
					Main.getLog().warn("Modifier type " + option[1] + "does not exist.");
				}
				return true;
			case "S":
				builder.seed(option[1].hashCode());
				return true;
			default: return false;
		}
	}
	
	private void createPlatform(Location<World> center){
		platform(center, BlockTypes.STONE);
		
		platform(center.getRelative(Direction.UP), BlockTypes.AIR);
		platform(center.getRelative(Direction.UP).getRelative(Direction.UP), BlockTypes.AIR);
		platform(center.getRelative(Direction.UP).getRelative(Direction.UP).getRelative(Direction.UP), BlockTypes.AIR);
	}
	
	private void platform(Location<World> center, BlockType type){
		List<Location<World>> list = new ArrayList<>();

		Location<World> south = center.getRelative(Direction.SOUTH);
		Location<World> north = center.getRelative(Direction.NORTH);
		Location<World> east = center.getRelative(Direction.EAST);
		Location<World> west = center.getRelative(Direction.WEST);
		
		Location<World> north2 = north.getRelative(Direction.NORTH);
		Location<World> south2 = south.getRelative(Direction.SOUTH);
		Location<World> east2 = east.getRelative(Direction.EAST);
		Location<World> west2 = west.getRelative(Direction.WEST);
		
		list.add(center);
		
		list.add(north);
		list.add(south);
		list.add(east);
		list.add(west);	
		
		list.add(north.getRelative(Direction.EAST));
		list.add(north.getRelative(Direction.WEST));
		list.add(south.getRelative(Direction.EAST));
		list.add(south.getRelative(Direction.WEST));
		
		list.add(north2);
		list.add(north2.getRelative(Direction.EAST));
		list.add(north2.getRelative(Direction.EAST).getRelative(Direction.EAST));
		list.add(north2.getRelative(Direction.WEST));
		list.add(north2.getRelative(Direction.WEST).getRelative(Direction.WEST));

		list.add(south2);
		list.add(south2.getRelative(Direction.EAST));
		list.add(south2.getRelative(Direction.EAST).getRelative(Direction.EAST));
		list.add(south2.getRelative(Direction.WEST));
		list.add(south2.getRelative(Direction.WEST).getRelative(Direction.WEST));
		
		list.add(east2);
		list.add(east2.getRelative(Direction.NORTH));
		list.add(east2.getRelative(Direction.SOUTH));
		
		list.add(west2);
		list.add(west2.getRelative(Direction.NORTH));
		list.add(west2.getRelative(Direction.SOUTH));

		for(Location<World> location : list){
			location.setBlock(Main.getGame().getRegistry().createBuilder(BlockState.Builder.class).blockType(type).build());
		}
	}
}
