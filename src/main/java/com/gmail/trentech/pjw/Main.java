package com.gmail.trentech.pjw;

import java.util.HashMap;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import com.gmail.trentech.pjw.commands.CommandManager;
import com.gmail.trentech.pjw.extra.VoidWorldGeneratorModifier;
import com.gmail.trentech.pjw.io.Migrator;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.listeners.EventManager;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Resource;

import me.flibio.updatifier.Updatifier;

@Updatifier(repoName = "ProjectWorlds", repoOwner = "TrenTech", version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, authors = Resource.AUTHOR, url = Resource.URL, description = Resource.DESCRIPTION, dependencies = {@Dependency(id = "Updatifier", optional = true)})
public class Main {

	private static Game game;
	private static Logger log;	
	private static PluginContainer plugin;

	private static HashMap<String, WorldGeneratorModifier> modifiers = new HashMap<>();
	
	@Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
		game = Sponge.getGame();
		plugin = getGame().getPluginManager().getPlugin(Resource.ID).get();
		log = getPlugin().getLogger();
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
    	
    	new ConfigManager().init();
    }

    @Listener
    public void onAboutToStartServer(GameAboutToStartServerEvent event) {
    	if(getGame().getPlatform().getType().equals(Platform.Type.SERVER)) {
        	SpongeData.init();
        	Migrator.init();
    	}
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
}