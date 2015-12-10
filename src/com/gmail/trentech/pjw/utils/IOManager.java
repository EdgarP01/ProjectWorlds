package com.gmail.trentech.pjw.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

		CompoundTag compoundSpongeData = new CompoundTag("SpongeData", null);

		compoundSpongeData.put(new ByteTag("enabled", (byte) 1));
		compoundSpongeData.put(new ByteTag("keepSpawnLocked", (byte) 1));
		compoundSpongeData.put(new ByteTag("loadOnStartup", (byte) 1));
		compoundSpongeData.put(new IntegerTag("dimensionId", getDimenionId()));
		compoundSpongeData.put(new LongTag("uuid_least", -6732046318667659594L));
		compoundSpongeData.put(new LongTag("uuid_most", 9143053678590905554L));
		compoundSpongeData.put(new StringTag("dimensionType", "net.minecraft.world.WorldProviderSurface"));
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
	}
}
