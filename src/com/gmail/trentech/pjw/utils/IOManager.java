package com.gmail.trentech.pjw.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

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

public class IOManager {

	public boolean dimensionIdExists(String worldName) throws IOException{
		String defaultWorld = Main.getGame().getServer().getDefaultWorld().get().getWorldName();
		
		int dimId = getDimenionId(worldName);
		
		for(World world : Main.getGame().getServer().getWorlds()){		
			if(world.getName().equalsIgnoreCase(worldName)){
				continue;
			}
			
			File dataFile;
			
			if(!world.getName().equalsIgnoreCase(defaultWorld)){
				dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + world.getName(), "level_sponge.dat");
			}else{
				dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld, "level_sponge.dat");
			}
			
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
		}
		return false;
	}

	public void repairDim(String worldName) throws IOException{
		int dimId = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dimId, 0);
		
		String defaultWorld = Main.getGame().getServer().getDefaultWorld().get().getWorldName();

		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + worldName, "level_sponge.dat");
		
		CompoundTag compoundData = new CompoundTag("SpongeData", null);
		
		for (NBTTag root : XNBT.readFromFile(dataFile)) {
			CompoundTag compoundRoot = (CompoundTag) root;
			
			for(Entry<String, NBTTag> rootItem :compoundRoot.entrySet()){
				if(rootItem.getKey().equalsIgnoreCase("SpongeData")){
					CompoundTag compoundSpongeData = (CompoundTag) rootItem.getValue();
					
					for(Entry<String, NBTTag> tag :compoundSpongeData.entrySet()){
						if(tag.getKey().equalsIgnoreCase("dimensionId")){
							compoundData.put(new IntegerTag("dimensionId", dimId));
						}else{
							compoundData.put(tag.getValue());
						}		
					}
				}
			}
		}
		
		CompoundTag compoundRoot = new CompoundTag("", null);
		
		compoundRoot.put(compoundData);

		List<NBTTag> list = new ArrayList<>();
		
		list.add(compoundRoot);

		XNBT.writeToFile(list, dataFile);
	}
	
	public void init(String worldName, String dimension) throws IOException{
		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + Main.getGame().getServer().getDefaultWorld().get().getWorldName() + "/" + worldName, "level_sponge.dat");

		int dimId = DimensionManager.getNextFreeDimId();
		DimensionManager.registerDimension(dimId, 0);

		CompoundTag compoundSpongeData = new CompoundTag("SpongeData", null);	
		compoundSpongeData.put(new IntegerTag("dimensionId", dimId));
		compoundSpongeData.put(new LongTag("uuid_least", -6732046318667659594L));
		compoundSpongeData.put(new LongTag("uuid_most", 9143053678590905554L));
		compoundSpongeData.put(new StringTag("dimensionType", dimension));
		compoundSpongeData.put(new StringTag("LevelName", worldName));
		compoundSpongeData.put(new ListTag("generatorModifiers", null));

		CompoundTag compoundPlayerId = new CompoundTag("", null);
		
		compoundPlayerId.put(new LongTag("uuid_least", -7628444587550319768L));
		compoundPlayerId.put(new LongTag("uuid_most", 4244735002832685980L));

		List<NBTTag> listPlayerId = new ArrayList<>();
		listPlayerId.add(compoundPlayerId);
		
		compoundSpongeData.put(new ListTag("PlayerIdTable", listPlayerId));

		CompoundTag compoundRoot = new CompoundTag("", null);
		
		compoundRoot.put(compoundSpongeData);

		List<NBTTag> list = new ArrayList<>();
		
		list.add(compoundRoot);

		XNBT.writeToFile(list, dataFile);
		
		level(worldName);
	}
	
	private int getDimenionId(String worldName) throws IOException{
		String defaultWorld = Main.getGame().getServer().getDefaultWorld().get().getWorldName();

		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + worldName, "level_sponge.dat");

		for (NBTTag root : XNBT.readFromFile(dataFile)) {
			CompoundTag compoundRoot = (CompoundTag) root;
			
			for(Entry<String, NBTTag> rootItem :compoundRoot.entrySet()){
				if(rootItem.getKey().equalsIgnoreCase("SpongeData")){
					CompoundTag compoundSpongeData = (CompoundTag) rootItem.getValue();
					
					for(Entry<String, NBTTag> tag :compoundSpongeData.entrySet()){
						if(tag.getKey().equalsIgnoreCase("dimensionId")){
							return (Integer)tag.getValue().getPayload();
						}
					}
				}
			}
		}
		return 0;
	}
	
	private void level(String worldName) throws IOException{
		String defaultWorld = Main.getGame().getServer().getDefaultWorld().get().getWorldName();

		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + worldName, "level.dat");

		CompoundTag compoundData = new CompoundTag("Data", null);
		
		for (NBTTag root : XNBT.readFromFile(dataFile)) {
			CompoundTag compoundRoot = (CompoundTag) root;
			
			for(Entry<String, NBTTag> rootItem :compoundRoot.entrySet()){
				if(rootItem.getKey().equalsIgnoreCase("Data")){
					CompoundTag compoundSpongeData = (CompoundTag) rootItem.getValue();
					
					for(Entry<String, NBTTag> tag :compoundSpongeData.entrySet()){
						if(tag.getKey().equalsIgnoreCase("LevelName")){
							compoundData.put(new StringTag("LevelName", worldName));
						}else{
							compoundData.put(tag.getValue());
						}		
					}
				}
			}
		}
		
		CompoundTag compoundRoot = new CompoundTag("", null);
		
		compoundRoot.put(compoundData);

		List<NBTTag> list = new ArrayList<>();
		
		list.add(compoundRoot);

		XNBT.writeToFile(list, dataFile);
	}
}
