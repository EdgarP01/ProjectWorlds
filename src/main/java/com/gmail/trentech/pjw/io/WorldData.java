package com.gmail.trentech.pjw.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

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

		String defaultWorld = null;

		try {
			Scanner scanner = new Scanner(new File("server.properties"));
			
	        while(scanner.hasNextLine()) {
	            String line = scanner.nextLine();
	            
	            if(line.startsWith("level-name=")) {
	         	   defaultWorld = line.replace("level-name=", "");
	         	   scanner.close();
	         	   break;
	            }
	         }
	        scanner.close();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}

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
