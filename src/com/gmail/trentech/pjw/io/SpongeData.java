package com.gmail.trentech.pjw.io;


import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;

import net.obnoxint.xnbt.XNBT;
import net.obnoxint.xnbt.types.CompoundTag;
import net.obnoxint.xnbt.types.NBTTag;

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
	
//	public void setDimId() throws IOException{
//		int dimId = DimensionManager.getNextFreeDimId();
//		DimensionManager.registerDimension(dimId, 0);
//		
//		compoundTag.put(new IntegerTag("dimensionId", dimId));
//
//		CompoundTag compoundRoot = new CompoundTag("", null);
//		
//		compoundRoot.put(compoundTag);
//
//		List<NBTTag> list = new ArrayList<>();
//		
//		list.add(compoundRoot);
//
//		XNBT.saveTags(list, dataFile);
//	}
}
