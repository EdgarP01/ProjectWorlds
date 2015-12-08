package com.gmail.trentech.pjw.utils;

import java.util.HashMap;
import java.util.UUID;

import org.spongepowered.api.block.BlockSnapshot;

public class Portal {
	
	private UUID playerUUID;
	private String world;
	private HashMap<BlockSnapshot, Boolean> blockData = new HashMap<>();

	public Portal(UUID playerUUID, String world) {
		this.playerUUID = playerUUID;
		this.world = world;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public String getWorld() {
		return world;
	}

	public void setPlayerUUID(UUID playerUUID) {
		this.playerUUID = playerUUID;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public HashMap<BlockSnapshot, Boolean> getBlockData() {
		return blockData;
	}
}

