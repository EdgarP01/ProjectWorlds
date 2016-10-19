package com.gmail.trentech.pjw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import com.gmail.trentech.helpme.Help;
import com.gmail.trentech.pjw.commands.CommandManager;
import com.gmail.trentech.pjw.extra.OceanWorldGeneratorModifier;
import com.gmail.trentech.pjw.io.Migrator;
import com.gmail.trentech.pjw.listeners.EventManager;
import com.gmail.trentech.pjw.listeners.TabEventManager;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Resource;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true), @Dependency(id = "helpme", version = "0.1.0", optional = true) })
public class Main {

	@Inject @ConfigDir(sharedRoot = false)
    private Path path;

	@Inject
	private Logger log;

	private static PluginContainer plugin;
	private static Main instance;
	
	@Listener
	public void onPreInitializationEvent(GamePreInitializationEvent event) {
		plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
		instance = this;
		
		try {			
			Files.createDirectories(path);		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		Sponge.getEventManager().registerListeners(this, new EventManager());
		Sponge.getEventManager().registerListeners(this, new TabEventManager());

		Sponge.getRegistry().register(WorldGeneratorModifier.class, new OceanWorldGeneratorModifier());

		Sponge.getCommandManager().register(this, new CommandManager().cmdWorld, "world", "w");

		ConfigManager.init();
		
		if (Sponge.getPluginManager().isLoaded("helpme")) {
			Help worldCopy = new Help("world copy", "copy", "Allows you to make a new world from an existing world")
					.setPermission("pjw.cmd.world.copy")
					.addUsage("/world copy <oldWorld> <newWorld>")
					.addUsage("/w cp <oldWorld> <newWorld>")
					.addExample("/world copy srcWorld newWorld");
			
			Help worldCreate = new Help("world create", "create", "Allows you to create new worlds with any combination of optional arguments -d " + "for dimension type, -g for generator type, -s for seed and -m for generator modifiers")
					.setPermission("pjw.cmd.world.create")
					.addUsage("/world create <world> [-d <dimensionType>] [-g <generatorType>] [-m <modifer>] [-s <seed>]")
					.addUsage("/w cr <world> [-d <dimensionType>] [-g <generatorType>] [-m <modifer>]  [-s <seed>]")
					.addExample("/world create NewWorld -d overworld -g overworld")
					.addExample("/world create NewWorld -d nether -m sponge:skylands")
					.addExample("/world create NewWorld -s -12309830198412353456");
			
			Help worldDelete = new Help("world remove", "remove", "Delete worlds you no longer need. Worlds must be unloaded before you can delete them")
					.setPermission("pjw.cmd.world.remove")
					.addUsage("/world remove <world>")
					.addUsage("/w remove <world>")
					.addUsage("/w rm <world>")
					.addExample("/world remove OldWorld");
			
			Help worldDifficulty = new Help("world difficulty", "difficulty", "Set the difficulty level for each world")
					.setPermission("pjw.cmd.world.difficulty")
					.addUsage("/world difficulty <world> [difficulty]")
					.addUsage("/w df <world> [difficulty]")
					.addExample("/world difficulty MyWorld HARD")
					.addExample("/world difficulty MyWorld");
			
			Help worldEnable = new Help("world enable", "enable", "Enable and disable worlds from loading")
					.setPermission("pjw.cmd.world.enable")
					.addUsage("/world enable <world> [boolean]")
					.addUsage("/w e <world> [boolean]")
					.addExample("/world enable MyWorld false")
					.addExample("/world enable MyWorld true");
			
			Help worldFill = new Help("world fill", "fill", "Pre generate chunks in a world outwards from center spawn")
					.setPermission("pjw.cmd.world.fill")
					.addUsage("/world fill <world> <diameter> [interval]")
					.addUsage("/w f <world> <diameter> [interval]")
					.addExample("/world fill MyWorld stop")
					.addExample("/world fill MyWorld 1000");
			
			Help worldGamemode = new Help("world gamemode", "gamemode", "Change gamemode of the specified world")
					.setPermission("pjw.cmd.world.gamemode")
					.addUsage("/world gamemode <world> [gamemode]")
					.addUsage("/w g <world> [gamemode]")
					.addExample("/world gamemode MyWorld SURVIVAL")
					.addExample("/world gamemode");
			
			Help worldGamerule = new Help("world gamerule", "gamerule", " Configure varies world properties")
					.setPermission("pjw.cmd.world.gamerule")
					.addUsage("/world gamerule <world> [rule] [value]")
					.addUsage("/w gr <world> [rule] [value]")
					.addExample("/world gamerule MyWorld mobGriefing false")
					.addExample("/world gamerule MyWorld");
			
			Help worldHardcore = new Help("world hardcore", "hardcore", "Toggle on and off hardcore mode for world")
					.setPermission("pjw.cmd.world.hardcore")
					.addUsage("/world hardcore <world> [value]")
					.addUsage("/w h <world> [value]")
					.addExample("/world hardcore MyWorld false")
					.addExample("/world hardcore MyWorld");
			
			Help worldImport = new Help("world import", "import", "Import worlds not native to Sponge")
					.setPermission("pjw.cmd.world.import")
					.addUsage("/world import <world> <type> <generator>")
					.addUsage("/w i <world> <type> <generator>")
					.addExample("/world import NewWorld overworld overworld");
			
			Help worldKeepSpawnLoaded = new Help("world keepspawnloaded", "keepspawnloaded", "Keeps spawn point of world loaded in memory")
					.setPermission("pjw.cmd.world.keepspawnloaded")
					.addUsage("/world keepspawnloaded <world> [value]")
					.addUsage("/w k <world> [value]")
					.addExample("/world keepspawnloaded MyWorld true")
					.addExample("/world keepspawnloaded MyWorld");
			
			Help worldList = new Help("world list", "list", "Lists all known worlds, loaded or unloaded")
					.setPermission("pjw.cmd.world.list")
					.addUsage("/world list")
					.addUsage("/w ls");
			
			Help worldLoad = new Help("world load", "load", "Loads sepcified world. If world is a non Sponge created world you will need to specify a dimension type to import")
					.setPermission("pjw.cmd.world.load")
					.addUsage("/world load <world> [type]")
					.addUsage("/w l <world> [type]")
					.addExample("/world load BukkitWorld overworld")
					.addExample("/world load NewWorld");
			
			Help worldProperties = new Help("world properties", "properties", "View all properties associated with a world")
					.setPermission("pjw.cmd.world.properties")
					.addUsage("/world properties <world>")
					.addUsage("/w pp <world>")
					.addExample("/world properties MyWorld")
					.addExample("/world properties");
			
			Help worldPvp = new Help("world pvp", "pvp", "Toggle on and off pvp for world")
					.setPermission("pjw.cmd.world.pvp")
					.addUsage("/world pvp <world> [value]")
					.addUsage("/w p <world> [value]")
					.addExample("/world pvp MyWorld true")
					.addExample("/world pvp MyWorld");
			
			Help worldRegen = new Help("world regen", "regen", "Regenerates a world. You can preserve the seed or generate new random")
					.setPermission("pjw.cmd.world.regen")
					.addUsage("/world regen <world> [true/false]")
					.addUsage("/w r <world>  [true/false]")
					.addExample("/world regen MyWorld true")
					.addExample("/world regen MyWorld");
			
			Help worldRename = new Help("world rename", "rename", "Allows for renaming worlds. World must be unloaded before you can rename world")
					.setPermission("pjw.cmd.world.rename")
					.addUsage("/world rename <world> <world>")
					.addUsage("/w rn <world> <world>")
					.addExample("/world rename MyWorld NewWorldName");
			
			Help worldSetSpawn = new Help("world setspawn", "setspawn", "Sets the spawn point of specified world. If no arguments present sets spawn of current world to player location")
					.setPermission("pjw.cmd.world.setspawn")
					.addUsage("/world setspawn [<world> [x,y,z]]")
					.addUsage("/w s <world> <x,y,z>")
					.addExample("/world setspawn MyWorld -153,75,300")
					.addExample("/world setspawn");
			
			Help worldTeleport = new Help("world teleport", "teleport", "Teleport to specified world and location")
					.setPermission("pjw.cmd.world.teleport")
					.addUsage("/world teleport <world> [-c <x,y,z>] [-d <direction>]")
					.addUsage("/w tp <world> [-c <x,y,z>] [-d <direction>]")
					.addExample("/world tp MyWorld -c -153,75,300 -d WEST")
					.addExample("/world teleport MyWorld -c -153,75,300")
					.addExample("/world tp MyWorld");
			
			Help worldUnload = new Help("world unload", "unload", "Unloads specified world. If players are in world, they will be teleported to default spawn")
					.setPermission("pjw.cmd.world.unload")
					.addUsage("/world unload <world>")
					.addUsage("/w u <world>")
					.addExample("/world unload MyWorld");
			
			Help world = new Help("world", "world", "Base Project Worlds command")
					.setPermission("pjw.cmd.world")
					.addChild(worldUnload)
					.addChild(worldTeleport)
					.addChild(worldSetSpawn)
					.addChild(worldRename)
					.addChild(worldRegen)
					.addChild(worldPvp)
					.addChild(worldProperties)
					.addChild(worldLoad)
					.addChild(worldList)
					.addChild(worldKeepSpawnLoaded)
					.addChild(worldImport)
					.addChild(worldHardcore)
					.addChild(worldGamerule)
					.addChild(worldGamemode)
					.addChild(worldFill)
					.addChild(worldEnable)
					.addChild(worldDifficulty)
					.addChild(worldDelete)
					.addChild(worldCreate)
					.addChild(worldCopy);

			Help.register(world);
		}
	}

	@Listener
	public void onAboutToStartServer(GameAboutToStartServerEvent event) {
		if (new File(Sponge.getServer().getDefaultWorldName()).exists()) {
			Migrator.init();
		}
	}

	public Logger getLog() {
		return log;
	}

	public Path getPath() {
		return path;
	}
	
	public static PluginContainer getPlugin() {
		return plugin;
	}
	
	public static Main instance() {
		return instance;
	}
}