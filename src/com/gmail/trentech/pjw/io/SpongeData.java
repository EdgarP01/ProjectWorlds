package com.gmail.trentech.pjw.io;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.world.World;
import org.spongepowered.common.world.DimensionManager;

import com.gmail.trentech.pjw.Main;

import net.obnoxint.xnbt.XNBT;
import net.obnoxint.xnbt.types.CompoundTag;
import net.obnoxint.xnbt.types.IntegerTag;
import net.obnoxint.xnbt.types.ListTag;
import net.obnoxint.xnbt.types.LongTag;
import net.obnoxint.xnbt.types.NBTTag;
import net.obnoxint.xnbt.types.StringTag;

public class SpongeData {

	private String worldName;
	private File dataFile;
	private CompoundTag compoundTag;
	private boolean exists = false;
	
	public SpongeData(String worldName){
		this.worldName = worldName;

		String defaultWorld = Main.getGame().getServer().getDefaultWorld().get().getWorldName();
		
		dataFile = new File(defaultWorld + File.separator + worldName, "level_sponge.dat");
		if(defaultWorld.equalsIgnoreCase(worldName)){
			dataFile = new File(defaultWorld, "level_sponge.dat");
		}

		if(dataFile.exists()){
			exists = true;
			init();
		}
	}
	
	public boolean exists(){
		return exists;
	}
	
	private void init() {
		try {
			for (NBTTag root : XNBT.loadTags(dataFile)) {
				CompoundTag compoundRoot = (CompoundTag) root;
				
				for(Entry<String, NBTTag> rootItem :compoundRoot.entrySet()){
					if(rootItem.getKey().equalsIgnoreCase("SpongeData")){
						compoundTag = (CompoundTag) rootItem.getValue();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createNewConfig(String dimType) throws IOException{
		int dimId = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dimId, 0);

		CompoundTag compoundTag = new CompoundTag("SpongeData", null);	
		
		compoundTag.put(new IntegerTag("dimensionId", dimId));
		compoundTag.put(new LongTag("uuid_least", -6732046318667659594L));
		compoundTag.put(new LongTag("uuid_most", 9143053678590905554L));
		compoundTag.put(new StringTag("dimensionType", dimType));
		compoundTag.put(new StringTag("LevelName", worldName));

		CompoundTag compoundPlayerId = new CompoundTag("", null);
		
		compoundPlayerId.put(new LongTag("uuid_least", -7628444587550319768L));
		compoundPlayerId.put(new LongTag("uuid_most", 4244735002832685980L));

		List<NBTTag> listPlayerId = new ArrayList<>();
		listPlayerId.add(compoundPlayerId);
		
		compoundTag.put(new ListTag("PlayerIdTable", listPlayerId));

		CompoundTag compoundRoot = new CompoundTag("", null);

		compoundRoot.put(compoundTag);

		List<NBTTag> list = new ArrayList<>();
		
		list.add(compoundRoot);

		XNBT.saveTags(list, dataFile);
		
		this.compoundTag = compoundTag;
		
		WorldData worldData = new WorldData(worldName);
		
		if(!worldData.exists()){
			return;
		}
		
		if(!worldData.isCorrectLevelName()){
			worldData.setLevelName();
		}
	}
	
	public boolean isCorrectLevelName(){
		for(Entry<String, NBTTag> entry : compoundTag.entrySet()){
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
		WorldData worldData = new WorldData(worldName);
		
		if(!worldData.exists()){
			return;
		}
		
		if(!worldData.isCorrectLevelName()){
			worldData.setLevelName();
		}
		
		compoundTag.put(new StringTag("LevelName", worldName));

		CompoundTag compoundRoot = new CompoundTag("", null);
		
		compoundRoot.put(compoundTag);

		List<NBTTag> list = new ArrayList<>();
		
		list.add(compoundRoot);

		XNBT.saveTags(list, dataFile);
	}

	public boolean isFreeDimId(){
		int dimId = 0;
		for(Entry<String, NBTTag> entry : compoundTag.entrySet()){
			if(entry.getKey().equalsIgnoreCase("dimensionId")){
				dimId = (Integer) entry.getValue().getPayload();
				break;
			}
		}

		for(World world : Main.getGame().getServer().getWorlds()){
			if(world.getName().equalsIgnoreCase(worldName)){
				continue;
			}

			String defaultWorld = Main.getGame().getServer().getDefaultWorld().get().getWorldName();
			
			File dataFile = new File(defaultWorld + File.separator + world.getName(), "level_sponge.dat");
			if(defaultWorld.equalsIgnoreCase(world.getName())){
				dataFile = new File(defaultWorld, "level_sponge.dat");
			}
			
			try {
				for (NBTTag root : XNBT.loadTags(dataFile)) {
					CompoundTag compoundRoot = (CompoundTag) root;
					
					for(Entry<String, NBTTag> rootItem : compoundRoot.entrySet()){
						if(!rootItem.getKey().equalsIgnoreCase("SpongeData")){
							continue;
						}
						
						CompoundTag compoundSpongeData = (CompoundTag) rootItem.getValue();
						
						for(Entry<String, NBTTag> tag :compoundSpongeData.entrySet()){
							if(!tag.getKey().equalsIgnoreCase("dimensionId")){
								continue;
							}
							
							int id = (Integer) tag.getValue().getPayload();
							
							if(id == dimId){
								return false;
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public void setDimId() throws IOException{
		int dimId = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dimId, 0);
		
		compoundTag.put(new IntegerTag("dimensionId", dimId));

		CompoundTag compoundRoot = new CompoundTag("", null);
		
		compoundRoot.put(compoundTag);

		List<NBTTag> list = new ArrayList<>();
		
		list.add(compoundRoot);

		XNBT.saveTags(list, dataFile);
	}
}
