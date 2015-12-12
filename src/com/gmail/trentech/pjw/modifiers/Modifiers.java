package com.gmail.trentech.pjw.modifiers;

import java.util.HashMap;

import org.spongepowered.api.world.gen.WorldGeneratorModifier;

public class Modifiers {

	private static HashMap<String, WorldGeneratorModifier> modifiers = new HashMap<>();
	
    public static WorldGeneratorModifier get(String name){
    	return modifiers.get(name);
    }

	public static HashMap<String, WorldGeneratorModifier> getAll() {
		return modifiers;
	}
	
	public static boolean exists(String name){
		if(modifiers.containsKey(name)){
			return true;
		}
		return false;
	}
	
	public static void put(String name, WorldGeneratorModifier modifier){
		modifiers.put(name, modifier);
	}
}
