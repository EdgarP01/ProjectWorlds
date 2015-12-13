package com.gmail.trentech.pjw.events;

import java.util.HashMap;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;

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

			if((block.getState().getType() != BlockTypes.WOODEN_BUTTON) && (block.getState().getType() != BlockTypes.STONE_BUTTON)){
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

			player.setLocationSafely(world.getSpawnLocation());
			
			player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, world.getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", world.getSpawnLocation().getBlockX(), ", y: ", world.getSpawnLocation().getBlockY(),", z: ", world.getSpawnLocation().getBlockZ())));
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
			if((transaction.getFinal().getState().getType() == BlockTypes.WOODEN_BUTTON) || (transaction.getFinal().getState().getType() == BlockTypes.STONE_BUTTON)){				
				Location<World> location = transaction.getFinal().getLocation().get();		
				String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

	            ConfigManager loader = new ConfigManager("portals.conf");
	            ConfigurationNode config = loader.getConfig();

	            config.getNode("Buttons", locationName, "World").setValue(creators.get(player));

	            loader.save();
	          
	            location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.EXPLOSION_LARGE).build(), location.getPosition().add(0, 1, 0));
	            player.sendMessage(Texts.of(TextColors.DARK_GREEN, "New teleport button created"));
	            
	            creators.remove(player);
			}
		}
	}
}
