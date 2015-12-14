package com.gmail.trentech.pjw.listeners;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.weather.Weather;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.commands.CMDYes;
import com.gmail.trentech.pjw.events.TeleportEvent;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Resource;

import ninja.leaping.configurate.ConfigurationNode;

public class EventManager {

	@Listener
	public void onTeleportEvent(TeleportEvent event){
		Player player = event.getPlayer();
		Location<World> src = event.getSrc();
		Location<World> dest = event.getDest();

		if(!player.hasPermission("pjw.worlds." + dest.getExtent().getName())){
			player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to travel to ", dest.getExtent().getName()));
			return;
		}
		if(!player.setLocationSafely(dest)){
			CMDYes.players.put(player, dest);
			player.sendMessage(Texts.builder().color(TextColors.DARK_RED).append(Texts.of("Unsafe spawn point detected. Teleport anyway? ")).onClick(TextActions.runCommand("/yes")).append(Texts.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
			return;
		}
		
		Resource.spawnParticles(src, 0.5, true);
		Resource.spawnParticles(src.getRelative(Direction.UP), 0.5, true);
		
		Resource.spawnParticles(dest, 1.0, false);
		Resource.spawnParticles(dest.getRelative(Direction.UP), 1.0, false);
		
		player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, dest.getExtent().getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", dest.getExtent().getSpawnLocation().getBlockX(), ", y: ", dest.getExtent().getSpawnLocation().getBlockY(),", z: ", dest.getExtent().getSpawnLocation().getBlockZ())));

		if(event.getSrc().getExtent() == event.getDest().getExtent()){
			return;
		}
		
		// TEMPORARY FIX FOR WORLD SPECIFIC GAMEMODES - STILL NOT WORKING
		String gm = new ConfigManager("worlds.conf").getConfig().getNode("Worlds", event.getDest().getExtent().getName(), "Gamemode").getString();
		GameMode gamemode = GameModes.SURVIVAL;
		if(Main.getGame().getRegistry().getType(GameMode.class, gm).isPresent()){
			gamemode = Main.getGame().getRegistry().getType(GameMode.class, gm).get();
		}

		player.offer(Keys.GAME_MODE, gamemode);
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
	    Player player = event.getTargetEntity();
	    
	    // NOT IMPLEMENTED YET
		if(player.get(JoinData.class).isPresent()){
			return;
		}
		
		String worldName = new ConfigManager().getConfig().getNode("Options", "Join-Spawn").getString();
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			return;
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		player.setLocationSafely(world.getSpawnLocation());
	}
	
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
//	@Listener
//	public void onDisplaceEntityEvent(DisplaceEntityEvent event) {
//		if(!(event.getTargetEntity() instanceof Player)){
//			return;
//		}
//		Player player = (Player) event.getTargetEntity();
//		
//		World worldSrc = event.getFromTransform().getExtent();
//		World worldDest = event.getToTransform().getExtent();
//
//		if(worldSrc != worldDest){
//			GameMode gamemode = GameModes.SURVIVAL;
//			if(Main.getGame().getRegistry().getType(GameMode.class, new ConfigManager("worlds.conf").getConfig().getNode("Worlds", worldDest.getName(), "Gamemode").getString()).isPresent()){
//				gamemode = Main.getGame().getRegistry().getType(GameMode.class, new ConfigManager("worlds.conf").getConfig().getNode("Worlds", worldDest.getName(), "Gamemode").getString()).get();
//			}
//			System.out.println("DisplaceEntityEvent GAMEMODE");
//			player.offer(Keys.GAME_MODE, gamemode);
//		}
//	}
	
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
