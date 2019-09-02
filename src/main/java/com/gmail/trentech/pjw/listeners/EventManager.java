package com.gmail.trentech.pjw.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.PortalAgent;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.core.ConfigManager;
import com.gmail.trentech.pjc.core.TeleportManager;
import com.gmail.trentech.pjw.Main;

import ninja.leaping.configurate.ConfigurationNode;

public class EventManager {

	@Listener(order = Order.LAST)
	public void onClientConnectionEventJoin(ClientConnectionEvent.Join event) {
		Player player = event.getTargetEntity();

		ConfigurationNode node = ConfigManager.get(Main.getPlugin()).getConfig().getNode("options");

		String defaultWorld = Sponge.getServer().getDefaultWorld().get().getWorldName();

		boolean lobbyMode = node.getNode("lobby_mode").getBoolean();
		boolean firstJoin = !new File(defaultWorld + File.separator + "playerdata", player.getUniqueId().toString() + ".dat").exists();

		if (!firstJoin && !lobbyMode) {
			if (!player.hasPermission("pjw.override.gamemode")) {
				World world = player.getWorld();
				
				if(world.getGameRule("forceGamemode").get().equalsIgnoreCase("true")) {
					player.offer(Keys.GAME_MODE, player.getWorld().getProperties().getGameMode());
				}			
			}
			return;
		}

		if(firstJoin && !node.getNode("first_join", "enable").getBoolean()) {
			if (!player.hasPermission("pjw.override.gamemode")) {
				World world = player.getWorld();
				
				if(world.getGameRule("forceGamemode").get().equalsIgnoreCase("true")) {
					player.offer(Keys.GAME_MODE, player.getWorld().getProperties().getGameMode());
				}			
			}
			return;
		}
		
		String worldName = node.getNode("first_join", "world").getString();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(worldName);

		if (!optionalWorld.isPresent()) {
			return;
		}
		World world = optionalWorld.get();

		player.setLocationSafely(world.getSpawnLocation());

		if (!player.hasPermission("pjw.override.gamemode")) {
			if(world.getGameRule("forceGamemode").get().equalsIgnoreCase("true")) {
				player.offer(Keys.GAME_MODE, world.getProperties().getGameMode());
			}	
		}
	}

	@Listener
	public void onLoadWorldEvent(LoadWorldEvent event) {
		World world = event.getTargetWorld();
		WorldProperties properties = world.getProperties();
		
		List<WorldGeneratorModifier> modifiers = new ArrayList<>();
		
		boolean b = false;
		
		if(!properties.getGeneratorModifiers().isEmpty()) {
			for(WorldGeneratorModifier modifier : properties.getGeneratorModifiers()) {
				if(modifier.getId().equals("pjw:void")) {
					modifiers.add(Sponge.getRegistry().getType(WorldGeneratorModifier.class, "sponge:void").get());
					b = true;
				} else {
					modifiers.add(modifier);
				}
			}
		}

		if(b) {
			properties.setGeneratorModifiers(modifiers);
		}
		
		if (!properties.getGameRule("spawnOnDeath").isPresent()) {
			properties.setGameRule("spawnOnDeath", "default");
		}
		if (!properties.getGameRule("doWeatherCycle").isPresent()) {
			properties.setGameRule("doWeatherCycle", "true");
		}
		if (!properties.getGameRule("netherPortal").isPresent()) {
			properties.setGameRule("netherPortal", "default");
		}
		if (!properties.getGameRule("endPortal").isPresent()) {
			properties.setGameRule("endPortal", "default");
		}
		if (!properties.getGameRule("forceGamemode").isPresent()) {
			properties.setGameRule("forceGamemode", "false");
		}
	}

	@Listener
	public void onDamageEntityEvent(DamageEntityEvent event, @Root EntityDamageSource damageSource) {
		if (!(event.getTargetEntity() instanceof Player)) {
			return;
		}
		Player victim = (Player) event.getTargetEntity();

		if(!isValidPlayer(damageSource)) {
			return;
		}

		World world = victim.getWorld();
		WorldProperties properties = world.getProperties();

		if (!properties.isPVPEnabled() || victim.hasPermission("pjw.override.pvp")) {
			event.setCancelled(true);
		}
	}

	private boolean isValidPlayer(EntityDamageSource src) {
		if (src instanceof Player) {
			return true;
		} else if (src instanceof Projectile) {
			Projectile projectile = (Projectile) src;

			Optional<UUID> optionalUUID = projectile.getCreator();

			if (!optionalUUID.isPresent()) {
				return false;
			}

			Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(optionalUUID.get());

			if (!optionalPlayer.isPresent()) {
				return false;
			}

			return true;
		}
		
		return false;
	}
	
