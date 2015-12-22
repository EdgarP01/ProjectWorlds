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
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
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
import com.gmail.trentech.pjw.commands.CMDTeleport;
import com.gmail.trentech.pjw.events.TeleportEvent;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Resource;

public class EventManager {

	@Listener
	public void onTeleportEvent(TeleportEvent event, @First Player player){
		Location<World> src = event.getSrc();
		Location<World> dest = event.getDest();

		if(!player.hasPermission("pjw.worlds." + dest.getExtent().getName())){
			player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to travel to ", dest.getExtent().getName()));
			return;
		}
		if(!player.setLocationSafely(dest)){
			CMDTeleport.players.put(player, dest);
			player.sendMessage(Texts.builder().color(TextColors.DARK_RED).append(Texts.of("Unsafe spawn point detected. Teleport anyway? ")).onClick(TextActions.runCommand("/world teleport confirm")).append(Texts.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
			return;
		}
		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
			Resource.spawnParticles(src, 0.5, true);
			Resource.spawnParticles(src.getRelative(Direction.UP), 0.5, true);
			
			Resource.spawnParticles(dest, 1.0, false);
			Resource.spawnParticles(dest.getRelative(Direction.UP), 1.0, false);
		}

		player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, dest.getExtent().getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", dest.getExtent().getSpawnLocation().getBlockX(), ", y: ", dest.getExtent().getSpawnLocation().getBlockY(),", z: ", dest.getExtent().getSpawnLocation().getBlockZ())));
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

		if(!properties.getGameRule("pvp").isPresent()){
			properties.setGameRule("pvp", "true");
		}
		if(!properties.getGameRule("respawnWorld").isPresent()){
			properties.setGameRule("respawnWorld", Main.getGame().getServer().getDefaultWorld().get().getWorldName());
		}
		if(!properties.getGameRule("gamemode").isPresent()){
			properties.setGameRule("gamemode", GameModes.SURVIVAL.getName());
		}
		if(!properties.getGameRule("defaultWeather").isPresent()){
			properties.setGameRule("defaultWeather", "normal");
		}
	}

	@Listener
	public void onDisplaceEntityEvent(DisplaceEntityEvent.TargetPlayer event) {
		if(!(event.getTargetEntity() instanceof Player)){
			return;
		}
		Player player = (Player) event.getTargetEntity();

		World worldSrc = event.getFromTransform().getExtent();
		World worldDest = event.getToTransform().getExtent();

		WorldProperties properties = worldDest.getProperties();
		
		if(worldSrc != worldDest){
			GameMode gamemode = GameModes.SURVIVAL;
			if(Main.getGame().getRegistry().getType(GameMode.class, properties.getGameRule("gamemode").get()).isPresent()){
				gamemode = Main.getGame().getRegistry().getType(GameMode.class, properties.getGameRule("gamemode").get()).get();
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
		WorldProperties properties = world.getProperties();
		
		if(!Boolean.parseBoolean(properties.getGameRule("pvp").get())){
			event.setCancelled(true);
		}
    }
	
	// CURRENTLY NOT WORKING - TEMP SOLUTION IN TASKS CLASS
	@Listener
	public void onChangeWorldWeatherEvent(ChangeWorldWeatherEvent event) {
		World world = event.getTargetWorld();
		WorldProperties properties = world.getProperties();
		
		String grWeather = properties.getGameRule("defaultWeather").get();
		
		if(!grWeather.equalsIgnoreCase("normal")){
			Weather weather = Main.getGame().getRegistry().getType(Weather.class, grWeather).get();
			world.forecast(weather);
		}
	}
	
	@Listener
	public void onRespawnPlayerEvent(RespawnPlayerEvent event) {
		if(!(event.getTargetEntity() instanceof Player)){
			return;
		}
		
		World world = event.getFromTransform().getExtent();
		WorldProperties properties = world.getProperties();
		
		String respawnWorldName = properties.getGameRule("respawnWorld").get();
		
		if(Main.getGame().getServer().getWorld(respawnWorldName).isPresent()){
			World respawnWorld = Main.getGame().getServer().getWorld(respawnWorldName).get();
			
			Transform<World> transform = event.getToTransform().setLocation(respawnWorld.getSpawnLocation());
			event.setToTransform(transform);
		}
	}
}
