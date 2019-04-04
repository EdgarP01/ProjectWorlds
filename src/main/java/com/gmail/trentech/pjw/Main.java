package com.gmail.trentech.pjw;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjw.commands.CommandWorld;
import com.gmail.trentech.pjw.extra.OceanWorldGeneratorModifier;
import com.gmail.trentech.pjw.init.Common;
import com.gmail.trentech.pjw.io.Migrator;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.listeners.EventManager;
import com.gmail.trentech.pjw.utils.Resource;
import com.google.inject.Inject;

import ninja.leaping.configurate.ConfigurationNode;

@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "pjc", optional = false) })
public class Main {

	@Inject @ConfigDir(sharedRoot = false)
    private Path path;

	@Inject
	private Logger log;

	private static PluginContainer plugin;
	private static Main instance;
	
	@Listener
	public void onGameConstructionEvent(GameConstructionEvent event) {
		plugin = Sponge.getPluginManager().getPlugin(Resource.ID).get();
		instance = this;

		try {			
			Files.createDirectories(path);		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Common.initConfig();

		ConfigurationNode node = ConfigManager.get(Main.getPlugin()).getConfig().getNode("worlds");
		
		if(!node.isVirtual()) {
			for (final Map.Entry<Object, ? extends ConfigurationNode> child : node.getChildrenMap().entrySet()) {
				SpongeData.ids.put(child.getKey().toString(), child.getValue().getInt());
			}
		}

		Migrator.init();
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		Sponge.getEventManager().registerListeners(this, new EventManager());

		Common.initHelp();
		
		Sponge.getCommandManager().register(this, new CommandWorld().getCommandSpec(), "world", "w");
	}

	@Listener
	public void onRegister(GameRegistryEvent.Register<WorldGeneratorModifier> event) {
		event.register(new OceanWorldGeneratorModifier());
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