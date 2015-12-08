package com.gmail.trentech.pjw.managers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.entity.living.player.RespawnPlayerEvent;
import org.spongepowered.api.event.world.ChangeWorldWeatherEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weather;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Portal;

import ninja.leaping.configurate.ConfigurationNode;

public class EventManager {

	@Listener
	public void onPlayerPlaceBlock(ChangeBlockEvent.Place event) {
		if (!event.getCause().first(Player.class).isPresent()) {
			return;
		}
		Player player = event.getCause().first(Player.class).get();

		ConfigManager loader = new ConfigManager("portals.conf");
		ConfigurationNode config =  loader.getConfig();
		
		Set<Portal> portals = Main.getPortalsList();
		for (Portal portal : portals) {
			if (portal.getPlayerUUID().equals(player.getUniqueId())) {
				for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
					Main.getPortalsList().remove(portal);
					
					Location<World> location = transaction.getFinal().getLocation().get();
					
					String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

					if(config.getNode("Portals", name).getString() != null){
						config.getNode("Portals", name).setValue(null);
						portal.getBlockData().put(transaction.getFinal(), true);
						player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Portal block deleted"));
					}else{
						if(portal.getWorld() != null){
							config.getNode("Portals", name).setValue(portal.getWorld());
							portal.getBlockData().put(transaction.getFinal(), false);
							player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Portal block placed"));
						}
					}

					Main.getPortalsList().add(portal);

					loader.save();
				}
				return;
			}
		}

		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();
			
