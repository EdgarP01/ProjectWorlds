package com.gmail.trentech.pjw.listeners;

import java.util.Random;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.commands.CMDTeleport;
import com.gmail.trentech.pjw.events.TeleportEvent;
import com.gmail.trentech.pjw.utils.ConfigManager;

public class EventManager {

	@Listener
	public void onTeleportEvent(TeleportEvent event, @First Player player){
		Location<World> src = event.getSrc();
		Location<World> dest = event.getDest();

		if(!player.hasPermission("pjw.worlds." + dest.getExtent().getName())){
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to travel to ", dest.getExtent().getName()));
			return;
		}
		
		if(!player.setLocationSafely(dest)){
			CMDTeleport.players.put(player, dest);
			player.sendMessage(Text.builder().color(TextColors.DARK_RED).append(Text.of("Unsafe spawn point detected. Teleport anyway? ")).onClick(TextActions.runCommand("/pjw:world teleport confirm")).append(Text.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
			return;
		}
		
		if(new ConfigManager().getConfig().getNode("Options", "Show-Particles").getBoolean()){
			spawnParticles(src, 0.5, true);
			spawnParticles(src.getRelative(Direction.UP), 0.5, true);
			
			spawnParticles(dest, 1.0, false);
			spawnParticles(dest.getRelative(Direction.UP), 1.0, false);
		}

		player.sendTitle(Title.of(Text.of(TextColors.DARK_GREEN, dest.getExtent().getName()), Text.of(TextColors.AQUA, "x: ", dest.getBlockX(), ", y: ", dest.getBlockY(),", z: ", dest.getBlockZ())));
	}

	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
	    Player player = event.getTargetEntity();

	    // NOT IMPLEMENTED YET
		if(player.get(JoinData.class).isPresent()){
			System.out.println("JOIN DATA PRESENT");
			return;
		}
		
//		String worldName = new ConfigManager().getConfig().getNode("Options", "Join-Spawn").getString();
//		
//		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
//			return;
//		}
//		World world = Main.getGame().getServer().getWorld(worldName).get();
//		
//		player.setLocationSafely(world.getSpawnLocation());
	}
	
	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event){
		World world = event.getTargetWorld();
		
		WorldProperties properties = world.getProperties();

		if(!properties.getGameRule("respawnWorld").isPresent()){
			properties.setGameRule("respawnWorld", Main.getGame().getServer().getDefaultWorld().get().getWorldName());
		}

		if(!properties.getGameRule("doWeatherCycle").isPresent()){
			properties.setGameRule("doWeatherCycle", "true");
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
			if(!properties.getGameMode().equals(player.gameMode().get())){
				player.offer(Keys.GAME_MODE, properties.getGameMode());
			}			
		}
	}
	
    @Listener
    public void onDamageEntityEvent(DamageEntityEvent event, @First EntityDamageSource damageSource) {
    	if(!(event.getTargetEntity() instanceof Player)) {
    		return;
    	}
    	Player victim = (Player) event.getTargetEntity();

        Entity source = damageSource.getSource();
        if (!(source instanceof Player)) {
        	return;
        }

        World world = victim.getWorld();
		WorldProperties properties = world.getProperties();

		if(!properties.isPVPEnabled()){
			event.setCancelled(true);
		}
    }

	@Listener
	public void onChangeWorldWeatherEvent(ChangeWorldWeatherEvent event) {
		World world = event.getTargetWorld();
		WorldProperties properties = world.getProperties();

		if(properties.getGameRule("doWeatherCycle").get().equalsIgnoreCase("false")){
			event.setCancelled(true);
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
	
	private void spawnParticles(Location<World> location, double range, boolean sub){
		
		Random random = new Random();
		
		for(int i = 0; i < 5; i++){
			double v1 = 0.0 + (range - 0.0) * random.nextDouble();
			double v2 = 0.0 + (range - 0.0) * random.nextDouble();
			double v3 = 0.0 + (range - 0.0) * random.nextDouble();

			location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
					.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(v3,v1,v2));
			location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
					.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(0,v1,0));
			if(sub){
				location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
						.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().sub(v1,v2,v3));
				location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
						.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().sub(0,v2,0));
			}else{
				location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
						.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(v3,v1,v1));
				location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
						.type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(v2,v3,v2));
			}
		}
	}
}
