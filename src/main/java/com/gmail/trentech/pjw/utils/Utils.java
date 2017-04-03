package com.gmail.trentech.pjw.utils;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;

public class Utils {

	public static Text getTime(long time) {		
		int ticks = (int) (time % 24000);
        int hours = (ticks / 1000) + 6;
        
        if(hours > 24) {
        	hours = hours - 24;
        }
        
        Text text;

        if(hours > 12) {
        	hours = hours - 12;
        	if(hours == 12) {
        		text = Text.of("AM");
        	} else {
        		text = Text.of("PM");
        	}
        } else if(hours == 12){ 	
        	text = Text.of("PM");
        } else {
        	text = Text.of("AM");
        }
        
        if(hours < 1) {
        	hours = 12;
        }
        
        double minutes = (ticks % 1000) / 16.6666666667;
        
        if(minutes < 10) {
        	return Text.join(Text.of(hours, ":0" + (int)minutes), text);
        } else {
        	return Text.join(Text.of(hours, ":" + (int)minutes), text);
        }
	}
	
	public static void createPlatform(Location<World> center) {
		platform(center, BlockTypes.STONE);

		platform(center.getRelative(Direction.UP), BlockTypes.AIR);
		platform(center.getRelative(Direction.UP).getRelative(Direction.UP), BlockTypes.AIR);
		platform(center.getRelative(Direction.UP).getRelative(Direction.UP).getRelative(Direction.UP), BlockTypes.AIR);
	}

	private static void platform(Location<World> center, BlockType type) {
		List<Location<World>> list = new ArrayList<>();

		Location<World> south = center.getRelative(Direction.SOUTH);
		Location<World> north = center.getRelative(Direction.NORTH);
		Location<World> east = center.getRelative(Direction.EAST);
		Location<World> west = center.getRelative(Direction.WEST);

		Location<World> north2 = north.getRelative(Direction.NORTH);
		Location<World> south2 = south.getRelative(Direction.SOUTH);
		Location<World> east2 = east.getRelative(Direction.EAST);
		Location<World> west2 = west.getRelative(Direction.WEST);

		list.add(center);

		list.add(north);
		list.add(south);
		list.add(east);
		list.add(west);

		list.add(north.getRelative(Direction.EAST));
		list.add(north.getRelative(Direction.WEST));
		list.add(south.getRelative(Direction.EAST));
		list.add(south.getRelative(Direction.WEST));

		list.add(north2);
		list.add(north2.getRelative(Direction.EAST));
		list.add(north2.getRelative(Direction.EAST).getRelative(Direction.EAST));
		list.add(north2.getRelative(Direction.WEST));
		list.add(north2.getRelative(Direction.WEST).getRelative(Direction.WEST));

		list.add(south2);
		list.add(south2.getRelative(Direction.EAST));
		list.add(south2.getRelative(Direction.EAST).getRelative(Direction.EAST));
		list.add(south2.getRelative(Direction.WEST));
		list.add(south2.getRelative(Direction.WEST).getRelative(Direction.WEST));

		list.add(east2);
		list.add(east2.getRelative(Direction.NORTH));
		list.add(east2.getRelative(Direction.SOUTH));

		list.add(west2);
		list.add(west2.getRelative(Direction.NORTH));
		list.add(west2.getRelative(Direction.SOUTH));

		for (Location<World> location : list) {
			location.setBlock(Sponge.getRegistry().createBuilder(BlockState.Builder.class).blockType(type).build(), Cause.of(NamedCause.source(Main.getPlugin())));
		}
	}
}
