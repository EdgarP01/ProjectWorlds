package com.gmail.trentech.pjw.listeners;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.events.PortalConstructEvent;
import com.gmail.trentech.pjw.events.TeleportEvent;
import com.gmail.trentech.pjw.portal.Portal;
import com.gmail.trentech.pjw.portal.PortalBuilder;
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class PortalEventManager {

	@Listener
	public void onPortalConstructEvent(PortalConstructEvent event, @First Player player){
		if(!player.hasPermission("pjw.portal.create." + player.getWorld().getName())){
			player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to create portals in this world"));
        	event.setCancelled(true);
        	return;
		}
		
        if(event.getLocations() == null){
        	player.sendMessage(Texts.of(TextColors.DARK_RED, "Portals cannot over lap over portals"));
        	event.setCancelled(true);
        	return;
        }
        
        ConfigurationNode config = new ConfigManager().getConfig();
        
        int size = config.getNode("Options", "Portal", "Size").getInt();
        if(event.getLocations().size() > size){
        	player.sendMessage(Texts.of(TextColors.DARK_RED, "Portals cannot be larger than ", size, " blocks"));
        	event.setCancelled(true);
        	return;
        }
        
        PortalBuilder.getActiveBuilders().remove(player);
        PortalBuilder.getCreators().add(player);
        
        player.sendMessage(Texts.of(TextColors.DARK_GREEN, "New portal created"));
	}
	
	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Place event, @First Player player) {
		if(PortalBuilder.getCreators().contains(player)){
			PortalBuilder.getCreators().remove(player);
			return;
		}

		ConfigManager loader = new ConfigManager("portals.conf");

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(loader.getPortal(locationName) == null){
				continue;
			}
			
			if(!player.hasPermission("pjw.portal.place")){
				player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
				event.setCancelled(true);
			}
//			else{
//				loader.removePortalLocation(locationName);
//				player.sendMessage(Texts.of(TextColors.DARK_RED, "Broke Portal"));
//			}
		}
	}
	
	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break event, @First Player player) {
		if(PortalBuilder.getCreators().contains(player)){
			PortalBuilder.getCreators().remove(player);
			return;
		}

		ConfigManager loader = new ConfigManager("portals.conf");

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(loader.getPortal(locationName) == null){
				continue;
			}
			
			if(!player.hasPermission("pjw.portal.break")){
				player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
				event.setCancelled(true);
			}
//			else{
//				loader.removePortalLocation(locationName);
//				player.sendMessage(Texts.of(TextColors.DARK_RED, "Broke Portal"));
//			}
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
		
		Main.getGame().getEventManager().post(new TeleportEvent(player.getLocation(), world.getSpawnLocation(), Cause.of(player)));
	}

	@Listener
	public void onInteractBlockEvent(InteractBlockEvent.Secondary event, @First Player player) {
		if(PortalBuilder.getActiveBuilders().get(player) == null){
			return;
		}
		
		PortalBuilder builder = PortalBuilder.getActiveBuilders().get(player);
		
        ConfigManager loaderPortals = new ConfigManager("portals.conf");

		if(builder.getWorld() == null){
        	Location<World> location = event.getTargetBlock().getLocation().get();
        	String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
        	
        	if(loaderPortals.removePortalLocation(locationName)){
				PortalBuilder.getActiveBuilders().remove(player);
				
                player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Portal has been removed"));
        	}
		}else if(builder.getLocation() == null){
			builder.setLocation(event.getTargetBlock().getLocation().get());
			
			PortalBuilder.getActiveBuilders().put(player, builder);
			
			player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Starting point selected"));
		}else{
			Portal portal = new Portal(event.getTargetBlock().getState(), builder.getWorld(), builder.getLocation(), event.getTargetBlock().getLocation().get());

			boolean portalConstructEvent = Main.getGame().getEventManager().post(new PortalConstructEvent(portal.getLocations(), Cause.of(player)));
			if(!portalConstructEvent) {
				portal.build();
			}
		}
	}
}
