package com.gmail.trentech.pjw.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.gmail.trentech.pjw.Main;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private File file;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;
	
	public ConfigManager(String configName) {
		String folder = "config/" + Resource.NAME + "/";
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder + configName);
		
		create();
		load();
		init();
	}
	
	public ConfigManager() {
		String folder = "config/" + Resource.NAME + "/";
        if (!new File(folder).isDirectory()) {
        	new File(folder).mkdirs();
        }
		file = new File(folder, "config.conf");
		
		create();
		load();
		init();
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
	
	private void init() {
		if(file.getName().equalsIgnoreCase("config.conf")){
			if(config.getNode("Options", "Portal", "Size").getString() == null) {
				config.getNode("Options", "Portal", "Size").setValue(100).setComment("Maximum portal region size");
			}
			if(config.getNode("Options", "Portal", "Replace-Frame").getString() == null) {
				config.getNode("Options", "Portal", "Replace-Frame").setValue(true);
			}
		}else if(file.getName().equalsIgnoreCase("worlds.conf")){
			if(config.getNode("Worlds").getString() == null) {
				config.getNode("Worlds").setComment("DO NOT EDIT THIS FILE");
			}
		}else if(file.getName().equalsIgnoreCase("portals.conf")){
			if(config.getNode("Portals").getString() == null) {
				config.getNode("Portals").setComment("DO NOT EDIT THIS FILE");
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
	
	@SuppressWarnings("unchecked")
	public boolean removeLocation(String locationName){
		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
			String uuid = node.getKey().toString();
			Object object = config.getNode("Portals", uuid, "Locations").getValue();
			
	    	ArrayList<String> list = null;
	    	if(object instanceof ArrayList) {
	    		list = (ArrayList<String>) object;
	    	}
	    	
			for(String loc : list){
				if(loc.equalsIgnoreCase(locationName)){
					config.getNode("Portals", uuid).setValue(null);
					save();
					return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public String getPortal(String locationName){
		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
			String uuid = node.getKey().toString();
			Object object = config.getNode("Portals", uuid, "Locations").getValue();
			
	    	ArrayList<String> list = null;
	    	if(object instanceof ArrayList) {
	    		list = (ArrayList<String>) object;
	    	}
	    	
			for(String loc : list){
				if(loc.equalsIgnoreCase(locationName)){
					return config.getNode("Portals", uuid, "World").getString();
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public boolean portalExists(String locationName){
		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
			String uuid = node.getKey().toString();
			Object object = config.getNode("Portals", uuid, "Locations").getValue();
			
	    	ArrayList<String> list = null;
	    	if(object instanceof ArrayList) {
	    		list = (ArrayList<String>) object;
	    	}
	    	
			for(String loc : list){
				if(loc.equalsIgnoreCase(locationName)){
					return true;
				}
			}
		}
		return false;
	}
}
