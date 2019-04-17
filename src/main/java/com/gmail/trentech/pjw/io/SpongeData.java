package com.gmail.trentech.pjw.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import eisenwave.nbt.NBTCompound;
import eisenwave.nbt.NBTNamedTag;
import eisenwave.nbt.io.NBTDeserializer;
import eisenwave.nbt.io.NBTSerializer;

public class SpongeData {

	private File dataFile;
	private NBTNamedTag namedTag;
	private int dimId;
	public static ConcurrentHashMap<String,Integer> ids = new ConcurrentHashMap<>();
	
	public SpongeData(String worldName) {
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
			dataFile = new File(defaultWorld, "level_sponge.dat");
		} else {
			dataFile = new File(defaultWorld + File.separator + worldName, "level_sponge.dat");
		}

		initialize();
	}

	public SpongeData(File directory) {
		dataFile = new File(directory, "level_sponge.dat");

		initialize();
	}

	public void initialize() {
		if(dataFile.exists()) {
			try {
				namedTag = new NBTDeserializer().fromFile(dataFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		    NBTCompound root = (NBTCompound) namedTag.getTag();
		    NBTCompound data = root.getCompoundTag("SpongeData");

		    dimId = data.getInt("dimensionId");
		}
	}
	
 	public static ConcurrentHashMap<String,Integer> getIds() {
		return ids;
	}

	public static void setIds(ConcurrentHashMap<String,Integer> list) {
		ids = list;
	}

	public boolean exists() {
		return dataFile.exists();
	}

	public int getDimId() {
		return dimId;
	}

	public boolean isFreeDimId() {
		if (ids.containsValue(getDimId())) {
			return false;
		}
		return true;
	}

	public int getFreeDimId() {
		for (int i = 1; i < Integer.MAX_VALUE; i++) {
			if (!ids.containsValue(i)) {
				return i;
			}
		}

		throw new NullPointerException();
	}

	public void setDimId(int id) throws IOException {
	    NBTCompound root = (NBTCompound) namedTag.getTag();
	    NBTCompound data = root.getCompoundTag("SpongeData");
		
	    dataFile.delete();
	    
	    data.putInt("dimensionId", id);
	    
	    new NBTSerializer().toFile(namedTag, dataFile);
	    
		this.dimId = id;		
	}
}