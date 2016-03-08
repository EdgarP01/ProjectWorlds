package com.gmail.trentech.pjw.listeners;

import java.io.File;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent.TargetPlayer;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.TeleportHelper;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.commands.CMDTeleport;
import com.gmail.trentech.pjw.events.TeleportEvent;
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class EventManager {

	@Listener
	public void onTeleportEvent(TeleportEvent event){
		Player player = event.getTarget();

		Location<World> src = event.getSource();
		Location<World> dest = event.getDestination();

		if(!player.hasPermission("pjw.worlds." + dest.getExtent().getName())){
			player.sendMessage(Text.of(TextColors.DARK_RED, "You do not have permission to travel to ", dest.getExtent().getName()));
			event.setCancelled(true);
			return;
		}
		
		TeleportHelper teleportHelper = Main.getGame().getTeleportHelper();
		
		Optional<Location<World>> optionalLocation = teleportHelper.getSafeLocation(dest);

		if(!optionalLocation.isPresent()){
			CMDTeleport.players.put(player, dest);
			player.sendMessage(Text.builder().color(TextColors.DARK_RED).append(Text.of("Unsafe spawn point detected. Teleport anyway? "))
					.onClick(TextActions.runCommand("/pjw:world teleport confirm")).append(Text.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
			event.setCancelled(true);
			return;
		}

		player.sendTitle(Title.of(Text.of(TextColors.DARK_GREEN, dest.getExtent().getName()), Text.of(TextColors.AQUA, "x: ", dest.getBlockX(), ", y: ", dest.getBlockY(),", z: ", dest.getBlockZ())));
		
		TargetPlayer displaceEvent = SpongeEventFactory.createDisplaceEntityEventTargetPlayer(Cause.of(NamedCause.source(this)), new Transform<World>(src), new Transform<World>(dest), player);
		Main.getGame().getEventManager().post(displaceEvent);
	}

	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
	    Player player = event.getTargetEntity();

		if(player.hasPermission("pjw.options.motd")){
			player.sendMessage(Main.getGame().getServer().getMotd());
		}

		String defaultWorld = Main.getGame().getServer().getDefaultWorld().get().getWorldName();
		
		if(new File(defaultWorld + File.separator + "playerdata", player.getUniqueId().toString() + ".dat").exists()){
			return;
		}

		ConfigurationNode node = new ConfigManager().getConfig().getNode("options", "first_join");
		
		String worldName = node.getNode("world").getString();
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			return;
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		player.setLocationSafely(world.getSpawnLocation());
		
		Text title = TextSerializers.FORMATTING_CODE.deserialize(node.getNode("title").getString());
		Text subTitle = TextSerializers.FORMATTING_CODE.deserialize(node.getNode("sub_title").getString());

		player.sendTitle(Title.of(title, subTitle));		
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
	public void onDisplaceEntityEvent(DisplaceEntityEvent.Teleport.TargetPlayer event) {
		System.out.println("DisplaceEntityEvent.Teleport.TargetPlayer");
	}
	
	@Listener
	public void onDisplaceEntityEvent(DisplaceEntityEvent.TargetPlayer event) {
		Player player = event.getTargetEntity();

		if(!player.hasPermission("pjw.options.gamemode")){
			return;
		}
		
		World worldSrc = event.getFromTransform().getExtent();
		World worldDest = event.getToTransform().getExtent();

		WorldProperties properties = worldDest.getProperties();
		
		if(!worldSrc.equals(worldDest)){
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

		if(!properties.isPVPEnabled() && !victim.hasPermission("pjw.options.pvp")){
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
}
