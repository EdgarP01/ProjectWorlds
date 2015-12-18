package com.gmail.trentech.pjw.events;

import java.util.List;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class PortalConstructEvent extends AbstractEvent implements Cancellable {

	private boolean cancelled = false;
	//private Player player;
	private Cause cause;
	private List<String> locations;
	
	public PortalConstructEvent(List<String> locations, Cause cause){
		this.cause = cause;
		this.locations = locations;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;		
	}
	
	@Override
	public Cause getCause() {
		return cause;
	}

	public List<String> getLocations() {
		return locations;
	}

//	public Player getPlayer() {
//		return player;
//	}
}
