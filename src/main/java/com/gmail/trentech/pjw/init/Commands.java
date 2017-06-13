package com.gmail.trentech.pjw.init;

import org.spongepowered.api.CatalogTypes;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import com.gmail.trentech.pjw.commands.CMDCopy;
import com.gmail.trentech.pjw.commands.CMDCreate;
import com.gmail.trentech.pjw.commands.CMDDifficulty;
import com.gmail.trentech.pjw.commands.CMDEnable;
import com.gmail.trentech.pjw.commands.CMDFill;
import com.gmail.trentech.pjw.commands.CMDGamemode;
import com.gmail.trentech.pjw.commands.CMDGamerule;
import com.gmail.trentech.pjw.commands.CMDHardcore;
import com.gmail.trentech.pjw.commands.CMDImport;
import com.gmail.trentech.pjw.commands.CMDKeepSpawnLoaded;
import com.gmail.trentech.pjw.commands.CMDList;
import com.gmail.trentech.pjw.commands.CMDLoad;
import com.gmail.trentech.pjw.commands.CMDLoadOnStartup;
import com.gmail.trentech.pjw.commands.CMDModifier;
import com.gmail.trentech.pjw.commands.CMDProperties;
import com.gmail.trentech.pjw.commands.CMDPvp;
import com.gmail.trentech.pjw.commands.CMDRegen;
import com.gmail.trentech.pjw.commands.CMDRemove;
import com.gmail.trentech.pjw.commands.CMDRename;
import com.gmail.trentech.pjw.commands.CMDSetSpawn;
import com.gmail.trentech.pjw.commands.CMDTeleport;
import com.gmail.trentech.pjw.commands.CMDTime;
import com.gmail.trentech.pjw.commands.CMDUnload;
import com.gmail.trentech.pjw.commands.CMDUseMapFeatures;
import com.gmail.trentech.pjw.commands.CMDWeather;
import com.gmail.trentech.pjw.commands.CMDWorld;
import com.gmail.trentech.pjw.commands.elements.GameModeElement;
import com.gmail.trentech.pjw.commands.elements.GameruleElement;

public class Commands {

	private CommandElement element = GenericArguments.flags().flag("help").setAcceptsArbitraryLongFlags(true).buildWith(GenericArguments.none());
	
