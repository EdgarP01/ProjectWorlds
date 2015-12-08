package com.gmail.trentech.pjw.utils;

import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;

public class Utils {
	// Convenience methods to avoid null or absent values
	
	public static DimensionType getDimensionType(String type){
		switch(type){
		case "OVERWORLD": return DimensionTypes.OVERWORLD;
		case "END": return DimensionTypes.END;
		case "NETHER": return DimensionTypes.NETHER;
		default: return DimensionTypes.OVERWORLD;
		}
	}
	
	public static GameMode getGameMode(String gamemode){
		switch(gamemode){
		case "SURVIVAL": return GameModes.SURVIVAL;
		case "ADVENTURE": return GameModes.ADVENTURE;
		case "CREATIVE": return GameModes.CREATIVE;
		case "SPECTATOR": return GameModes.SPECTATOR;
		default: return GameModes.SURVIVAL;
		}
	}
	
	public static GeneratorType getGeneratorType(String type){
		switch(type){
		case "OVERWORLD": return GeneratorTypes.OVERWORLD;
		case "END": return GeneratorTypes.END;
		case "FLAT": return GeneratorTypes.FLAT;
		case "NETHER": return GeneratorTypes.NETHER;
		case "DEBUG": return GeneratorTypes.DEBUG;
		default: return GeneratorTypes.DEFAULT;
		}
	}
	
	public static Difficulty getGetDifficulty(String difficulty){
		switch(difficulty){
		case "EASY": return Difficulties.EASY;
		case "HARD": return Difficulties.HARD;
		case "NORMAL": return Difficulties.NORMAL;
		case "PEACEFUL": return Difficulties.PEACEFUL;
		default: return Difficulties.NORMAL;
		}
	}
}
