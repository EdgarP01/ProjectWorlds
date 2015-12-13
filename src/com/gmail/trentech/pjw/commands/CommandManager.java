package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Texts;

public class CommandManager {

	public CommandSpec cmdCreate = CommandSpec.builder()
		    .description(Texts.of("Create new world"))
		    .permission("pjw.cmd.world.create")	    
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))),
		    		GenericArguments.optional(GenericArguments.string(Texts.of("arg0"))),
		    		GenericArguments.optional(GenericArguments.string(Texts.of("arg1"))),
		    		GenericArguments.optional(GenericArguments.string(Texts.of("arg2"))),
		    		GenericArguments.optional(GenericArguments.string(Texts.of("arg3"))))
		    .executor(new CMDCreate())
		    .build();
	
	public CommandSpec cmdDelete = CommandSpec.builder()
		    .description(Texts.of("delete world"))
		    .permission("pjw.cmd.world.delete")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("name"))))
		    .executor(new CMDDelete())
		    .build();
	
	public CommandSpec cmdPortal = CommandSpec.builder()
		    .description(Texts.of("create world portal"))
		    .permission("pjw.cmd.world.portal")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))), GenericArguments.optional(GenericArguments.string(Texts.of("point1"))),
		    		GenericArguments.optional(GenericArguments.string(Texts.of("point2"))), GenericArguments.optional(GenericArguments.string(Texts.of("block"))), GenericArguments.optional(GenericArguments.string(Texts.of("world"))))
		    .executor(new CMDPortal())
		    .build();
	
	public CommandSpec cmdDiffculty = CommandSpec.builder()
		    .description(Texts.of("set difficulty of world"))
		    .permission("pjw.cmd.world.difficulty")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))), GenericArguments.optional(GenericArguments.string(Texts.of("value"))))
		    .executor(new CMDDifficulty())
		    .build();
	
	public CommandSpec cmdGamemode = CommandSpec.builder()
		    .description(Texts.of("set gamemode of world"))
		    .permission("pjw.cmd.world.gamemode")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))), GenericArguments.optional(GenericArguments.string(Texts.of("value"))))
		    .executor(new CMDGamemode())
		    .build();
	
	public CommandSpec cmdSetSpawn = CommandSpec.builder()
		    .description(Texts.of("set spawn of world"))
		    .permission("pjw.cmd.world.setspawn")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("value"))))
		    .executor(new CMDSetSpawn())
		    .build();
	
	public CommandSpec cmdHardcore = CommandSpec.builder()
		    .description(Texts.of("toggle hardcore of world"))
		    .permission("pjw.cmd.world.hardcore")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("value"))))
		    .executor(new CMDHardcore())
		    .build();
	
	public CommandSpec cmdRespawn = CommandSpec.builder()
		    .description(Texts.of("sets default respawn world"))
		    .permission("pjw.cmd.world.respawn")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("name"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("value"))))
		    .executor(new CMDRespawn())
		    .build();
	
	public CommandSpec cmdKeepSpawnLoaded = CommandSpec.builder()
		    .description(Texts.of("toggle keep world spawn loaded"))
		    .permission("pjw.cmd.world.keeploaded")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("value"))))
		    .executor(new CMDKeepSpawnLoaded())
		    .build();
	
	public CommandSpec cmdLockTime = CommandSpec.builder()
		    .description(Texts.of("toggle lock time of world"))
		    .permission("pjw.cmd.world.time")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("value"))))
		    .executor(new CMDLockTime())
		    .build();
	
	public CommandSpec cmdLockWeather = CommandSpec.builder()
		    .description(Texts.of("toggle lock weather of world"))
		    .permission("pjw.cmd.world.weather")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("value"))))
		    .executor(new CMDLockWeather())
		    .build();
	
	public CommandSpec cmdProperties = CommandSpec.builder()
		    .description(Texts.of("view world properties"))
		    .permission("pjw.cmd.world.properties")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("name"))))
		    .executor(new CMDProperties())
		    .build();
	
	public CommandSpec cmdList = CommandSpec.builder()
		    .description(Texts.of("list all worlds"))
		    .permission("pjw.cmd.world.list")
		    .executor(new CMDList())
		    .build();
	
	public CommandSpec cmdTeleport = CommandSpec.builder()
		    .description(Texts.of("Teleport to world"))
		    .permission("pjw.cmd.world.teleport")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("arg0"))), GenericArguments.optional(GenericArguments.string(Texts.of("arg1"))), GenericArguments.optional(GenericArguments.string(Texts.of("arg1"))))
		    .executor(new CMDTeleport())
		    .build();
	
	public CommandSpec cmdPvp = CommandSpec.builder()
		    .description(Texts.of("Toggle PVP for world"))
		    .permission("pjw.cmd.world.pvp")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("value"))))
		    .executor(new CMDPvp())
		    .build();
	
	public CommandSpec cmdCopy = CommandSpec.builder()
		    .description(Texts.of("Copy a world"))
		    .permission("pjw.cmd.world.copy")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("old"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("new"))))
		    .executor(new CMDCopy())
		    .build();
	
	public CommandSpec cmdRename = CommandSpec.builder()
		    .description(Texts.of("Rename a world"))
		    .permission("pjw.cmd.world.rename")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("old"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("new"))))
		    .executor(new CMDRename())
		    .build();
	
	public CommandSpec cmdUnload = CommandSpec.builder()
		    .description(Texts.of("unload a world"))
		    .permission("pjw.cmd.world.unload")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("name"))))
		    .executor(new CMDUnload())
		    .build();

	public CommandSpec cmdLoad = CommandSpec.builder()
		    .description(Texts.of("load a world"))
		    .permission("pjw.cmd.world.load")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("name"))))
		    .executor(new CMDLoad())
		    .build();
	
	public CommandSpec cmdGamerule = CommandSpec.builder()
		    .description(Texts.of("edit gamerules of world"))
		    .permission("pjw.cmd.world.gamerule")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))), GenericArguments.optional(GenericArguments.string(Texts.of("rule"))), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("value"))))
		    .executor(new CMDGamerule())
		    .build();
	
	public CommandSpec cmdButton = CommandSpec.builder()
		    .description(Texts.of("place a teleport button"))
		    .permission("pjw.cmd.world.button")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("name"))))
		    .executor(new CMDButton())
		    .build();
	
	public CommandSpec cmdPlate = CommandSpec.builder()
		    .description(Texts.of("place a teleport pressure plate"))
		    .permission("pjw.cmd.world.plate")
		    .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Texts.of("name"))))
		    .executor(new CMDPlate())
		    .build();
	
	public CommandSpec cmdYes = CommandSpec.builder()
		    .executor(new CMDYes())
		    .build();
	
	public CommandSpec cmdWorld = CommandSpec.builder()
			.description(Texts.of("Base command"))
			.permission("pjw.cmd.world")
			.child(cmdCreate, "create", "cr")
			.child(cmdDelete, "delete", "del")
			.child(cmdProperties, "properties", "prop")
			.child(cmdPortal, "portal", "p")
			.child(cmdDiffculty, "difficulty", "diff")
			.child(cmdGamemode, "gamemode", "gm")
			.child(cmdSetSpawn, "setspawn", "spawn", "ss")
			.child(cmdHardcore, "hardcore", "h")
			.child(cmdRespawn, "respawn", "rs")
			.child(cmdKeepSpawnLoaded, "keepspawnloaded", "keep", "ksl")
			.child(cmdLockTime, "locktime", "time", "lt")
			.child(cmdLockWeather, "lockweather", "weather",  "lw")
			.child(cmdList, "list", "l")
			.child(cmdTeleport, "teleport", "tp")
			.child(cmdPvp, "pvp")
			.child(cmdCopy, "copy", "cp")
			.child(cmdRename, "rename", "rn")
			.child(cmdUnload, "unload", "u")
			.child(cmdLoad, "load", "ld")
			.child(cmdGamerule, "gamerule", "gr")
			.child(cmdButton, "button", "b")
			.child(cmdPlate, "plate", "pl")
			.executor(new CMDWorld())
			.build();
}
