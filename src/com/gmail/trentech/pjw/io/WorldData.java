package com.gmail.trentech.pjw.io;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.gmail.trentech.pjw.Main;

import java.util.Optional;

import net.obnoxint.xnbt.NBTTag;
import net.obnoxint.xnbt.XNBT;
import net.obnoxint.xnbt.types.CompoundTag;
import net.obnoxint.xnbt.types.StringTag;

public class WorldData {

	protected String worldName;
	protected String defaultWorld = Main.getGame().getServer().getDefaultWorld().get().getWorldName();
	protected Optional<CompoundTag> compoundTag = Optional.empty();

	public WorldData(String worldName){
		this.worldName = worldName;

		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + worldName, "level.dat");
		if(defaultWorld.equalsIgnoreCase(worldName)){
			dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld, "level.dat");
		}
		
		if(dataFile.exists()){
			init();
		}
	}
	
	public boolean exists(){
		if(compoundTag.isPresent()){
			return true;
		}
		return false;
	}
	
	private void init() {
		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + worldName, "level.dat");
		if(defaultWorld.equalsIgnoreCase(worldName)){
			dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld, "level.dat");
		}
		try {
			for (NBTTag root : XNBT.readFromFile(dataFile)) {
				CompoundTag compoundRoot = (CompoundTag) root;
				
				for(Entry<String, NBTTag> rootItem :compoundRoot.entrySet()){
					if(rootItem.getKey().equalsIgnoreCase("Data")){
						compoundTag = Optional.of((CompoundTag) rootItem.getValue());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isCorrectLevelName(){
		for(Entry<String, NBTTag> entry : compoundTag.get().entrySet()){
			if(!entry.getKey().equalsIgnoreCase("LevelName")){
				continue;
			}
			
			String levelName = (String) entry.getValue().getPayload();
			
			if(levelName.equalsIgnoreCase(worldName)){
				return true;
			}
			return false;
		}
		return false;
	}
	
	public void setLevelName() throws IOException{
		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + worldName, "level.dat");
		if(defaultWorld.equalsIgnoreCase(worldName)){
			dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld, "level.dat");
		}
		
		compoundTag.get().put(new StringTag("LevelName", worldName));

		CompoundTag compoundRoot = new CompoundTag("", null);
		
		compoundRoot.put(compoundTag.get());

		List<NBTTag> list = new ArrayList<>();
		
		list.add(compoundRoot);

		XNBT.writeToFile(list, dataFile);
	}
}
