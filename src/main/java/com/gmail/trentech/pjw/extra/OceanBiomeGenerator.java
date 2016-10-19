package com.gmail.trentech.pjw.extra;

import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.gen.BiomeGenerator;

import com.flowpowered.math.vector.Vector3i;

public class OceanBiomeGenerator implements BiomeGenerator {

	@Override
	public void generateBiomes(MutableBiomeVolume buffer) {
        final Vector3i min = buffer.getBiomeMin();
        final Vector3i max = buffer.getBiomeMax();
        final Vector3i size = buffer.getBiomeSize();
        
        for (int y = min.getY(); y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
            	for(int z = size.getZ(); z <= size.getZ(); z++) {
            		 buffer.setBiome(x, y, z, BiomeTypes.OCEAN);
            	}
            }
        }
	}

}
