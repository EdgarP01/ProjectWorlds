package com.gmail.trentech.pjw.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PortalBuilder {

	private String world;
	private Location<World> location;
	
	private static HashMap<Player, PortalBuilder> activeBuilders = new HashMap<>();
	private static List<Player> creators = new ArrayList<>();

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

	public static HashMap<Player, PortalBuilder> getActiveBuilders() {
		return activeBuilders;
	}

	public static List<Player> getCreators() {
		return creators;
	}
}

