package com.gmail.trentech.pjw.events;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.commands.CMDYes;
import com.gmail.trentech.pjw.portal.Portal;
import com.gmail.trentech.pjw.portal.PortalBuilder;
import com.gmail.trentech.pjw.utils.ConfigManager;

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

			if(loader.getPortal(locationName) != null){
				if(!player.hasPermission("pjw.portal.place")){
					player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
					event.setCancelled(true);
				}else{
					loader.removePortalLocation(locationName);
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

			if(loader.getPortal(locationName) != null){
				if(!player.hasPermission("pjw.portal.break")){
					player.sendMessage(Texts.of(TextColors.DARK_RED, "you do not have permission"));
					event.setCancelled(true);
				}else{
					loader.removePortalLocation(locationName);
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
		
		if(!player.setLocationSafely(world.getSpawnLocation())){
			CMDYes.players.put(player, world.getSpawnLocation());
			player.sendMessage(Texts.builder().color(TextColors.DARK_RED).append(Texts.of("Unsafe spawn point detected. Teleport anyway? ")).onClick(TextActions.runCommand("/yes")).append(Texts.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
			return;
		}
		
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
			event.getTargetBlock().getLocation().get().getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.REDSTONE).build(), event.getTargetBlock().getLocation().get().getPosition().add(0, 1, 0));
		}else{
			Portal portal = new Portal(player, event.getTargetBlock().getState().getType(), builder.getWorld(), builder.getLocation(), event.getTargetBlock().getLocation().get());

			portal.build();
		}
	}
}
