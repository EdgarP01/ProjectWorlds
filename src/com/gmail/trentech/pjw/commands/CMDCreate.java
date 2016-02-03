package com.gmail.trentech.pjw.commands;

import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.WorldCreationSettings.Builder;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;

public class CMDCreate implements CommandExecutor {

	private Builder builder = WorldCreationSettings.builder();
	
	public CMDCreate(){
		String alias = new ConfigManager().getConfig().getNode("settings", "commands", "world").getString();
		
		Help help = new Help("create", " Allows you to create new worlds with any combination of optional arguments D: "
				+ "for dimension type, G: for generator type, S: for seed and M: for generator modifiers");
		help.setSyntax(" /world create <world> [d:type] [g:generator] [m:modifer]  [s:seed]\n /" + alias + " cr <world> [d:type] [g:generator] [m:modifer]  [s:seed]");
		help.setExample(" /world create NewWorld s:-12309830198412353456\n /world create NewWorld d:overworld g:overworld\n"
						+ " /world create NewWorld d:nether m:sponge:skylands\n /world create m:pjw:void");
		CMDHelp.getList().add(help);
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();

		if(worldName.contains(":")){
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		
		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " already exists"));
			return CommandResult.empty();
		}
		
		builder.name(worldName);

		if(args.hasAny("arg0")) {
			if(!add(src, args.<String>getOne("arg0").get())){
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
		}

		if(args.hasAny("arg1")) {
			if(!add(src, args.<String>getOne("arg1").get())){
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
		}

		if(args.hasAny("arg2")) {
			if(!add(src, args.<String>getOne("arg2").get())){
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
		}

		if(args.hasAny("arg3")) {
			if(!add(src, args.<String>getOne("arg3").get())){
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
		}

		WorldCreationSettings settings = builder.enabled(true).keepsSpawnLoaded(true).loadsOnStartup(true).build();

		final Optional<WorldProperties> optionalProperties = Main.getGame().getServer().createWorldProperties(settings);

        if (!optionalProperties.isPresent()) {
			src.sendMessage(Text.of(TextColors.DARK_RED, "something went wrong"));
			return CommandResult.empty();
        }

        optionalProperties.get();
        
        src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " created successfully"));

		return CommandResult.success();
	}

	private Text invalidArg(){
		Main.getGame().getRegistry().getAllOf(DimensionType.class);
		Text t1 = Text.of(TextColors.YELLOW, "/world create <world> ");
		org.spongepowered.api.text.Text.Builder dimTypes = null;
		for(DimensionType dimType : Main.getGame().getRegistry().getAllOf(DimensionType.class)){
			if(dimTypes == null){
				dimTypes = Text.builder().append(Text.of(dimType.getName()));
			}else{
				dimTypes.append(Text.of("\n", dimType.getName()));
			}
		}
		Text t2 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(dimTypes.build())).append(Text.of("[d:type] ")).build();
		org.spongepowered.api.text.Text.Builder genTypes = Text.builder();
		for(GeneratorType genType : Main.getGame().getRegistry().getAllOf(GeneratorType.class)){
			if(!genType.getName().equalsIgnoreCase("debug_all_block_states") && !genType.getName().equalsIgnoreCase("default_1_1")){
				if(genTypes == null){
					genTypes = Text.builder().append(Text.of(genType.getName()));
				}else{
					genTypes.append(Text.of(genType.getName(), "\n"));
				}
			}
		}
		Text t3 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(genTypes.build())).append(Text.of("[g:generator] ")).build();
		org.spongepowered.api.text.Text.Builder modifiers = null;
		for(Entry<String, WorldGeneratorModifier> modifier :Main.getModifiers().entrySet()){
			if(modifiers == null){
				modifiers = Text.builder().append(Text.of(modifier.getKey()));
			}else{
				modifiers.append(Text.of("\n", modifier.getKey()));
			}	
		}
		Text t4 = Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(modifiers.build())).append(Text.of("[m:modifier] ")).build();
		Text t5 = Text.of(TextColors.YELLOW, "[s:seed]");
		return Text.of(t1,t2,t3,t4,t5);
	}
	
	private boolean add(CommandSource src, String arg) {
		String[] option = arg.split(":");
		
		switch(option[0].toUpperCase()){
			case "D":
				if(Main.getGame().getRegistry().getType(DimensionType.class, option[1]).isPresent()){
					builder.dimension(Main.getGame().getRegistry().getType(DimensionType.class, option[1]).get());
					return true;
				}
				src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid Dimension Type"));
				return false;
			case "G":
				if(Main.getGame().getRegistry().getType(GeneratorType.class, option[1]).isPresent()){
					builder.generator(Main.getGame().getRegistry().getType(GeneratorType.class, option[1]).get());
					return true;
				}
				src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid Generator Type"));
				return false;
			case "M":
				String modifier = option[1];
				if(option.length > 2){
					modifier = option[1] + ":" + option[2];
				}
				if(Main.getModifiers().get(modifier) != null){
					builder.generatorModifiers(Main.getModifiers().get(modifier));
					return true;
				}
				src.sendMessage(Text.of(TextColors.DARK_RED, "Invalid Modifier Type"));
				return false;
			case "S":
				try{
					Long seed = Long.parseLong(option[1]);
					builder.seed(seed);
				}catch(Exception e){
					builder.seed(option[1].hashCode());
				}		
				return true;
			default: return false;
		}
	}
}
