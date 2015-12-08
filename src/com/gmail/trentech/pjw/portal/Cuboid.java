package com.gmail.trentech.pjw.portal;

import java.util.Iterator;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;

public class Cuboid implements Iterable<BlockSnapshot> {

	protected final String worldName;
	protected final int x1, y1, z1;
	protected final int x2, y2, z2;

	public Cuboid(Location<World> loc1, Location<World> l2) {
		if (!loc1.getExtent().equals(l2.getExtent())){
			throw new IllegalArgumentException("Locations must be on the same world");
		}
		this.worldName = loc1.getExtent().getName();
		this.x1 = Math.min(loc1.getBlockX(), l2.getBlockX());
		this.y1 = Math.min(loc1.getBlockY(), l2.getBlockY());
		this.z1 = Math.min(loc1.getBlockZ(), l2.getBlockZ());
		this.x2 = Math.max(loc1.getBlockX(), l2.getBlockX());
		this.y2 = Math.max(loc1.getBlockY(), l2.getBlockY());
		this.z2 = Math.max(loc1.getBlockZ(), l2.getBlockZ());
	}

	private World getWorld() {
		if (!Main.getGame().getServer().getWorld(worldName).isPresent()) {
			throw new IllegalStateException("World " + this.worldName + " is not loaded");
		}
		return Main.getGame().getServer().getWorld(worldName).get();
	}

	public Iterator<BlockSnapshot> iterator() {
		return new CuboidIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
	}

	@Override
	public String toString() {
		return new String("Cuboid: " + this.worldName + "," + this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2 + "," + this.y2 + "," + this.z2);
	}

	public class CuboidIterator implements Iterator<BlockSnapshot> {
		private World w;
		private int baseX, baseY, baseZ;
		private int x, y, z;
		private int sizeX, sizeY, sizeZ;

		public CuboidIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
			this.w = w;
			this.baseX = x1;
			this.baseY = y1;
			this.baseZ = z1;
			this.sizeX = Math.abs(x2 - x1) + 1;
			this.sizeY = Math.abs(y2 - y1) + 1;
			this.sizeZ = Math.abs(z2 - z1) + 1;
			this.x = this.y = this.z = 0;
		}

		public boolean hasNext() {
			return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
		}

		public BlockSnapshot next() {
			BlockSnapshot b = this.w.createSnapshot(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
			if (++x >= this.sizeX) {
				this.x = 0;
				if (++this.y >= this.sizeY) {
					this.y = 0;
					++this.z;
				}
			}
			return b;
		}
	}
}
