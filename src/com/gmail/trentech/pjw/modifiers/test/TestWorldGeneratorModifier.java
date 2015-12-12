package com.gmail.trentech.pjw.modifiers.test;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

public class TestWorldGeneratorModifier implements WorldGeneratorModifier {

	@Override
	public void modifyWorldGenerator(WorldCreationSettings world, DataContainer settings, WorldGenerator worldGenerator) {
        worldGenerator.setBaseGeneratorPopulator(new TestGeneratorPopulator());
        worldGenerator.setBiomeGenerator(new TestBiomeGenerator());
		worldGenerator.getPopulators().clear();
	}

    @Override
    public String getId() {
        return "pjw:test";
    }

    @Override
    public String getName() {
        return "TEST";
    }

}
