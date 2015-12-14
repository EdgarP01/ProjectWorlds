package com.gmail.trentech.pjw.events;

import java.util.HashMap;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.commands.CMDYes;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Resource;

import ninja.leaping.configurate.ConfigurationNode;

public class PlateEventManager {

	public static HashMap<Player, String> creators = new HashMap<>();

	@Listener
	public void onChangeBlockEvent(ChangeBlockEvent.Modify event) {
		if (!event.getCause().first(Player.class).isPresent()) {
			return;
		}
		Player player = event.getCause().first(Player.class).get();
		
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot block = transaction.getFinal();

			if(!(block.getState().getType().getName().toUpperCase().contains("_PRESSURE_PLATE"))){
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

			ConfigurationNode config = new ConfigManager("portals.conf").getConfig();;
			
			if(config.getNode("Plates", locationName, "World").getString() == null){
				return;
			}
			
			String worldName = config.getNode("Plates", locationName, "World").getString();
			
			if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
				player.sendMessage(Texts.of(TextColors.DARK_RED, worldName, " does not exist"));
				return;
			}
			World world = Main.getGame().getServer().getWorld(worldName).get();
			
			if(!player.hasPermission("pjw.plate.interact." + worldName)){
				player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
				return;
			}
			
			Location<World> playerLocation = player.getLocation();
			
			if(!player.setLocationSafely(world.getSpawnLocation())){
				CMDYes.players.put(player, world.getSpawnLocation());
				player.sendMessage(Texts.builder().color(TextColors.DARK_RED).append(Texts.of("Unsafe spawn point detected. Teleport anyway? ")).onClick(TextActions.runCommand("/yes")).append(Texts.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
				return;
			}
			
			Resource.particles(playerLocation);
			Resource.particles(player.getLocation());
			
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

			if(config.getNode("Plates", locationName, "World").getString() != null){
				if(!player.hasPermission("pjw.plate.break")){
					player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
					event.setCancelled(true);
				}else{
					config.getNode("Plates", locationName).setValue(null);
					loader.save();
					player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Broke teleport pressure plate"));
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
			if((transaction.getFinal().getState().getType().getName().toUpperCase().contains("_PRESSURE_PLATE"))){		
				Location<World> location = transaction.getFinal().getLocation().get();
				
				if(!player.hasPermission("pjw.button.place." + location.getExtent().getName())){
		        	player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to create teleport pressure playes in this world"));
		        	return;
				}
				
				String locationName = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

	            ConfigManager loader = new ConfigManager("portals.conf");
	            ConfigurationNode config = loader.getConfig();

	            config.getNode("Plates", locationName, "World").setValue(creators.get(player));

	            loader.save();
	          
	            location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(.2,.2,.4));
	            location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(.3,.8,.2));
	            location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(.7,.6,.9));
	            location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(.6,.3,.6));
	            location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(.4,.9,.5));
	            
	            player.sendMessage(Texts.of(TextColors.DARK_GREEN, "New teleport pressure plate created"));
	            
	            creators.remove(player);
			}
		}
	}
}
