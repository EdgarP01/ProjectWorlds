package com.gmail.trentech.pjw.events;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weather;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class EventManager {
	
	// TEMPORARY FIX FOR WORLD SPECIFIC GAMEMODES - DOES NOT ALWAYS WORK
	@Listener
	public void onDisplaceEntityEvent(DisplaceEntityEvent.TargetPlayer event) {
		if(!(event.getTargetEntity() instanceof Player)){
			return;
		}
		Player player = (Player) event.getTargetEntity();
		
		World worldSrc = event.getFromTransform().getExtent();
		World worldDest = event.getToTransform().getExtent();
		
		if(worldSrc != worldDest){
			GameMode gamemode = Main.getGame().getRegistry().getType(GameMode.class, new ConfigManager().getConfig().getNode("Worlds", worldDest.getName(), "Gamemode").getString()).get();
			if(player.getGameModeData().type().get() != gamemode){
				player.offer(Keys.GAME_MODE, gamemode);
			}		
		}
	}
	
	// CURRENTLY NOT WORKING - TEMP SOLUTION IN TASKS CLASS
	@Listener
	public void onChangeWorldWeatherEvent(ChangeWorldWeatherEvent event) {
		World world = event.getTargetWorld();
		
		ConfigurationNode config = new ConfigManager().getConfig();
		
		if(config.getNode("Worlds", world.getName(), "Weather", "Lock").getBoolean()){
			Weather weather = Main.getGame().getRegistry().getType(Weather.class, config.getNode("Worlds", world.getName(), "Weather", "Set").getString()).get();
			world.forecast(weather);
		}
	}
	
	@Listener
	public void onRespawnPlayerEvent(RespawnPlayerEvent event) {
		if(!(event.getTargetEntity() instanceof Player)){
			return;
		}

		String worldName = new ConfigManager().getConfig().getNode("Options", "Respawn").getString();
		
		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			World world = Main.getGame().getServer().getWorld(worldName).get();

			Transform<World> transform = event.getToTransform().setLocation(world.getSpawnLocation());
			event.setToTransform(transform);
		}
	}
}
