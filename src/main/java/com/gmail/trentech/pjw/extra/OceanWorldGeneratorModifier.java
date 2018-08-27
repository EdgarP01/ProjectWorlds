package com.gmail.trentech.pjw.extra;

import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

public class OceanWorldGeneratorModifier implements WorldGeneratorModifier {

	@Override
	public void modifyWorldGenerator(WorldProperties world, DataContainer settings, WorldGenerator worldGenerator) {
		worldGenerator.getGenerationPopulators().clear();
		worldGenerator.getPopulators().clear();
		
		worldGenerator.setBiomeGenerator(new OceanBiomeGenerator());
		worldGenerator.setBaseGenerationPopulator(new OceanBaseGeneratorPopulator());
	}

	@Override
	public String getId() {
		return "pjw:ocean";
	}

	@Override
	public String getName() {
		return "Ocean";
	}

	@Override
	public CatalogKey getKey() {
		return CatalogKey.of("pjw", "ocean");
	}

}