	@Listener
	public void onChangeWorldWeatherEvent(ChangeWorldWeatherEvent event) {
		World world = event.getTargetWorld();
		WorldProperties properties = world.getProperties();

		if (properties.getGameRule("doWeatherCycle").get().equalsIgnoreCase("false")) {
			event.setCancelled(true);
		}
	}

	@Listener
	public void onRespawnPlayerEvent(RespawnPlayerEvent event) {
		World world = event.getFromTransform().getExtent();
		WorldProperties properties = world.getProperties();

		String worldName = properties.getGameRule("spawnOnDeath").get();

		Optional<World> optionalSpawnWorld = Sponge.getServer().getWorld(worldName);

		if (!optionalSpawnWorld.isPresent()) {
			return;
		}
		World spawnWorld = optionalSpawnWorld.get();

		Transform<World> transform = event.getToTransform().setLocation(spawnWorld.getSpawnLocation());
		event.setToTransform(transform);
	}

	@Listener(order = Order.LAST)
	public void onRespawnPlayerEvent2(RespawnPlayerEvent event) {
		Player player = event.getTargetEntity();
		
		if (player.hasPermission("pjw.override.gamemode")) {
			return;
		}

		World from = event.getFromTransform().getExtent();
		World to = event.getToTransform().getExtent();

		WorldProperties properties = to.getProperties();

		if (!from.equals(to)) {
			if(properties.getGameRule("forceGamemode").get().equalsIgnoreCase("true")) {
				if (!properties.getGameMode().equals(player.gameMode().get())) {
					player.offer(Keys.GAME_MODE, properties.getGameMode());
				}
			}
		}
	}
	
	@Listener
	public void onMoveEntityEventEventTeleport(MoveEntityEvent.Teleport event, @Getter("getTargetEntity") Player player) {
		if (player.hasPermission("pjw.override.gamemode")) {
			return;
		}

		World from = event.getFromTransform().getExtent();
		World to = event.getToTransform().getExtent();

		WorldProperties properties = to.getProperties();

		if (!from.equals(to)) {
			if (!player.hasPermission("pjw.worlds." + properties.getWorldName())) {
				player.sendMessage(Text.of(TextColors.RED, "You do not have permission to travel to ", properties.getWorldName()));
				event.setCancelled(true);
				return;
			}
			
			if(properties.getGameRule("forceGamemode").get().equalsIgnoreCase("true")) {
				if (!properties.getGameMode().equals(player.gameMode().get())) {
					player.offer(Keys.GAME_MODE, properties.getGameMode());
				}
			}
		}
	}
	
	@Listener
	public void onMoveEntityEventPortal(MoveEntityEvent.Teleport.Portal event, @Getter("getTargetEntity") Player player) {
		World from = event.getFromTransform().getExtent();
		World to = event.getToTransform().getExtent();

		String toName;
		if (to.getName().equals("DIM-1")) {
			toName = from.getGameRule("netherPortal").get();
			
			if(toName.equals("DIM-1") || toName.equalsIgnoreCase("default")) {
				return;
			}
		} else if (to.getName().equals("DIM1")) {
			toName = from.getGameRule("endPortal").get();
			
			if(toName.equals("DIM1") || toName.equalsIgnoreCase("default")) {
				return;
			}
		} else {
			return;
		}

		Optional<World> optionalWorld = Sponge.getServer().getWorld(toName);

		if (!optionalWorld.isPresent()) {
			return;
		}
		World world = optionalWorld.get();

		Optional<Location<World>> optionalLocation = TeleportManager.getRandomLocation(world, 2000);

		Location<World> location;
		
		if(!optionalLocation.isPresent()) {
			location = world.getSpawnLocation();
		} else {
			location = optionalLocation.get();
		}

		PortalAgent portalAgent = event.getPortalAgent();

		optionalLocation = portalAgent.findPortal(location);

		if(!optionalLocation.isPresent()) {
			optionalLocation = portalAgent.createPortal(location);
			
			if(!optionalLocation.isPresent()) {
				event.setCancelled(true);
				return;
			}
		}
		
		location = optionalLocation.get();	
		
		Transform<World> transform = new Transform<>(location.getExtent(), location.getPosition());
		
		event.setToTransform(transform);

		event.setUsePortalAgent(true);
	}
}