			String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(config.getNode("Portals", name).getString() != null){
				event.setCancelled(true);
			}
		}
	}
	
	@Listener
	public void onPlayerBreakBlock(ChangeBlockEvent.Break event) {
		if (!event.getCause().first(Player.class).isPresent()) {
			return;
		}
		Player player = event.getCause().first(Player.class).get();
		
		ConfigManager loader = new ConfigManager("portals.conf");
		ConfigurationNode config =  loader.getConfig();
		
		Set<Portal> portals = Main.getPortalsList();
		for (Portal portal : portals) {
			if (portal.getPlayerUUID().equals(player.getUniqueId())) {
				for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
					Location<World> location = transaction.getFinal().getLocation().get();
					
					String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

					if(config.getNode("Portals", name).getString() != null){
						config.getNode("Portals", name).setValue(null);
						player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Portal block deleted"));
					}

					loader.save();
				}
				return;
			}
		}

		List<Player> list = Main.getPlayersList();
		for(Player playerRan : list){
			if(playerRan == player){
				Main.getPlayersList().remove(playerRan);
				return;
			}
		}
		
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			Location<World> location = transaction.getFinal().getLocation().get();
			
			String name = location.getExtent().getName() + "." + location.getBlockX() + "." + location.getBlockY() + "." + location.getBlockZ();

			if(config.getNode("Portals", name).getString() != null){
				event.setCancelled(true);
			}
		}	
	}
	
	@Listener
	public void onPlayerMove(DisplaceEntityEvent event){
		if (!(event.getTargetEntity() instanceof Player)){
			return;
		}
		Player player = (Player) event.getTargetEntity();
		
		ConfigurationNode config = new ConfigManager("portals.conf").getConfig();
		String location = player.getLocation().getExtent().getName() + "." + player.getLocation().getBlockX() + "." + player.getLocation().getBlockY() + "." + player.getLocation().getBlockZ();

		if(config.getNode("Portals", location).getString() == null){
			return;
		}
		
		String worldName = config.getNode("Portals", location).getString();
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			player.sendMessage(Texts.of(TextColors.DARK_RED, worldName, " does not exist"));
			return;
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();
		
		player.setLocationSafely(world.getSpawnLocation());
		
		player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, world.getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", world.getSpawnLocation().getBlockX(), ", y: ", world.getSpawnLocation().getBlockY(),", z: ", world.getSpawnLocation().getBlockZ())));
	}

	@Listener
	public void onRespawnPlayerEvent(RespawnPlayerEvent event) {
		if(!(event.getTargetEntity() instanceof Player)){
			return;
		}

		String worldName = new ConfigManager().getConfig().getNode("Options", "Respawn").getString();
		
		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			World world = Main.getGame().getServer().getWorld(worldName).get();

			Transform<World> transform = event.getToTransform().setLocation(world.getSpawnLocation());
			event.setToTransform(transform);
		}
	}
	
	// TEMPORARY FIX FOR WORLD SPECIFIC GAMEMODES
	@Listener
	public void onDisplaceEntityEvent(DisplaceEntityEvent.TargetPlayer event) {
		if(!(event.getTargetEntity() instanceof Player)){
			return;
		}
		Player player = (Player) event.getTargetEntity();
		
		World worldSrc = event.getFromTransform().getExtent();
		World worldDest = event.getToTransform().getExtent();
		
		if(worldSrc != worldDest){
			GameMode gamemode = Main.getGame().getRegistry().getType(GameMode.class, new ConfigManager().getConfig().getNode("Worlds", worldDest.getName(), "Gamemode").getString()).get();
			if(player.getGameModeData().type().get() != gamemode){
				player.offer(Keys.GAME_MODE, gamemode);
			}		
		}
	}
	
	// CURRENTLY NOT WORKING - TEMP SOLUTION IN TASKS CLASS
	@Listener
	public void onChangeWorldWeatherEvent(ChangeWorldWeatherEvent event) {
		World world = event.getTargetWorld();
		
		ConfigurationNode config = new ConfigManager().getConfig();
		
		if(config.getNode("Worlds", world.getName(), "Weather", "Lock").getBoolean()){
			Weather weather = Main.getGame().getRegistry().getType(Weather.class, config.getNode("Worlds", world.getName(), "Weather", "Set").getString()).get();
			world.forecast(weather);
		}
	}
	
	@Listener
	public void onSignChangeEvent(ChangeSignEvent event) {
		Player player = null;
		Optional<Player> playerOptional = event.getCause().first(Player.class);
		if(playerOptional.isPresent()){
			player = playerOptional.get();
		}

		SignData signData = event.getText();

		ListValue<Text> lines = signData.getValue(Keys.SIGN_LINES).get();

		Text portalSign = Texts.of("[Portal]");
		if(!lines.get(0).equals(portalSign)) {
			return;
		}

		if(!player.hasPermission("pjw.sign.place")) {
			player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to place portal signs"));
			event.setCancelled(true);
			return;
		}
		
		if(!Main.getGame().getServer().getWorld(Texts.toPlain(lines.get(1))).isPresent()){
			player.sendMessage(Texts.of(TextColors.DARK_RED, Texts.toPlain(lines.get(1)), " does not exist"));
			event.setCancelled(true);
			return;
		}

		lines.set(0, Texts.of(TextColors.DARK_BLUE, "[Portal]"));

		event.getText().set(lines);
	}

	@Listener
	public void onSignInteractEvent(InteractBlockEvent.Secondary event) {
		if(!(event.getTargetBlock().getState().getType().equals(BlockTypes.WALL_SIGN) || event.getTargetBlock().getState().getType().equals(BlockTypes.STANDING_SIGN))){
			return;
		}

		Optional<Player> playerOptional = event.getCause().first(Player.class);
		if(!playerOptional.isPresent()){
			return;
			
		}
		Player player = playerOptional.get();

		Location<World> block = event.getTargetBlock().getLocation().get();

		Optional<SignData> data = block.getOrCreate(SignData.class);
		if(!data.isPresent()){
			return;
		}
		SignData signData = data.get();

		ListValue<Text> lines = signData.getValue(Keys.SIGN_LINES).get();

		Text portalSign = Texts.of(TextColors.DARK_BLUE, "[Portal]");
		if(!lines.get(0).equals(portalSign)) {
        	return;
		}

		if(!player.hasPermission("pjw.sign.interact")) {
			player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to interact with portal signs"));
			event.setCancelled(true);
			return;
		}
		
		if(!Main.getGame().getServer().getWorld(Texts.toPlain(lines.get(1))).isPresent()){
			player.sendMessage(Texts.of(TextColors.DARK_RED, Texts.toPlain(lines.get(1)), " does not exist"));
			event.setCancelled(true);
			return;
		}
		
		World world = Main.getGame().getServer().getWorld(Texts.toPlain(lines.get(1))).get();
		
		player.setLocationSafely(world.getSpawnLocation());
		player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, world.getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", world.getSpawnLocation().getBlockX(), ", y: ", world.getSpawnLocation().getBlockY(),", z: ", world.getSpawnLocation().getBlockZ())));
	}
	
	@Listener
	public void onSignBreakEvent(ChangeBlockEvent.Break event) {
		Optional<Player> playerOptional = event.getCause().first(Player.class);
	    if (!playerOptional.isPresent()) {
	    	return;
	    }

	    SignData signData = null;
	    for(Transaction<BlockSnapshot> blockTransaction : event.getTransactions()){
	    	Optional<Location<World>> blockOptional = blockTransaction.getOriginal().getLocation();	    	
	    	if(blockOptional.isPresent()){
	    		
	    		Location<World> block = blockOptional.get();
	    		if(block.getBlock().getType().equals(BlockTypes.WALL_SIGN) || block.getBlock().getType().equals(BlockTypes.STANDING_SIGN)){
    				Optional<SignData> signDataOptional = block.getOrCreate(SignData.class);
    				if(signDataOptional.isPresent()){
    					signData = signDataOptional.get();
    					break;
    				}
	    		}
	    	}
	    }

	    if(signData == null){
	    	return;
	    }
	    
		ListValue<Text> lines = signData.getValue(Keys.SIGN_LINES).get();

		Text kitSign = Texts.of(TextColors.DARK_BLUE, "[Portal]");
		if(!lines.get(0).equals(kitSign)) {
        	return;
		}

		Player player = playerOptional.get();
		if(!player.hasPermission("pjw.sign.break")) {
			player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to break portal signs"));
			event.setCancelled(true);
			return;
		}
	}
}
