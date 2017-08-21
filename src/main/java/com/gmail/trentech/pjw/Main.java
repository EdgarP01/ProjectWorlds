package com.gmail.trentech.pjw;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import com.gmail.trentech.pjw.extra.OceanWorldGeneratorModifier;
import com.gmail.trentech.pjw.init.Commands;
import com.gmail.trentech.pjw.init.Common;
import com.gmail.trentech.pjw.io.Migrator;
import com.gmail.trentech.pjw.listeners.EventManager;
import com.gmail.trentech.pjw.utils.Resource;
import com.google.inject.Inject;

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
		
		Migrator.init();
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		Sponge.getEventManager().registerListeners(this, new EventManager());

		Sponge.getRegistry().register(WorldGeneratorModifier.class, new OceanWorldGeneratorModifier());

		Sponge.getCommandManager().register(this, new Commands().cmdWorld, "world", "w");

		Common.initHelp();
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