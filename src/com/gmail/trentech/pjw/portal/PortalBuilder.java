package com.gmail.trentech.pjw.portal;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PortalBuilder {

	private String world;
	private Location<World> location;

	public PortalBuilder(String world) {
		this.world = world;
	}
	
	public PortalBuilder() {
		
	}

	public String getWorld() {
		return world;
	}

	public Location<World> getLocation() {
		return location;
	}

	public void setLocation(Location<World> location) {
		this.location = location;
	}
}

