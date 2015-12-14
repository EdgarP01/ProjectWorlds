package com.gmail.trentech.pjw.events;

import java.util.List;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.impl.AbstractEvent;

public class PortalConstructEvent extends AbstractEvent implements Cancellable {

	private boolean cancelled = false;
	private Player player;
	private List<String> locations;
	private String worldName;
	
	public PortalConstructEvent(Player player, List<String> locations, String worldName){
		this.player = player;
		this.locations = locations;
		this.worldName = worldName;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;		
	}

	public List<String> getLocations() {
		return locations;
	}

	public Player getPlayer() {
		return player;
	}

	public String getWorldName() {
		return worldName;
	}
}
