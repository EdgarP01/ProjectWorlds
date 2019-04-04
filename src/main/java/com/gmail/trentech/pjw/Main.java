package com.gmail.trentech.pjw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

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
	}

	@Listener
	public void onInitialization(GameInitializationEvent event) {
		Sponge.getEventManager().registerListeners(this, new EventManager());

		Common.initHelp();
		
		Sponge.getCommandManager().register(this, new CommandWorld().getCommandSpec(), "world", "w");
	}

	@Listener
	public void onAboutToStartServer(GameAboutToStartServerEvent event) {
		ConfigurationNode node = ConfigManager.get(Main.getPlugin()).getConfig().getNode("dimension_ids");

		SpongeData.getIds().addAll(node.getChildrenList().stream().map(ConfigurationNode::getInt).collect(Collectors.toList()));

		if (!node.isVirtual() && new File(Sponge.getServer().getDefaultWorldName()).exists()) {
			Migrator.init();
		}
	}

	@Listener
	public void onStartedServer(GameStartedServerEvent event) {
		List<Integer> list = new ArrayList<>();

		for (WorldProperties world : Sponge.getServer().getAllWorldProperties()) {
			Sponge.getServer().saveWorldProperties(world);
			list.add((int) world.getPropertySection(DataQuery.of("SpongeData")).get().get(DataQuery.of("dimensionId")).get());
		}

		SpongeData.setIds(list);

		ConfigManager configManager = ConfigManager.get(Main.getPlugin());
		
		configManager.getConfig().getNode("dimension_ids").setValue(list).setComment("DO NOT EDIT");
		configManager.save();
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