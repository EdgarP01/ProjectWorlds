package com.gmail.trentech.pjw.io;

import java.io.File;
import java.io.IOException;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjw.Main;

import eisenwave.nbt.NBTCompound;
import eisenwave.nbt.NBTNamedTag;
import eisenwave.nbt.io.NBTDeserializer;
import eisenwave.nbt.io.NBTSerializer;

public class WorldData {

	private String worldName;
	private File dataFile;
	private NBTNamedTag namedTag;

	public WorldData(String worldName) {
		this.worldName = worldName;

		String defaultWorld = ConfigManager.get(Main.getPlugin()).getConfig().getNode("options", "world_root").getString();

		if (defaultWorld.equalsIgnoreCase(worldName)) {
			dataFile = new File(defaultWorld, "level.dat");
		} else {
			dataFile = new File(defaultWorld + File.separator + worldName, "level.dat");
		}

		initialize();
	}

	public WorldData(File directory) {
		this.dataFile = new File(directory, "level.dat");
		
		if(dataFile.exists()) {
			this.worldName = directory.getName();
		}
		
		initialize();
	}

	private void initialize() {
		if (dataFile.exists()) {		
			try {
				namedTag = new NBTDeserializer().fromFile(dataFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean exists() {
		return dataFile.exists();
	}

	public boolean isCorrectLevelName() {
	    NBTCompound root = (NBTCompound) namedTag.getTag();
	    NBTCompound data = root.getCompoundTag("Data");

		if (data.getString("LevelName").equalsIgnoreCase(worldName)) {
			return true;
		}

		return false;
	}

	public String getLevelName() {
	    NBTCompound root = (NBTCompound) namedTag.getTag();
	    NBTCompound data = root.getCompoundTag("Data");
	    
	    return data.getString("LevelName");
	}
	
	public void setLevelName(String name) throws IOException {
	    NBTCompound root = (NBTCompound) namedTag.getTag();
	    NBTCompound data = root.getCompoundTag("Data");
	    data.putString("LevelName", worldName);
	    
	    dataFile.delete();
	    
	    new NBTSerializer().toFile(namedTag, dataFile);
	}
}
