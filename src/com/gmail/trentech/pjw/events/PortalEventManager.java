package com.gmail.trentech.pjw.events;

import java.util.List;
import java.util.UUID;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.portal.Portal;
import com.gmail.trentech.pjw.portal.PortalBuilder;
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class PortalEventManager {

	
	
	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Place event) {
		if (!event.getCause().first(Player.class).isPresent()) {
			return;
		}
		Player player = event.getCause().first(Player.class).get();
		
		if(PortalBuilder.getCreators().contains(player)){
			PortalBuilder.getCreators().remove(player);
			return;
		}

		ConfigManager loader = new ConfigManager("portals.conf");

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(loader.portalExists(locationName)){
				if(!player.hasPermission("pjw.portal.place")){
					player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
					event.setCancelled(true);
				}else{
					loader.removeLocation(locationName);
					player.sendMessage(Texts.of(TextColors.DARK_RED, "Broke Portal"));
				}
			}
		}
	}
	
	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break event) {
		if (!event.getCause().first(Player.class).isPresent()) {
			return;
		}
		Player player = event.getCause().first(Player.class).get();
		
		if(PortalBuilder.getCreators().contains(player)){
			PortalBuilder.getCreators().remove(player);
			return;
		}

		ConfigManager loader = new ConfigManager("portals.conf");

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(loader.portalExists(locationName)){
				if(!player.hasPermission("pjw.portal.break")){
					player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
					event.setCancelled(true);
				}else{
					loader.removeLocation(locationName);
					player.sendMessage(Texts.of(TextColors.DARK_RED, "Broke Portal"));
				}
			}
		}
	}

	@Listener
	public void onDisplaceEntityEvent(DisplaceEntityEvent.TargetPlayer event){
		if (!(event.getTargetEntity() instanceof Player)){
			return;
		}
		Player player = (Player) event.getTargetEntity();
		
		ConfigManager loader = new ConfigManager("portals.conf");

		Location<World> location = player.getLocation();		
		String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

		if(loader.getPortal(locationName) == null){
			return;
		}		
		String worldName = loader.getPortal(locationName);
		
		if(!player.hasPermission("pjw.portal.interact." + worldName)){
			player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
			return;
		}
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			player.sendMessage(Texts.of(TextColors.DARK_RED, worldName, " does not exist"));
			return;
		}
		
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		player.setLocationSafely(world.getSpawnLocation());
		
		player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, world.getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", world.getSpawnLocation().getBlockX(), ", y: ", world.getSpawnLocation().getBlockY(),", z: ", world.getSpawnLocation().getBlockZ())));
	}

	@Listener
	public void onInteractBlockEvent(InteractBlockEvent.Secondary event) {
		if(!(event.getCause().first(Player.class).isPresent())){
			return;
		}
		Player player = (Player) event.getCause().first(Player.class).get();

		if(PortalBuilder.getActiveBuilders().get(player) == null){
			return;
		}
		
		PortalBuilder builder = PortalBuilder.getActiveBuilders().get(player);
		
        ConfigManager loaderPortals = new ConfigManager("portals.conf");
		ConfigurationNode configPortals = loaderPortals.getConfig();
		
		if(builder.getWorld() == null){
        	Location<World> location = event.getTargetBlock().getLocation().get();
        	String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
        	
        	if(loaderPortals.removeLocation(locationName)){
				PortalBuilder.getActiveBuilders().remove(player);
				
                player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Portal has been removed"));
        	}
		}else if(builder.getLocation() == null){
			builder.setLocation(event.getTargetBlock().getLocation().get());
			
			PortalBuilder.getActiveBuilders().put(player, builder);
			
			player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Starting point selected"));
		}else{
			Portal portal = new Portal(builder.getLocation(), event.getTargetBlock().getLocation().get());

			PortalBuilder.getActiveBuilders().remove(player);
			
            if(portal.getLocations() == null){
                player.sendMessage(Texts.of(TextColors.DARK_RED, "Portals cannot over lap over portals"));
            	return;
            }
            List<String> locations = portal.getLocations();

            ConfigurationNode config = new ConfigManager().getConfig();
            
            int size = config.getNode("Options", "Portal", "Size").getInt();
            if(locations.size() > size){
            	player.sendMessage(Texts.of(TextColors.DARK_RED, "Portals cannot be larger than ", size, " blocks"));
            	return;
            }
            
            PortalBuilder.getCreators().add(player);
            
            for(String loc : locations){
            	String[] info = loc.split("\\.");

            	Location<World> location = Main.getGame().getServer().getWorld(info[0]).get().getLocation(Integer.parseInt(info[1]), Integer.parseInt(info[2]), Integer.parseInt(info[3]));
            	
            	if(config.getNode("Options", "Portal", "Replace-Frame").getBoolean()){	
                	if(location.getBlockType() != BlockTypes.AIR){
//                		if(Main.getGame().getRegistry().getType(BlockType.class, config.getNode("Options", "Portal", "Material").getString()).isPresent()){
//                			BlockType type = Main.getGame().getRegistry().getType(BlockType.class, new ConfigManager().getConfig().getNode("Options", "Portal-Frame").getString()).get();
//                			location.setBlock(Main.getGame().getRegistry().createBuilder(BlockState.Builder.class).blockType(type).build());
//                		}
            			location.setBlock(Main.getGame().getRegistry().createBuilder(BlockState.Builder.class).blockType(event.getTargetBlock().getState().getType()).build());
                	}
            	}
            	location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.EXPLOSION_LARGE).build(), location.getPosition());
            }
            
            String uuid = UUID.randomUUID().toString();
            configPortals.getNode("Portals", uuid, "Locations").setValue(locations);
            configPortals.getNode("Portals", uuid, "World").setValue(builder.getWorld());

            loaderPortals.save();
          
            player.sendMessage(Texts.of(TextColors.DARK_GREEN, "New portal created"));
		}
	}
}
