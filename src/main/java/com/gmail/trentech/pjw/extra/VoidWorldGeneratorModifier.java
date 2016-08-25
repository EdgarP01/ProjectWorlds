package com.gmail.trentech.pjw.extra;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

public class VoidWorldGeneratorModifier implements WorldGeneratorModifier {

	@Override
	public void modifyWorldGenerator(WorldProperties world, DataContainer settings, WorldGenerator worldGenerator) {
        worldGenerator.getGenerationPopulators().clear();
        worldGenerator.getPopulators().clear();
        for(BiomeType biome: Sponge.getRegistry().getAllOf(BiomeType.class)) {
            BiomeGenerationSettings biomeSettings = worldGenerator.getBiomeSettings(biome);
            biomeSettings.getGenerationPopulators().clear();
            biomeSettings.getPopulators().clear();
            biomeSettings.getGroundCoverLayers().clear();
        }
        worldGenerator.setBaseGenerationPopulator((world1, buffer, biomes) -> {});
        worldGenerator.setBiomeGenerator(buffer -> buffer.getBiomeWorker().fill((x, z) -> BiomeTypes.VOID));
	}

	@Override
	public String getId() {
		return "pjw:void";
	}

	@Override
	public String getName() {
		return "Void";
	}

}
