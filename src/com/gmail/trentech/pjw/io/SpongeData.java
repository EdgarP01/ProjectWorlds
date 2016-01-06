package com.gmail.trentech.pjw.io;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.world.World;
import org.spongepowered.common.world.DimensionManager;

import com.gmail.trentech.pjw.Main;

import net.obnoxint.xnbt.NBTTag;
import net.obnoxint.xnbt.XNBT;
import net.obnoxint.xnbt.types.CompoundTag;
import net.obnoxint.xnbt.types.IntegerTag;
import net.obnoxint.xnbt.types.ListTag;
import net.obnoxint.xnbt.types.LongTag;
import net.obnoxint.xnbt.types.StringTag;

public class SpongeData {

	protected String worldName;
	protected String defaultWorld = Main.getGame().getServer().getDefaultWorld().get().getWorldName();
	protected Optional<CompoundTag> compoundTag = Optional.empty();

	public SpongeData(String worldName){
		this.worldName = worldName;

		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + worldName, "level_sponge.dat");
		if(defaultWorld.equalsIgnoreCase(worldName)){
			dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld, "level_sponge.dat");
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
		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + worldName, "level_sponge.dat");
		if(defaultWorld.equalsIgnoreCase(worldName)){
			dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld, "level_sponge.dat");
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

	public void createNewConfig(String dimType) throws IOException{
		int dimId = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dimId, 0);

		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + worldName, "level_sponge.dat");
		if(defaultWorld.equalsIgnoreCase(worldName)){
			dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld, "level_sponge.dat");
		}

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

		XNBT.writeToFile(list, dataFile);
		
		this.compoundTag = Optional.of(compoundTag);
		
		WorldData worldData = new WorldData(worldName);
		
		if(!worldData.exists()){
			return;
		}
		
		if(!worldData.isCorrectLevelName()){
			worldData.setLevelName();
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
		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + worldName, "level_sponge.dat");
		if(defaultWorld.equalsIgnoreCase(worldName)){
			dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld, "level_sponge.dat");
		}
		
		WorldData worldData = new WorldData(worldName);
		
		if(!worldData.exists()){
			return;
		}
		
		if(!worldData.isCorrectLevelName()){
			worldData.setLevelName();
		}
		
		compoundTag.get().put(new StringTag("LevelName", worldName));

		CompoundTag compoundRoot = new CompoundTag("", null);
		
		compoundRoot.put(compoundTag.get());

		List<NBTTag> list = new ArrayList<>();
		
		list.add(compoundRoot);

		XNBT.writeToFile(list, dataFile);
	}

	public boolean isFreeDimId(){
		int dimId = 0;
		for(Entry<String, NBTTag> entry : compoundTag.get().entrySet()){
			if(!entry.getKey().equalsIgnoreCase("dimensionId")){
				continue;
			}
			
			dimId = (Integer) entry.getValue().getPayload();
		}

		for(World world : Main.getGame().getServer().getWorlds()){		
			if(world.getName().equalsIgnoreCase(worldName)){
				continue;
			}
			
			String defaultWorld = Main.getGame().getServer().getDefaultWorld().get().getWorldName();
			
			File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + world.getName(), "level_sponge.dat");
			if(!world.getName().equalsIgnoreCase(defaultWorld)){
				dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld, "level_sponge.dat");
			}
			
			try {
				for (NBTTag root : XNBT.readFromFile(dataFile)) {
					CompoundTag compoundRoot = (CompoundTag) root;
					
					for(Entry<String, NBTTag> rootItem :compoundRoot.entrySet()){
						if(rootItem.getKey().equalsIgnoreCase("SpongeData")){
							CompoundTag compoundSpongeData = (CompoundTag) rootItem.getValue();
							
							for(Entry<String, NBTTag> tag :compoundSpongeData.entrySet()){
								if(tag.getKey().equalsIgnoreCase("dimensionId")){
									int id = (Integer)tag.getValue().getPayload();
									if(id == dimId){
										return true;
									}
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public void setDimId() throws IOException{
		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + worldName, "level_sponge.dat");
		if(defaultWorld.equalsIgnoreCase(worldName)){
			dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld, "level_sponge.dat");
		}
		
		int dimId = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dimId, 0);
		
		compoundTag.get().put(new IntegerTag("dimensionId", dimId));

		CompoundTag compoundRoot = new CompoundTag("", null);
		
		compoundRoot.put(compoundTag.get());

		List<NBTTag> list = new ArrayList<>();
		
		list.add(compoundRoot);

		XNBT.writeToFile(list, dataFile);
	}
}
