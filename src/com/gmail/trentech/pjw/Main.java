package com.gmail.trentech.pjw;

import java.util.Map.Entry;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.extra.skylands.SkylandsWorldGeneratorModifier;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.commands.CommandManager;
import com.gmail.trentech.pjw.listeners.EventManager;
import com.gmail.trentech.pjw.modifiers.Modifiers;
import com.gmail.trentech.pjw.modifiers.voidd.VoidWorldGeneratorModifier;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Resource;
import com.gmail.trentech.pjw.utils.Tasks;

@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION)
public class Main {

	private static Game game;
	private static Logger log;	
	private static PluginContainer plugin;

	@Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
		game = Sponge.getGame();
		plugin = getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = getGame().getPluginManager().getLogger(plugin);
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
    	getGame().getEventManager().registerListeners(this, new EventManager());
    	//getGame().getEventManager().registerListeners(this, new SignEventManager());
    	//getGame().getEventManager().registerListeners(this, new PortalEventManager());
    	//getGame().getEventManager().registerListeners(this, new ButtonEventManager());
    	//getGame().getEventManager().registerListeners(this, new PlateEventManager());
    	
    	getGame().getCommandManager().register(this, new CommandManager().cmdWorld, "world", "w");
    	getGame().getCommandManager().register(this, new CommandManager().cmdGamerule, "gamerule", "gr");
    	
    	Modifiers.put("SKY",  new SkylandsWorldGeneratorModifier());
    	Modifiers.put("VOID",  new VoidWorldGeneratorModifier());
    	
    	for(Entry<String, WorldGeneratorModifier> entry : Modifiers.getAll().entrySet()){
    		getGame().getRegistry().registerWorldGeneratorModifier(entry.getValue());
    	}
    }

    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
    	getLog().info("Initializing...");

    	new ConfigManager();

    	loadWorlds();
    	
    	new Tasks().start();
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
}