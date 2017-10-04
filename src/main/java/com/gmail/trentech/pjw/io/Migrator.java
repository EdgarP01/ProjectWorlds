package com.gmail.trentech.pjw.io;

import java.io.File;
import java.io.IOException;

import org.spongepowered.api.Sponge;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjw.Main;

public class Migrator {

	public static void init() {
		String defaultWorld = ConfigManager.get(Main.getPlugin()).getConfig().getNode("options", "world_root").getString();
		
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
				Main.instance().getLog().warn(name + ": Requires importing. /world import " + name + " <dimensionType> <generatorType> [modifier..]");
			}
		}
	}
}
