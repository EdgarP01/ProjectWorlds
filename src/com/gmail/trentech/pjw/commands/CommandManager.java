package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandManager {

	public CommandSpec cmdCreate = CommandSpec.builder()
		    .description(Text.of(" Allows you to create new worlds with any combination of optional arguments D: for dimension type, G: for generator type, S: for seed and M: for generator modifiers."))
		    .permission("pjw.cmd.world.create")	    
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name")))
		    		,GenericArguments.optional(GenericArguments.string(Text.of("arg0")))
		    		,GenericArguments.optional(GenericArguments.string(Text.of("arg1")))
		    		,GenericArguments.optional(GenericArguments.string(Text.of("arg2")))
		    		,GenericArguments.optional(GenericArguments.string(Text.of("arg3"))))
		    .executor(new CMDCreate())
		    .build();

	public CommandSpec cmdDelete = CommandSpec.builder()
		    .description(Text.of(" Delete worlds you no longer need. Worlds must be unloaded before you can delete them"))
		    .permission("pjw.cmd.world.delete")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("name"))))
		    .executor(new CMDDelete())
		    .build();

	public CommandSpec cmdDiffculty = CommandSpec.builder()
		    .description(Text.of(" Set the difficulty level for each world"))
		    .permission("pjw.cmd.world.difficulty")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("value"))))
		    .executor(new CMDDifficulty())
		    .build();

	public CommandSpec cmdSetSpawn = CommandSpec.builder()
		    .description(Text.of(" Sets the spawn point of specified world. If no arguments present sets spawn of current world to player location"))
		    .permission("pjw.cmd.world.setspawn")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("value"))))
		    .executor(new CMDSetSpawn())
		    .build();
	
	public CommandSpec cmdHardcore = CommandSpec.builder()
		    .description(Text.of(" Toggle on and off hardcore mode for world"))
		    .permission("pjw.cmd.world.hardcore")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("value"))))
		    .executor(new CMDHardcore())
		    .build();

	public CommandSpec cmdPvp = CommandSpec.builder()
		    .description(Text.of(" Toggle on and off pvp for world"))
		    .permission("pjw.cmd.world.pvp")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("value"))))
		    .executor(new CMDPvp())
		    .build();
	
	public CommandSpec cmdKeepSpawnLoaded = CommandSpec.builder()
		    .description(Text.of(" Keeps spawn point of world loaded in memory"))
		    .permission("pjw.cmd.world.keeploaded")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("value"))))
		    .executor(new CMDKeepSpawnLoaded())
		    .build();

	public CommandSpec cmdProperties = CommandSpec.builder()
		    .description(Text.of(" View all properties associated with a world"))
		    .permission("pjw.cmd.world.properties")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("name"))))
		    .executor(new CMDProperties())
		    .build();
	
	public CommandSpec cmdList = CommandSpec.builder()
		    .description(Text.of(" Lists all known worlds, loaded or unloaded"))
		    .permission("pjw.cmd.world.list")
		    .executor(new CMDList())
		    .build();
	
	public CommandSpec cmdTeleport = CommandSpec.builder()
		    .description(Text.of(" Teleport self or others to specified world and location"))
		    .permission("pjw.cmd.world.teleport")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("arg0"))), GenericArguments.optional(GenericArguments.string(Text.of("arg1")))
		    		,GenericArguments.optional(GenericArguments.string(Text.of("arg1"))))
		    .executor(new CMDTeleport())
		    .build();

	public CommandSpec cmdCopy = CommandSpec.builder()
		    .description(Text.of(" Allows you to make a new world from an existing world"))
		    .permission("pjw.cmd.world.copy")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("old"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("new"))))
		    .executor(new CMDCopy())
		    .build();
	
	public CommandSpec cmdRename = CommandSpec.builder()
		    .description(Text.of(" Allows for renaming worlds. World must be unloaded before you can rename world"))
		    .permission("pjw.cmd.world.rename")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("old"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("new"))))
		    .executor(new CMDRename())
		    .build();
	
	public CommandSpec cmdUnload = CommandSpec.builder()
		    .description(Text.of(" Unloads specified world. If players are in world, they will be teleported to default spawn"))
		    .permission("pjw.cmd.world.unload")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("name"))))
		    .executor(new CMDUnload())
		    .build();

	public CommandSpec cmdLoad = CommandSpec.builder()
		    .description(Text.of(" Loads specified world if exists"))
		    .permission("pjw.cmd.world.load")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("type"))))
		    .executor(new CMDLoad())
		    .build();
	
	public CommandSpec cmdGamerule = CommandSpec.builder()
		    .description(Text.of(" Configure varies world properties"))
		    .permission("pjw.cmd.world.gamerule")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name")))
		    		,GenericArguments.optional(GenericArguments.string(Text.of("rule")))
		    		,GenericArguments.optional(GenericArguments.string(Text.of("value"))))
		    .executor(new CMDGamerule())
		    .build();

	public CommandSpec cmdHelp = CommandSpec.builder()
		    .description(Text.of(" I need help with Project Worlds"))
		    .permission("pjw.cmd.world")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("command"))))
		    .executor(new CMDHelp())
		    .build();
	
	public CommandSpec cmdGamemode = CommandSpec.builder()
		    .description(Text.of(" Change gamemode of the specified world"))
		    .permission("pjw.cmd.world.gamemode")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("value"))))
		    .executor(new CMDGamemode())
		    .build();
	
	public CommandSpec cmdRegen = CommandSpec.builder()
		    .description(Text.of(" Regenerates a world. You can preserve the seed or generate new random"))
		    .permission("pjw.cmd.world.regen")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("value"))))
		    .executor(new CMDRegen())
		    .build();
	
	public CommandSpec cmdEnable = CommandSpec.builder()
		    .description(Text.of(" Enable and disable worlds from loading"))
		    .permission("pjw.cmd.world.enable")
		    .arguments(GenericArguments.optional(GenericArguments.string(Text.of("name"))), GenericArguments.optional(GenericArguments.string(Text.of("value"))))
		    .executor(new CMDEnable())
		    .build();
	
	public CommandSpec cmdWorld = CommandSpec.builder()
			.description(Text.of("Base command"))
			.permission("pjw.cmd.world")
			.child(cmdCreate, "create", "cr")
			.child(cmdRegen, "regen", "r")
			.child(cmdDelete, "delete", "dl")
			.child(cmdProperties, "properties", "p")
			.child(cmdDiffculty, "difficulty", "df")
			.child(cmdSetSpawn, "setspawn", "s")
			.child(cmdHardcore, "hardcore", "h")
			.child(cmdPvp, "pvp")
			.child(cmdKeepSpawnLoaded, "keepspawnloaded", "k")
			.child(cmdList, "list", "ls")
			.child(cmdTeleport, "teleport", "tp")
			.child(cmdCopy, "copy", "cp")
			.child(cmdRename, "rename", "rn")
			.child(cmdUnload, "unload", "u")
			.child(cmdLoad, "load", "l")
			.child(cmdEnable, "enable", "e")
			.child(cmdHelp, "help")
			.child(cmdGamemode, "gamemode", "gm")
			.executor(new CMDWorld())
			.build();
}
