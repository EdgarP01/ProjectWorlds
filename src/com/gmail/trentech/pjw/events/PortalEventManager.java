package com.gmail.trentech.pjw.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.spongepowered.api.block.BlockSnapshot;
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
import com.gmail.trentech.pjw.portal.Cuboid;
import com.gmail.trentech.pjw.portal.PortalBuilder;
import com.gmail.trentech.pjw.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class PortalEventManager {

	@SuppressWarnings({ "unchecked" })
	@Listener
	public void onPortalPlaceBlockEvent(ChangeBlockEvent.Place event) {
		if (!event.getCause().first(Player.class).isPresent()) {
			return;
		}

		ConfigManager loader = new ConfigManager("portals.conf");
		ConfigurationNode config =  loader.getConfig();

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
				Object object = config.getNode("Portals", node.getKey().toString(), "Locations").getValue();
				
		    	ArrayList<String> list = null;
		    	if(object instanceof ArrayList) {
		    		list = (ArrayList<String>) object;
		    	}
				for(String loc : list){
					if(loc.equalsIgnoreCase(locationName)){
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Listener
	public void onPortalBreakBlockEvent(ChangeBlockEvent.Break event) {
		if (!event.getCause().first(Player.class).isPresent()) {
			return;
		}

		ConfigManager loader = new ConfigManager("portals.conf");
		ConfigurationNode config =  loader.getConfig();

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
				Object object = config.getNode("Portals", node.getKey().toString(), "Locations").getValue();
				
		    	ArrayList<String> list = null;
		    	if(object instanceof ArrayList) {
		    		list = (ArrayList<String>) object;
		    	}
				for(String loc : list){
					if(loc.equalsIgnoreCase(locationName)){
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Listener
	public void onPortalDisplaceEvent(DisplaceEntityEvent event){
		if (!(event.getTargetEntity() instanceof Player)){
			return;
		}
		Player player = (Player) event.getTargetEntity();
		
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();

		Location<World> location = player.getLocation();		
		String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
			String uuid = node.getKey().toString();
			Object object = config.getNode("Portals", uuid, "Locations").getValue();
			
	    	ArrayList<String> list = null;
	    	if(object instanceof ArrayList) {
	    		list = (ArrayList<String>) object;
	    	}
	    	
			for(String loc : list){
				if(loc.equalsIgnoreCase(locationName)){
					String worldName = config.getNode("Portals", uuid, "World").getString();
					
					if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
						player.sendMessage(Texts.of(TextColors.DARK_RED, worldName, " does not exist"));
						return;
					}
					World world = Main.getGame().getServer().getWorld(worldName).get();
					
					player.setLocationSafely(world.getSpawnLocation());
					
					player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, world.getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", world.getSpawnLocation().getBlockX(), ", y: ", world.getSpawnLocation().getBlockY(),", z: ", world.getSpawnLocation().getBlockZ())));
					return;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Listener
	public void onPortalInteractEvent(InteractBlockEvent.Secondary event) {
		if(!(event.getCause().first(Player.class).isPresent())){
			return;
		}
		Player player = (Player) event.getCause().first(Player.class).get();

		if(Main.getActiveBuilders().get(player) != null){
			PortalBuilder builder = Main.getActiveBuilders().get(player);
            ConfigManager loader = new ConfigManager("portals.conf");
    		ConfigurationNode config = loader.getConfig();
    		
			if(builder.getWorld() == null){
            	Location<World> location = event.getTargetBlock().getLocation().get();
            	String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();
            	
        		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
        			String uuid = node.getKey().toString();
        			Object object = config.getNode("Portals", uuid, "Locations").getValue();
        			
        	    	ArrayList<String> list = null;
        	    	if(object instanceof ArrayList) {
        	    		list = (ArrayList<String>) object;
        	    	}
        	    	
        			for(String loc : list){
        				if(loc.equalsIgnoreCase(locationName)){
        					config.getNode("Portals", uuid).setValue(null);
        					loader.save();
        	                Main.getActiveBuilders().remove(player);
        	                player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Portal has been removed"));
        					return;
        				}
        			}
        		}
			}else if(builder.getLocation() == null){
				builder.setLocation(event.getTargetBlock().getLocation().get());
				Main.getActiveBuilders().put(player, builder);
				player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Starting point selected"));
			}else{
				List<String> locations = new ArrayList<>();
				
				Cuboid cuboid = new Cuboid(builder.getLocation(), event.getTargetBlock().getLocation().get());

                for (BlockSnapshot block : cuboid){
                	Location<World> location = block.getLocation().get();

                	if(block.getState().getType() == BlockTypes.AIR){
                		// CURRENTLY NOT WORKING
                		location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.CLOUD).build(), location.getPosition(), 1);
                	}

                	String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

            		for(Entry<Object, ? extends ConfigurationNode> node : config.getNode("Portals").getChildrenMap().entrySet()){
            			String uuid = node.getKey().toString();
            			Object object = config.getNode("Portals", uuid, "Locations").getValue();
            			
            	    	ArrayList<String> list = null;
            	    	if(object instanceof ArrayList) {
            	    		list = (ArrayList<String>) object;
            	    	}
            	    	
            			for(String loc : list){
            				if(loc.equalsIgnoreCase(locationName)){
            	                Main.getActiveBuilders().remove(player);
            	                player.sendMessage(Texts.of(TextColors.DARK_RED, "Portals cannot overlap"));
            					return;
            				}
            			}
            		}
            		locations.add(locationName);
                }
                String uuid = UUID.randomUUID().toString();
                config.getNode("Portals", uuid, "Locations").setValue(locations);
                config.getNode("Portals", uuid, "World").setValue(builder.getWorld());

                Main.getActiveBuilders().remove(player);

        		loader.save();
                player.sendMessage(Texts.of(TextColors.DARK_GREEN, "New portal created"));
			}
		}
	}
}
