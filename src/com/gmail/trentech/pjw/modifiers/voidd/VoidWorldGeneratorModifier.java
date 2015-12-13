package com.gmail.trentech.pjw.modifiers.voidd;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.gen.WorldGenerator;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

public class VoidWorldGeneratorModifier implements WorldGeneratorModifier {

	@Override
	public void modifyWorldGenerator(WorldCreationSettings world, DataContainer settings, WorldGenerator worldGenerator) {
        
        worldGenerator.getGeneratorPopulators().clear();
		worldGenerator.getPopulators().clear();
		
		worldGenerator.setBaseGeneratorPopulator(new VoidBaseGeneratorPopulator());
	}

    @Override
    public String getId() {
        return "pjw:void";
    }

    @Override
    public String getName() {
        return "VOID";
    }

}
