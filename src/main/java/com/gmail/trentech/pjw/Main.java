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
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.commands.CommandManager;
import com.gmail.trentech.pjw.extra.OceanWorldGeneratorModifier;
import com.gmail.trentech.pjw.extra.VoidWorldGeneratorModifier;
import com.gmail.trentech.pjw.io.Migrator;
import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.listeners.EventManager;
import com.gmail.trentech.pjw.listeners.TabEventManager;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Resource;
import com.google.inject.Inject;

import me.flibio.updatifier.Updatifier;
import ninja.leaping.configurate.ConfigurationNode;

@Updatifier(repoName = Resource.NAME, repoOwner = Resource.AUTHOR, version = Resource.VERSION)
@Plugin(id = Resource.ID, name = Resource.NAME, version = Resource.VERSION, description = Resource.DESCRIPTION, authors = Resource.AUTHOR, url = Resource.URL, dependencies = { @Dependency(id = "Updatifier", optional = true) })
public class Main {

	@Inject @ConfigDir(sharedRoot = false)
    private Path path;

	@Inject 
	private PluginContainer plugin;
	
	@Inject
	private Logger log;

	private static Main instance;
	
	@Listener
	public void onPreInitializationEvent(GamePreInitializationEvent event) {
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

		Sponge.getRegistry().register(WorldGeneratorModifier.class, new VoidWorldGeneratorModifier());
		Sponge.getRegistry().register(WorldGeneratorModifier.class, new OceanWorldGeneratorModifier());

		Sponge.getCommandManager().register(this, new CommandManager().cmdWorld, "world", "w");
		Sponge.getCommandManager().register(this, new CommandManager().cmdGamerule, "gamerule", "gr");

		ConfigManager.init();
	}

	@Listener
	public void onAboutToStartServer(GameAboutToStartServerEvent event) {
		ConfigurationNode node = ConfigManager.get().getConfig().getNode("dimension_ids");

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

		ConfigManager configManager = ConfigManager.get();
		
		configManager.getConfig().getNode("dimension_ids").setValue(list).setComment("DO NOT EDIT");
		configManager.save();
	}

	public Logger getLog() {
		return log;
	}

	public PluginContainer getPlugin() {
		return plugin;
	}
	
	public Path getPath() {
		return path;
	}
	
	public static Main instance() {
		return instance;
	}
}