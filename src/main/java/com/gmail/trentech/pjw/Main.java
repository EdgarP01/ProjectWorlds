package com.gmail.trentech.pjw;

import java.util.HashMap;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import com.gmail.trentech.pjw.commands.CommandManager;
import com.gmail.trentech.pjw.extra.VoidWorldGeneratorModifier;
import com.gmail.trentech.pjw.listeners.EventManager;
import com.gmail.trentech.pjw.utils.Resource;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;
import net.minecrell.mcstats.SpongeStatsLite;

@Updatifier(repoName = "ProjectWorlds", repoOwner = "TrenTech", version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, authors = Resource.AUTHOR, url = Resource.URL, description = Resource.DESCRIPTION, dependencies = {@Dependency(id = "Updatifier", optional = true)})
public class Main {

    @Inject
    private SpongeStatsLite stats;
    
	private static Game game;
	private static Logger log;	
	private static PluginContainer plugin;

	private static HashMap<String, WorldGeneratorModifier> modifiers = new HashMap<>();
	
	@Listener
    public void onPreInitialization(GamePreInitializationEvent event) {

		
		game = Sponge.getGame();
		plugin = getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = getPlugin().getLogger();
		
		if(this.stats.start()) {
			getLog().info("MCStats started.");
		}else{
			getLog().warn("Could not start MCStats. This could be due to server opt-out, or error.");
		}
    }
	
    @Listener
    public void onInitialization(GameInitializationEvent event) {   	
    	getGame().getEventManager().registerListeners(this, new EventManager());

    	getGame().getRegistry().register(WorldGeneratorModifier.class, new VoidWorldGeneratorModifier());
    	
    	for(WorldGeneratorModifier modifier : getGame().getRegistry().getAllOf(WorldGeneratorModifier.class)) {
    		getModifiers().put(modifier.getId(), modifier);
    	}

    	getGame().getCommandManager().register(this, new CommandManager().cmdWorld, "world", "w");
    	getGame().getCommandManager().register(this, new CommandManager().cmdGamerule, "gamerule", "gr");
    }

    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
    	//loadWorlds();
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

//	private void loadWorlds() {
//		for(WorldProperties world : getGame().getServer().getUnloadedWorlds()) {
//			// Temporarily disable enable check until issue is resolved
//			//if(world.isEnabled()) {
//				getLog().info("Loading " + world.getWorldName());
//				Optional<World> load = getGame().getServer().loadWorld(world);
//				if(load.isPresent()) {
//					getLog().info("Loaded " + world.getWorldName());
//				}else{
//					getLog().warn("Failed to load " + world.getWorldName());
//				}
//			//}
//		}
//	}
}