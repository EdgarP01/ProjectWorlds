package com.gmail.trentech.pjw.utils;

import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.gmail.trentech.pjw.Main;

public class Resource {

	public final static String NAME = "Project Worlds";
	public final static String VERSION = "0.5.14";
	public final static String ID = "Project Worlds";

	
	public static void particles(Location<World> location){
		Location<World> location2 = location.getRelative(Direction.UP);
		
		location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(0,.2,0));
        location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(0,.8,0));
        location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(0,.6,0));
        location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(0,.3,0));
        location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location.getPosition().add(0,.9,0));
        location2.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location2.getPosition().add(0,.2,0));
        location2.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location2.getPosition().add(0,.8,0));
        location2.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location2.getPosition().add(0,.6,0));
        location2.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location2.getPosition().add(0,.3,0));
        location2.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.SPELL_WITCH).motion(Vector3d.UP).count(3).build(), location2.getPosition().add(0,.9,0));
	}
}
