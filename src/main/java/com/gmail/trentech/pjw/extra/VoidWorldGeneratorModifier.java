package com.gmail.trentech.pjw.extra;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

public class VoidWorldGeneratorModifier implements WorldGeneratorModifier {

	@Override
	public void modifyWorldGenerator(WorldProperties world, DataContainer settings, WorldGenerator worldGenerator) {
        worldGenerator.getGenerationPopulators().clear();
		worldGenerator.getPopulators().clear();
		
		worldGenerator.setBaseGenerationPopulator(new VoidBaseGeneratorPopulator());
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