	private CommandSpec cmdCreate = CommandSpec.builder()
		    .description(Text.of(" Allows you to create new worlds with any combination of optional arguments -d for dimension type, -g for generator type, -s for seed and -m for generator modifiers."))
		    .permission("pjw.cmd.world.create")	    
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.flags()
	                .valueFlag(GenericArguments.onlyOne(GenericArguments.dimension(Text.of("dimension"))), "d", "-dimension")
	                .valueFlag(GenericArguments.onlyOne(GenericArguments.catalogedElement(Text.of("generator"), CatalogTypes.GENERATOR_TYPE)), "g", "-generator")
					.valueFlag(GenericArguments.catalogedElement(Text.of("modifier"), CatalogTypes.WORLD_GENERATOR_MODIFIER), "m", "-modifer")
					.valueFlag(GenericArguments.onlyOne(GenericArguments.string(Text.of("seed"))), "s", "-seed")
					.valueFlag(GenericArguments.onlyOne(new GameModeElement(Text.of("gamemode"))), "-gm", "-gamemode")
	                .valueFlag(GenericArguments.onlyOne(GenericArguments.catalogedElement(Text.of("difficulty"), CatalogTypes.DIFFICULTY)), "-di", "-difficulty")
	                .valueFlag(GenericArguments.bool(Text.of("l")), "l", "-loadonstartup")
	                .valueFlag(GenericArguments.bool(Text.of("k")), "k", "-keepspawnloaded")
	                .valueFlag(GenericArguments.bool(Text.of("c")), "c", "-allowcommands")
	                .valueFlag(GenericArguments.bool(Text.of("b")), "b", "-bonuschest")
	                .valueFlag(GenericArguments.bool(Text.of("f")), "f", "-mapfeatures").buildWith(GenericArguments.none()))
		    .executor(new CMDCreate())
		    .build();

	private CommandSpec cmdModifier = CommandSpec.builder()
		    .description(Text.of(" Allows you to Add or remove WorldGeneratorModifier's from the given world. This will have no effect on existing chunks only ungenerated chunks."))
		    .permission("pjw.cmd.world.modifier")	    
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.catalogedElement(Text.of("modifier"), WorldGeneratorModifier.class),
    				GenericArguments.flags().flag("r").buildWith(GenericArguments.none()))
		    .executor(new CMDModifier())
		    .build();
	
	private CommandSpec cmdRemove = CommandSpec.builder()
		    .description(Text.of(" Delete worlds you no longer need. Worlds must be unloaded before you can delete them"))
		    .permission("pjw.cmd.world.remove")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))))
		    .executor(new CMDRemove())
		    .build();

	private CommandSpec cmdDiffculty = CommandSpec.builder()
		    .description(Text.of(" Set the difficulty level for each world"))
		    .permission("pjw.cmd.world.difficulty")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.catalogedElement(Text.of("difficulty"), Difficulty.class)))
		    .executor(new CMDDifficulty())
		    .build();

	private CommandSpec cmdSetSpawn = CommandSpec.builder()
		    .description(Text.of(" Sets the spawn point of specified world. If no arguments present sets spawn of current world to player location"))
		    .permission("pjw.cmd.world.setspawn")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.seq(GenericArguments.world(Text.of("world")), GenericArguments.string(Text.of("x,y,z")))))
		    .executor(new CMDSetSpawn())
		    .build();
	
	private CommandSpec cmdHardcore = CommandSpec.builder()
		    .description(Text.of(" Toggle on and off hardcore mode for world"))
		    .permission("pjw.cmd.world.hardcore")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.bool(Text.of("true|false"))))
		    .executor(new CMDHardcore())
		    .build();

	private CommandSpec cmdTime = CommandSpec.builder()
		    .description(Text.of(" Set the time of world"))
		    .permission("pjw.cmd.world.time")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.longNum(Text.of("time"))))
		    .executor(new CMDTime())
		    .build();
	
	private CommandSpec cmdLoadOnStartup = CommandSpec.builder()
		    .description(Text.of(" Set whether to load world on startup"))
		    .permission("pjw.cmd.world.loadonstartup")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.bool(Text.of("true|false"))))
		    .executor(new CMDLoadOnStartup())
		    .build();
	
	private CommandSpec cmdWeather = CommandSpec.builder()
		    .description(Text.of(" Set the weather of world"))
		    .permission("pjw.cmd.world.weather")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.string(Text.of("clear|rain|thunder"))),
		    		GenericArguments.optional(GenericArguments.integer(Text.of("duration"))))
		    .executor(new CMDWeather())
		    .build();
	
	private CommandSpec cmdPvp = CommandSpec.builder()
		    .description(Text.of(" Toggle on and off pvp for world"))
		    .permission("pjw.cmd.world.pvp")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.bool(Text.of("true|false"))))
		    .executor(new CMDPvp())
		    .build();
	
	private CommandSpec cmdKeepSpawnLoaded = CommandSpec.builder()
		    .description(Text.of(" Keeps spawn point of world loaded in memory"))
		    .permission("pjw.cmd.world.keepspawnloaded")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.bool(Text.of("true|false"))))
		    .executor(new CMDKeepSpawnLoaded())
		    .build();

	private CommandSpec cmdUseMapFeatures = CommandSpec.builder()
		    .description(Text.of(" Keeps spawn point of world loaded in memory"))
		    .permission("pjw.cmd.world.usemapfeatures")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.bool(Text.of("true|false"))))
		    .executor(new CMDUseMapFeatures())
		    .build();
	
	private CommandSpec cmdProperties = CommandSpec.builder()
		    .description(Text.of(" View all properties associated with a world"))
		    .permission("pjw.cmd.world.properties")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))))
		    .executor(new CMDProperties())
		    .build();
	
	private CommandSpec cmdList = CommandSpec.builder()
		    .description(Text.of(" Lists all known worlds, loaded or unloaded"))
		    .permission("pjw.cmd.world.list")
		    .executor(new CMDList())
		    .build();
	
	private CommandSpec cmdTeleport = CommandSpec.builder()
		    .description(Text.of(" Teleport to specified world and location"))
		    .permission("pjw.cmd.world.teleport")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.flags()
    				.valueFlag(GenericArguments.string(Text.of("x,y,z")), "c")
    				.valueFlag(GenericArguments.string(Text.of("direction")), "d").flag("f").buildWith(GenericArguments.none()))
		    .executor(new CMDTeleport())
		    .build();
	
	private CommandSpec cmdCopy = CommandSpec.builder()
		    .description(Text.of(" Allows you to make a new world from an existing world"))
		    .permission("pjw.cmd.world.copy")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("srcWorld"))), 
		    		GenericArguments.optional(GenericArguments.string(Text.of("newWorld"))))
		    .executor(new CMDCopy())
		    .build();
	
	private CommandSpec cmdRename = CommandSpec.builder()
		    .description(Text.of(" Allows for renaming worlds. World must be unloaded before you can rename world"))
		    .permission("pjw.cmd.world.rename")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("srcWorld"))), 
		    		GenericArguments.optional(GenericArguments.string(Text.of("newWorld"))))
		    .executor(new CMDRename())
		    .build();
	
	private CommandSpec cmdUnload = CommandSpec.builder()
		    .description(Text.of(" Unloads specified world. If players are in world, they will be teleported to default spawn"))
		    .permission("pjw.cmd.world.unload")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))))
		    .executor(new CMDUnload())
		    .build();

	private CommandSpec cmdLoad = CommandSpec.builder()
		    .description(Text.of(" Loads specified world if exists"))
		    .permission("pjw.cmd.world.load")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))))
		    .executor(new CMDLoad())
		    .build();
	
	private CommandSpec cmdImport = CommandSpec.builder()
		    .description(Text.of(" Imports non sponge worlds, such as bukkit worlds"))
		    .permission("pjw.cmd.world.import")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.string(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.catalogedElement(Text.of("dimensionType"), DimensionType.class)),
		    		GenericArguments.optional(GenericArguments.catalogedElement(Text.of("generatorType"), GeneratorType.class)), 
		    		GenericArguments.allOf(GenericArguments.catalogedElement(Text.of("modifier"), WorldGeneratorModifier.class)))
		    .executor(new CMDImport())
		    .build();
	
	private CommandSpec cmdGamerule = CommandSpec.builder()
		    .description(Text.of(" Configure varies world properties"))
		    .permission("pjw.cmd.world.gamerule")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(new GameruleElement(Text.of("rule"))), 
		    		GenericArguments.optional(GenericArguments.string(Text.of("value"))))
		    .executor(new CMDGamerule())
		    .build();

	private CommandSpec cmdGamemode = CommandSpec.builder()
		    .description(Text.of(" Change gamemode of the specified world"))
		    .permission("pjw.cmd.world.gamemode")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(new GameModeElement(Text.of("gamemode"))))
		    .executor(new CMDGamemode())
		    .build();
	
	private CommandSpec cmdRegen = CommandSpec.builder()
		    .description(Text.of(" Regenerates a world. You can preserve the seed or generate new random"))
		    .permission("pjw.cmd.world.regen")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.bool(Text.of("true|false"))), 
		    		GenericArguments.optional(GenericArguments.string(Text.of("seed"))))
		    .executor(new CMDRegen())
		    .build();
	
	private CommandSpec cmdEnable = CommandSpec.builder()
		    .description(Text.of(" Enable and disable worlds from loading"))
		    .permission("pjw.cmd.world.enable")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.bool(Text.of("true|false"))))
		    .executor(new CMDEnable())
		    .build();
	
	private CommandSpec cmdFill = CommandSpec.builder()
		    .description(Text.of(" Pre generate chunks in a world"))
		    .permission("pjw.cmd.world.fill")
		    .arguments(element,
		    		GenericArguments.optional(GenericArguments.world(Text.of("world"))), 
		    		GenericArguments.optional(GenericArguments.string(Text.of("diameter"))), 
		    		GenericArguments.optional(GenericArguments.integer(Text.of("interval"))))
		    .executor(new CMDFill())
		    .build();
	
	public CommandSpec cmdWorld = CommandSpec.builder()
			.description(Text.of("Base command"))
			.permission("pjw.cmd.world")
			.child(cmdCreate, "create", "cr")
			.child(cmdRegen, "regen", "r")
			.child(cmdRemove, "remove", "rm")
			.child(cmdProperties, "properties", "pp")
			.child(cmdDiffculty, "difficulty", "df")
			.child(cmdSetSpawn, "setspawn", "s")
			.child(cmdHardcore, "hardcore", "h")
			.child(cmdTime, "time", "t")
			.child(cmdWeather, "weather", "w")
			.child(cmdPvp, "pvp", "p")
			.child(cmdKeepSpawnLoaded, "keepspawnloaded", "k")
			.child(cmdUseMapFeatures, "usemapfeatures", "u")
			.child(cmdLoadOnStartup, "loadonstartup", "los")
			.child(cmdList, "list", "ls")
			.child(cmdTeleport, "teleport", "tp")
			.child(cmdCopy, "copy", "cp")
			.child(cmdRename, "rename", "rn")
			.child(cmdUnload, "unload", "u")
			.child(cmdLoad, "load", "l")
			.child(cmdImport, "import", "i")
			.child(cmdEnable, "enable", "e")
			.child(cmdGamemode, "gamemode", "gm")
			.child(cmdGamerule, "gamerule", "gr")
			.child(cmdFill, "fill", "f")
			.child(cmdModifier, "modifier", "m")
			.executor(new CMDWorld())
			.build();
}
