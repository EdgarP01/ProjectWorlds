package com.gmail.trentech.pjw.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;

import net.obnoxint.xnbt.NBTTag;
import net.obnoxint.xnbt.XNBT;
import net.obnoxint.xnbt.types.ByteTag;
import net.obnoxint.xnbt.types.CompoundTag;
import net.obnoxint.xnbt.types.IntegerTag;
import net.obnoxint.xnbt.types.ListTag;
import net.obnoxint.xnbt.types.LongTag;
import net.obnoxint.xnbt.types.StringTag;

public class IOManager {

	private static int getDimenionId() throws IOException{
		List<Integer> ids = new ArrayList<>();
		
		String defaultWorld = Main.getGame().getServer().getDefaultWorld().get().getWorldName();
		
		for(World world : Main.getGame().getServer().getWorlds()){
			if(!world.getName().equalsIgnoreCase(defaultWorld)){
				File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + defaultWorld + "/" + world.getName(), "level_sponge.dat");
				
				for (NBTTag root : XNBT.readFromFile(dataFile)) {
					CompoundTag compoundRoot = (CompoundTag) root;
					
					for(Entry<String, NBTTag> rootItem :compoundRoot.entrySet()){
						if(rootItem.getKey().equalsIgnoreCase("SpongeData")){
							CompoundTag compoundSpongeData = (CompoundTag) rootItem.getValue();
							
							for(Entry<String, NBTTag> tag :compoundSpongeData.entrySet()){
								if(tag.getKey().equalsIgnoreCase("dimensionId")){
									int id = (Integer)tag.getValue().getPayload();
									
									ids.add(id);
								}
							}
						}
					}
				}
			}
		}
		
		if(ids.isEmpty()){
			return 0;
		}
		
		return Collections.max(ids) + 1;
	}
	
	public static void init(String worldName) throws IOException{
		File dataFile = new File(Main.getGame().getSavesDirectory() + "/" + Main.getGame().getServer().getDefaultWorld().get().getWorldName() + "/" + worldName, "level_sponge.dat");

		LinkedHashMap<String, NBTTag> mapSpongeData = new LinkedHashMap<>();

		mapSpongeData.put("enabled", new ByteTag("enabled", (byte) 1));
		mapSpongeData.put("keepSpawnLocked", new ByteTag("keepSpawnLocked", (byte) 1));
		mapSpongeData.put("loadOnStartup", new ByteTag("loadOnStartup", (byte) 1));
		mapSpongeData.put("dimensionId", new IntegerTag("dimensionId", getDimenionId()));
		mapSpongeData.put("uuid_least", new LongTag("uuid_least", -6732046318667659594L));
		mapSpongeData.put("uuid_most", new LongTag("uuid_most", 9143053678590905554L));
		mapSpongeData.put("dimensionType", new StringTag("dimensionType", "net.minecraft.world.WorldProviderSurface"));
		mapSpongeData.put("LevelName", new StringTag("LevelName", worldName));
		mapSpongeData.put("generatorModifiers", new ListTag("generatorModifiers", null));
		
		LinkedHashMap<String, NBTTag> mapPlayerId = new LinkedHashMap<>();
		
		mapPlayerId.put("uuid_least", new LongTag("uuid_least", -7628444587550319768L));
		mapPlayerId.put("uuid_most", new LongTag("uuid_most", 4244735002832685980L));
		
		CompoundTag compoundPlayerId = new CompoundTag("", mapPlayerId);
		List<NBTTag> listPlayerId = new ArrayList<>();
		listPlayerId.add(compoundPlayerId);
		
		mapSpongeData.put("PlayerIdTable", new ListTag("PlayerIdTable", listPlayerId));
		
		CompoundTag compoundSpongeData = new CompoundTag("SpongeData", mapSpongeData);
		LinkedHashMap<String, NBTTag> mapRoot = new LinkedHashMap<>();
		mapRoot.put("SpongeData", compoundSpongeData);
		
		CompoundTag compoundRoot = new CompoundTag("", mapRoot);

		List<NBTTag> list = new ArrayList<>();
		
		list.add(compoundRoot);

		XNBT.writeToFile(list, dataFile);
	}

}
