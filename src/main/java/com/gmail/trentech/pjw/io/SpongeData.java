package com.gmail.trentech.pjw.io;

import java.io.File;

import org.spongepowered.api.Sponge;

public class SpongeData {

	private File dataFile;
	private boolean exists = false;

	public SpongeData(String worldName) {
		String defaultWorld = Sponge.getServer().getDefaultWorldName();

		if (defaultWorld.equalsIgnoreCase(worldName)) {
			dataFile = new File(defaultWorld, "level_sponge.dat");
		} else {
			dataFile = new File(defaultWorld + File.separator + worldName, "level_sponge.dat");
		}

		if (dataFile.exists()) {
			exists = true;
		}
	}

	public SpongeData(File directory) {
		dataFile = new File(directory, "level_sponge.dat");

		if (dataFile.exists()) {
			exists = true;
		}
	}

	public boolean exists() {
		return exists;
	}
}