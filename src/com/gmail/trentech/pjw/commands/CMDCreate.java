package com.gmail.trentech.pjw.commands;

import java.util.Map.Entry;
import java.util.Optional;

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
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBuilder;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.modifiers.Modifiers;

public class CMDCreate implements CommandExecutor {

	private WorldBuilder builder = Main.getGame().getRegistry().createBuilder(WorldBuilder.class);
	
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

		Optional<World> load = builder.enabled(true).loadsOnStartup(true).build();

		if(!load.isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "something went wrong"));
			return CommandResult.empty();
		}
	
		src.sendMessage(Texts.of(TextColors.DARK_GREEN, worldName, " created successfully"));
		return CommandResult.success();
	}

	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.GOLD, "/world create <world> ");
		Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("OVERWORLD\nNETHER\nEND"))).append(Texts.of("[D:type] ")).build();
		Text t3 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("DEFAULT\nOVERWORLD\nNETHER\nEND\nFLAT"))).append(Texts.of("[G:generator] ")).build();
		TextBuilder modifierShow = null;
		for(Entry<String, WorldGeneratorModifier> modifiers :Modifiers.getAll().entrySet()){
			if(modifierShow == null){
				modifierShow = Texts.builder().append(Texts.of(modifiers.getKey()));
			}else{
				modifierShow.append(Texts.of("\n", modifiers.getKey()));
			}	
		}
		Text t4 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(modifierShow.build())).append(Texts.of("[M:modifier] ")).build();
		Text t5 = Texts.of(TextColors.GOLD, "[S:seed]");
		return Texts.of(t1,t2,t3,t4,t5);
	}
	
	private boolean add(String arg) {
		String[] option = arg.split(":");
		
		switch(option[0]){
			case "D":
				if(Main.getGame().getRegistry().getType(DimensionType.class, option[1]).isPresent()){
					builder.dimensionType(Main.getGame().getRegistry().getType(DimensionType.class, option[1]).get());
				}else{
					Main.getLog().warn("Dimension type " + option[1] + "does not exist. defaulting to OVERWORLD");
					builder.dimensionType(DimensionTypes.OVERWORLD);
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
}
