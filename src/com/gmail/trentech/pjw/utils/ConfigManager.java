package com.gmail.trentech.pjw.utils;

import java.io.File;
import java.io.IOException;

import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private File file;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;
	
	public ConfigManager(String config) {
		String folder = "config/" + Resource.NAME + "/";
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder + config);
		
		create();
		load();
	}
	
	public ConfigManager() {
		String folder = "config/" + Resource.NAME + "/";
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder, "config.conf");
		
		create();
		load();
	}
	
	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	public void save(){
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}
	
	public void init() {	
		if(config.getNode("Options").getString() == null) {
			config.getNode("Options", "Respawn").setValue("world");
		}
		
		for(World world : Main.getGame().getServer().getWorlds()){
			WorldProperties properties = world.getProperties();
			if(config.getNode("Worlds", world.getName()).getString() == null){
				config.getNode("Worlds").setComment("DO NOT EDIT THIS SECTION");
				config.getNode("Worlds", world.getName(), "UUID").setValue(world.getUniqueId().toString());
				config.getNode("Worlds", world.getName(), "Dimension-Type").setValue(properties.getDimensionType().getName().toUpperCase());
				config.getNode("Worlds", world.getName(), "Generator-Type").setValue(properties.getGeneratorType().getName().toUpperCase());
				config.getNode("Worlds", world.getName(), "Seed").setValue(properties.getSeed());
				config.getNode("Worlds", world.getName(), "Difficulty").setValue(properties.getDifficulty().getName().toUpperCase());
				config.getNode("Worlds", world.getName(), "Gamemode").setValue(properties.getGameMode().getName().toUpperCase());
				config.getNode("Worlds", world.getName(), "Keep-Spawn-Loaded").setValue(false);
				config.getNode("Worlds", world.getName(), "Hardcore").setValue(false);
				config.getNode("Worlds", world.getName(), "Time", "Lock").setValue(false);
				config.getNode("Worlds", world.getName(), "Time", "Set").setValue(6000);
				config.getNode("Worlds", world.getName(), "Weather", "Lock").setValue(false);
				config.getNode("Worlds", world.getName(), "Weather", "Set").setValue("CLEAR");				
			}
		}
		save();
	}

	private void create(){
		if(!file.exists()) {
			try {
				Main.getLog().info("Creating new " + file.getName() + " file...");
				file.createNewFile();		
			} catch (IOException e) {				
				Main.getLog().error("Failed to create new config file");
				e.printStackTrace();
			}
		}
	}
	
	private void load(){
		loader = HoconConfigurationLoader.builder().setFile(file).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}

}
