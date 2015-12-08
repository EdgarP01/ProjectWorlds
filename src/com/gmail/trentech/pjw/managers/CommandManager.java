package com.gmail.trentech.pjw.managers;

import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Texts;

import com.gmail.trentech.pjw.commands.CMDCreate;
import com.gmail.trentech.pjw.commands.CMDDelete;
import com.gmail.trentech.pjw.commands.CMDDifficulty;
import com.gmail.trentech.pjw.commands.CMDGamemode;
import com.gmail.trentech.pjw.commands.CMDHardcore;
import com.gmail.trentech.pjw.commands.CMDKeepSpawnLoaded;
import com.gmail.trentech.pjw.commands.CMDLockTime;
import com.gmail.trentech.pjw.commands.CMDLockWeather;
import com.gmail.trentech.pjw.commands.CMDPortal;
import com.gmail.trentech.pjw.commands.CMDProperties;
import com.gmail.trentech.pjw.commands.CMDRespawn;
import com.gmail.trentech.pjw.commands.CMDSetSpawn;
import com.gmail.trentech.pjw.commands.CMDWorld;

public class CommandManager {

	public CommandSpec cmdCreate = CommandSpec.builder()
		    .description(Texts.of("Create new world"))
		    .permission("pjw.cmd.world.create")	    
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))),
		    		GenericArguments.optional(GenericArguments.string(Texts.of("type"))),
		    		GenericArguments.optional(GenericArguments.string(Texts.of("generator"))),
		    		GenericArguments.optional(GenericArguments.string(Texts.of("seed"))))
		    .executor(new CMDCreate())
		    .build();
	
	public CommandSpec cmdDelete = CommandSpec.builder()
		    .description(Texts.of("delete world"))
		    .permission("pjw.cmd.world.delete")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))))
		    .executor(new CMDDelete())
		    .build();
	
	public CommandSpec cmdPortal = CommandSpec.builder()
		    .description(Texts.of("create world portal"))
		    .permission("pjw.cmd.world.portal")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))))
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
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))))
		    .executor(new CMDHardcore())
		    .build();
	
	public CommandSpec cmdRespawn = CommandSpec.builder()
		    .description(Texts.of("sets default respawn world"))
		    .permission("pjw.cmd.world.respawn")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))))
		    .executor(new CMDRespawn())
		    .build();
	
	public CommandSpec cmdKeepSpawnLoaded = CommandSpec.builder()
		    .description(Texts.of("toggle keep world spawn loaded"))
		    .permission("pjw.cmd.world.keeploaded")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))))
		    .executor(new CMDKeepSpawnLoaded())
		    .build();
	
	public CommandSpec cmdLockTime = CommandSpec.builder()
		    .description(Texts.of("toggle lock time of world"))
		    .permission("pjw.cmd.world.time")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))))
		    .executor(new CMDLockTime())
		    .build();
	
	public CommandSpec cmdLockWeather = CommandSpec.builder()
		    .description(Texts.of("toggle lock weather of world"))
		    .permission("pjw.cmd.world.weather")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))))
		    .executor(new CMDLockWeather())
		    .build();
	
	public CommandSpec cmdProperties= CommandSpec.builder()
		    .description(Texts.of("view world properties"))
		    .permission("pjw.cmd.world.properties")
		    .arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))))
		    .executor(new CMDProperties())
		    .build();

	public CommandSpec cmdWorld = CommandSpec.builder()
			.description(Texts.of("Base command"))
			.permission("pjw.cmd.world")
			.arguments(GenericArguments.optional(GenericArguments.string(Texts.of("name"))))
			.child(cmdCreate, "create", "cr")
			.child(cmdDelete, "delete", "del")
			.child(cmdProperties, "properties", "prop")
			.child(cmdPortal, "portal", "p")
			.child(cmdDiffculty, "difficulty", "diff")
			.child(cmdGamemode, "gamemode", "gm")
			.child(cmdSetSpawn, "setspawn", "spawn", "ss")
			.child(cmdHardcore, "hardcore", "h")
			.child(cmdRespawn, "respawn", "rs")
			.child(cmdKeepSpawnLoaded, "keepspawnloaded", "ksl")
			.child(cmdLockTime, "locktime", "lt")
			.child(cmdLockWeather, "lockweather", "lw")
			.executor(new CMDWorld())
			.build();
}
