package com.gmail.trentech.pjw.utils;

import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;

import com.gmail.trentech.pjw.Main;

public class Utils {

	// Convenience method to avoid null or absent values
	public static DimensionType getDimensionType(String type){
		switch(type){
		case "OVERWORLD": return DimensionTypes.OVERWORLD;
		case "END": return DimensionTypes.END;
		case "NETHER": return DimensionTypes.NETHER;
		default: return DimensionTypes.OVERWORLD;
		}
	}
	
	// Convenience method to avoid null or absent values
	public static GameMode getGameMode(String gamemode){
		if(Main.getGame().getRegistry().getType(GameMode.class, gamemode).isPresent()){
			return Main.getGame().getRegistry().getType(GameMode.class, gamemode).get();
		}
		return GameModes.SURVIVAL;
	}
	
	// Convenience method to avoid null or absent values
	public static GeneratorType getGeneratorType(String type){
		if(Main.getGame().getRegistry().getType(GeneratorType.class, type).isPresent()){
			return Main.getGame().getRegistry().getType(GeneratorType.class, type).get();
		}
		return GeneratorTypes.DEFAULT;
	}
	
	// Convenience method to avoid null or absent values
	public static Difficulty getGetDifficulty(String difficulty){
		if(Main.getGame().getRegistry().getType(Difficulty.class, difficulty).isPresent()){
			return Main.getGame().getRegistry().getType(Difficulty.class, difficulty).get();
		}
		return Difficulties.NORMAL;
	}
}
