package com.gmail.trentech.pjw.events;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.weather.Weather;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class EventManager {
	
	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event){
		World world = event.getTargetWorld();
		
		WorldProperties properties = world.getProperties();
		
		ConfigManager loader = new ConfigManager("worlds.conf");
		ConfigurationNode config = loader.getConfig();
		
		if(config.getNode("Worlds", world.getName()).getString() == null){
			config.getNode("Worlds", world.getName(), "PVP").setValue(true);
			config.getNode("Worlds", world.getName(), "Respawn-World").setValue("NONE");
			config.getNode("Worlds", world.getName(), "Gamemode").setValue(properties.getGameMode().getName().toUpperCase());
			config.getNode("Worlds", world.getName(), "Time", "Lock").setValue(false);
			config.getNode("Worlds", world.getName(), "Time", "Set").setValue(6000);
			config.getNode("Worlds", world.getName(), "Weather", "Lock").setValue(false);
			config.getNode("Worlds", world.getName(), "Weather", "Set").setValue("CLEAR");	

			loader.save();
		}
	}
	
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
			GameMode gamemode = GameModes.SURVIVAL;
			if(Main.getGame().getRegistry().getType(GameMode.class, new ConfigManager("worlds.conf").getConfig().getNode("Worlds", worldDest.getName(), "Gamemode").getString()).isPresent()){
				gamemode = Main.getGame().getRegistry().getType(GameMode.class, new ConfigManager("worlds.conf").getConfig().getNode("Worlds", worldDest.getName(), "Gamemode").getString()).get();
			}

			player.offer(Keys.GAME_MODE, gamemode);
		}
	}
	
    @Listener
    public void onDamageEntityEvent(DamageEntityEvent event) {
    	if(!(event.getTargetEntity() instanceof Player)) {
    		return;
    	}
    	Player victim = (Player) event.getTargetEntity();

		if (!event.getCause().first(EntityDamageSource.class).isPresent()) {
			return;
		}		
        EntityDamageSource damageSource = event.getCause().first(EntityDamageSource.class).get();

        Entity source = damageSource.getSource();
        if (!(source instanceof Player)) {
        	return;
        }

        World world = victim.getWorld();
        
        if(!new ConfigManager("worlds.conf").getConfig().getNode("Worlds", world.getName(), "PVP").getBoolean()){
        	event.setCancelled(true);
        }
    }
	
	// CURRENTLY NOT WORKING - TEMP SOLUTION IN TASKS CLASS
	@Listener
	public void onChangeWorldWeatherEvent(ChangeWorldWeatherEvent event) {
		World world = event.getTargetWorld();
		
		ConfigurationNode config = new ConfigManager("worlds.conf").getConfig();
		
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
		World world = event.getFromTransform().getExtent();

		String respawnWorldName = new ConfigManager("worlds.conf").getConfig().getNode("Worlds", world.getName(), "Respawn-World").getString();

		if(!respawnWorldName.equalsIgnoreCase("NONE")){			
			if(Main.getGame().getServer().getWorld(respawnWorldName).isPresent()){
				World respawnWorld = Main.getGame().getServer().getWorld(respawnWorldName).get();
				
				Transform<World> transform = event.getToTransform().setLocation(respawnWorld.getSpawnLocation());
				event.setToTransform(transform);
			}
			return;
		}
		
		Transform<World> transform = event.getToTransform().setLocation(world.getSpawnLocation());
		event.setToTransform(transform);
	}
}
