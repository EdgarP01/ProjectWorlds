package com.gmail.trentech.pjw.listeners;

import java.util.HashMap;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.events.TeleportEvent;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Resource;

import ninja.leaping.configurate.ConfigurationNode;

public class ButtonEventManager {

	public static HashMap<Player, String> creators = new HashMap<>();

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Modify event) {
		if (!event.getCause().first(Player.class).isPresent()) {
			return;
		}
		Player player = event.getCause().first(Player.class).get();
		
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot block = transaction.getFinal();

			if(!(block.getState().getType().getName().toUpperCase().contains("_BUTTON"))){
				return;
			}

			if(!block.get(Keys.POWERED).isPresent()){
				return;
			}

			if(!block.get(Keys.POWERED).get()){
				return;
			}
			
			Location<World> location = block.getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

	        ConfigurationNode config = new ConfigManager("portals.conf").getConfig();

			if(config.getNode("Buttons", locationName, "World").getString() == null){
				return;
			}
			
			String worldName = config.getNode("Buttons", locationName, "World").getString();
			
			if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
				player.sendMessage(Texts.of(TextColors.DARK_RED, worldName, " does not exist"));
				return;
			}
			World world = Main.getGame().getServer().getWorld(worldName).get();
			
			if(!player.hasPermission("pjw.button.interact." + worldName)){
				player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
				return;
			}

			Main.getGame().getEventManager().post(new TeleportEvent(player, player.getLocation(), world.getSpawnLocation()));
		}
	}
	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Break event) {
		if (!event.getCause().first(Player.class).isPresent()) {
			return;
		}
		Player player = event.getCause().first(Player.class).get();
		
		if(creators.containsKey(player)){
			creators.remove(player);
			return;
		}

		ConfigManager loader = new ConfigManager("portals.conf");
		ConfigurationNode config = loader.getConfig();
		
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();		
			String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(config.getNode("Buttons", locationName, "World").getString() != null){
				if(!player.hasPermission("pjw.button.break")){
					player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
					event.setCancelled(true);
				}else{
					config.getNode("Buttons", locationName).setValue(null);
					loader.save();
					player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Broke teleport button"));
				}
			}
		}
	}

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Place event) {
		if (!event.getCause().first(Player.class).isPresent()) {
			return;
		}
		Player player = event.getCause().first(Player.class).get();
		
		if(!creators.containsKey(player)){
			return;
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			if((transaction.getFinal().getState().getType().getName().toUpperCase().contains("_BUTTON"))){
				Location<World> location = transaction.getFinal().getLocation().get();

				if(!player.hasPermission("pjw.button.place." + location.getExtent().getName())){
		        	player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to create teleport buttons in this world"));
		        	return;
				}

				String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

	            ConfigManager loader = new ConfigManager("portals.conf");
	            ConfigurationNode config = loader.getConfig();

	            config.getNode("Buttons", locationName, "World").setValue(creators.get(player));

	            loader.save();
	            
	            Resource.spawnParticles(location, 1.0, false);
	    		
	            player.sendMessage(Texts.of(TextColors.DARK_GREEN, "New teleport button created"));
	            
	            creators.remove(player);
			}
		}
	}
}
