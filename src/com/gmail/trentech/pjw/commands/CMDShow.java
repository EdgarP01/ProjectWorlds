package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.ConfigManager;

//BROKEN
public class CMDShow implements CommandExecutor {

	private static List<Player> show = new ArrayList<>();
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		if(show.contains(player)){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Slow down"));
			return CommandResult.empty();
		}
		
		String worldName = null;
		if(args.hasAny("name")) {
			worldName = args.<String>getOne("name").get();
			if(worldName.equalsIgnoreCase("@w")){
				if(src instanceof Player){
					worldName = player.getWorld().getName();
				}
			}
		}

		ConfigManager loader = new ConfigManager("portals.conf");
		
		HashMap<Location<World>, BlockSnapshot> hash = new HashMap<>();

		List<String> locations = loader.getAllLocations();

		show.add(player);
		
		for(String node : locations){
			String[] split = node.split("\\.");
			
			if(worldName != null){
				if(!worldName.equalsIgnoreCase(split[0])){
					continue;
				}
			}

			if(!Main.getGame().getServer().getWorld(split[0]).isPresent()){
				continue;
			}
			World world = Main.getGame().getServer().getWorld(split[0]).get();

			int x = Integer.parseInt(split[1]);
			int y = Integer.parseInt(split[2]);
			int z = Integer.parseInt(split[3]);

			Location<World> location = world.getLocation(x, y, z);
			 

			hash.put(location, location.createSnapshot());

			location.setBlock(BlockState.builder().blockType(BlockTypes.BEDROCK).build());
		}
		
		Timer timer = new Timer();

		timer.schedule( 
	        new TimerTask() {
	            @Override
	            public void run() {
	            	for(Entry<Location<World>, BlockSnapshot> item : hash.entrySet()){
	            		item.getKey().setBlock(item.getValue().getExtendedState());
	            	}
	            	show.remove(player);
//	        		new Timer().schedule( 
//        		        new java.util.TimerTask() {
//        		            @Override
//        		            public void run() {
//        		            	show.remove(player);
//        		            }
//        		        }, 
//        		        2000
//	        		);
	            }
	        },5000);
		
		return CommandResult.success();
	}
}
