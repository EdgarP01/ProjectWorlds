package com.gmail.trentech.pjw.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import com.gmail.trentech.pjw.Main;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private Path path;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;
	
	private static ConcurrentHashMap<String, ConfigManager> configManagers = new ConcurrentHashMap<>();

	private ConfigManager(String configName) {
		try {
			path = Main.instance().getPath().resolve(configName + ".conf");
			
			if (!Files.exists(path)) {		
				Files.createFile(path);
				Main.instance().getLog().info("Creating new " + path.getFileName() + " file...");
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}

		load();
	}
	
	public static ConfigManager get(String configName) {
		return configManagers.get(configName);
	}
	
	public static ConfigManager get() {
		return configManagers.get("config");
	}

	public static ConfigManager init() {
		return init("config");
	}

	public static ConfigManager init(String configName) {
		ConfigManager configManager = new ConfigManager(configName);
		CommentedConfigurationNode config = configManager.getConfig();
		
		if (configName.equalsIgnoreCase("config")) {
			if (config.getNode("options", "first_join", "world").isVirtual()) {
				config.getNode("options", "first_join", "world").setValue("world").setComment("World player spawns to when joining for the first time");
			}
			if (config.getNode("options", "lobby_mode").isVirtual()) {
				config.getNode("options", "lobby_mode").setValue(false).setComment("If true, player will always spawn in first_join world on join");
			}
			
			// UPDATE
			if(!config.getNode("dimension_ids").isVirtual()) {
				config.removeChild("dimension_ids");
			}
		}
		
		configManager.save();
		
		configManagers.put(configName, configManager);
		
		return configManager;
	}
	
	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	public void save() {
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.instance().getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}

	private void load() {
		loader = HoconConfigurationLoader.builder().setPath(path).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.instance().getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}
}
