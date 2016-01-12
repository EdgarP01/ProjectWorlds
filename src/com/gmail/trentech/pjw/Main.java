package com.gmail.trentech.pjw;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.commands.CommandManager;
import com.gmail.trentech.pjw.extra.VoidWorldGeneratorModifier;
import com.gmail.trentech.pjw.listeners.EventManager;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Resource;

import ninja.leaping.configurate.ConfigurationNode;

@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION)
public class Main {

	private static Game game;
	private static Logger log;	
	private static PluginContainer plugin;

	private static HashMap<String, WorldGeneratorModifier> modifiers = new HashMap<>();
	
	@Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
		game = Sponge.getGame();
		plugin = getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = getGame().getPluginManager().getLogger(plugin);
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
    	fixPath();
    	
    	getGame().getEventManager().registerListeners(this, new EventManager());

    	getGame().getRegistry().register(WorldGeneratorModifier.class, new VoidWorldGeneratorModifier());
    	
    	for(WorldGeneratorModifier modifier : getGame().getRegistry().getAllOf(WorldGeneratorModifier.class)){
    		getModifiers().put(modifier.getId(), modifier);
    	}
    	
    	ConfigurationNode config = new ConfigManager().getConfig();
    	
    	getGame().getCommandManager().register(this, new CommandManager().cmdWorld, "world", config.getNode("Options", "Command-Alias", "world").getString());
    	getGame().getCommandManager().register(this, new CommandManager().cmdGamerule, "gamerule", config.getNode("Options", "Command-Alias", "gamerule").getString());
    }

    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
    	getLog().info("Initializing...");

    	loadWorlds();
    }

    public static Logger getLog() {
        return log;
    }
    
	public static Game getGame() {
		return game;
	}

	public static PluginContainer getPlugin() {
		return plugin;
	}

	public static HashMap<String, WorldGeneratorModifier> getModifiers() {
		return modifiers;
	}

	private void loadWorlds(){
		getLog().info("Loading worlds...");
		for(WorldProperties world : getGame().getServer().getUnloadedWorlds()){
			Optional<World> load = getGame().getServer().loadWorld(world);
			if(load.isPresent()){
				getLog().info("Loaded " + world.getWorldName());
			}else{
				getLog().warn("Failed to load " + world.getWorldName());
			}
		}
	}
	
	private void fixPath(){
		File directory = new File("config", "Project Worlds");
		if(directory.exists()){
			File newDirectory = new File("config", "projectworlds");
			directory.renameTo(newDirectory);
		}
	}
}