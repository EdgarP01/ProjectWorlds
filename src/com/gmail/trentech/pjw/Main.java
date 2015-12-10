package com.gmail.trentech.pjw;

import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.commands.CommandManager;
import com.gmail.trentech.pjw.events.EventManager;
import com.gmail.trentech.pjw.events.PortalEventManager;
import com.gmail.trentech.pjw.events.SignEventManager;
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
		game = event.getGame();
		plugin = event.getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = event.getGame().getPluginManager().getLogger(plugin);
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
    	getGame().getEventManager().registerListeners(this, new EventManager());
    	getGame().getEventManager().registerListeners(this, new SignEventManager());
    	getGame().getEventManager().registerListeners(this, new PortalEventManager());
    	getGame().getCommandManager().register(this, new CommandManager().cmdWorld, "world", "w");
    }

    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
    	getLog().info("Initializing...");
    	
    	new ConfigManager();
    	new ConfigManager("worlds.conf");

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
//		Map<Object, ? extends CommentedConfigurationNode> worlds = new ConfigManager().getConfig().getNode("Worlds").getChildrenMap();
//		for(Entry<Object, ? extends CommentedConfigurationNode> item : worlds.entrySet()){
//			String key = item.getKey().toString();
//			getGame().getServer().loadWorld(key);
//		}
	}
}