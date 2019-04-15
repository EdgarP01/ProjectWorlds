package com.gmail.trentech.pjw.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.spongepowered.api.Sponge;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjw.Main;

import ninja.leaping.configurate.ConfigurationNode;

public class Migrator {

	public static void init() {	
		ConfigManager configManager = ConfigManager.get(Main.getPlugin());
		ConfigurationNode node = configManager.getConfig().getNode("worlds").setComment("DO NOT EDIT");
		
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

		SpongeData.ids.put(defaultWorld, 0);
		SpongeData.ids.put("DIM-1", 1);
		SpongeData.ids.put("DIM1", -1);
		
		node.getNode(defaultWorld).setValue(0);
		node.getNode("DIM-1").setValue(1);
		node.getNode("DIM1").setValue(-1);
		
		File directory = new File(Sponge.getGame().getSavesDirectory().toFile(), defaultWorld);

		if (!directory.exists()) {
			return;
		}

		for (File world : directory.listFiles()) {
			if (!world.isDirectory()) {
				continue;
			}

			WorldData worldData = new WorldData(world);

			if (!worldData.exists()) {
				continue;
			}

			String name = world.getName();

			if (!worldData.isCorrectLevelName()) {
				Main.instance().getLog().warn(worldData.getLevelName() + " -> " + name + ": Repairing level name mismatch");

				try {
					worldData.setLevelName(name);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}

			File dest = world;

			if (name.equalsIgnoreCase(defaultWorld)) {
				name = world.getName() + "_1";
				
				Main.instance().getLog().warn(defaultWorld + " -> " + name + ": Repairing duplicate world name");

				try {
					worldData.setLevelName(name);
					
					dest = new File(new File(Sponge.getGame().getSavesDirectory().toFile(), defaultWorld), name);
					
					world.renameTo(dest);
				} catch (IOException e) {
					e.printStackTrace();
					continue;
				}
			}

			SpongeData spongeData = new SpongeData(dest);
			
			if (!spongeData.exists()) {
				Main.instance().getLog().warn(name + ": Requires importing. /world import " + name + " <type> <generator>");
			} else {
				if(!SpongeData.ids.containsKey(name)) {
					if(!spongeData.isFreeDimId()) {
						Main.instance().getLog().warn(name + ": Repairing dimension id conflict");
						try {
							spongeData.setDimId(spongeData.getFreeDimId());
						} catch (IOException e) {
							e.printStackTrace();
							continue;
						}
					}
					
					SpongeData.ids.put(name, spongeData.getDimId());
					node.getNode(name).setValue(spongeData.getDimId());
					configManager.save();
				}
			}
		}
	}
}
