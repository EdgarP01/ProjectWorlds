package com.gmail.trentech.pjw.events;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class TeleportEvent extends AbstractEvent implements Cancellable {
	
	private boolean cancelled = false;
	private Player player;
	private Location<World> src;
	private Location<World> dest;
	
	public TeleportEvent(Player player, Location<World> src, Location<World> dest){
		this.player = player;
		this.src = src;
		this.setDest(dest);
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
		return null;
	}

	public Player getPlayer() {
		return player;
	}

	public Location<World> getDest() {
		return dest;
	}

	public void setDest(Location<World> dest) {
		this.dest = dest;
	}

	public Location<World> getSrc() {
		return src;
	}
}
