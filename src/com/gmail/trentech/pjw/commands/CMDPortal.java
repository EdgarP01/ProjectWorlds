package com.gmail.trentech.pjw.commands;

import java.util.List;
import java.util.UUID;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.portal.Portal;
import com.gmail.trentech.pjw.portal.PortalBuilder;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Utils;

import ninja.leaping.configurate.ConfigurationNode;

public class CMDPortal implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;
		
		if(!args.hasAny("name")) {
			src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Right click the portal to remove"));
			PortalBuilder.getActiveBuilders().put((Player) src, new PortalBuilder());
			return CommandResult.success();
		}
		String worldName = args.<String>getOne("name").get();

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		
		if(args.hasAny("point1")) {
			String[] point1 = args.<String>getOne("point1").get().split(",");

			if(!args.hasAny("point2")) {
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
				src.sendMessage(Texts.of(TextColors.GOLD, "/world portal <world> <x,y,z> <x,y,z> <block>"));
				return CommandResult.empty();
			}
			String[] point2 = args.<String>getOne("point2").get().split(",");
			
			if(!(Utils.isValidLocation(point1) && Utils.isValidLocation(point1))){
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
				src.sendMessage(Texts.of(TextColors.GOLD, "/world portal <world> <x,y,z> <x,y,z> <block>"));
				return CommandResult.empty();
			}

			if(!args.hasAny("block")) {
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Invalid Argument\n"));
				src.sendMessage(Texts.of(TextColors.GOLD, "/world portal <world> <x,y,z> <x,y,z> <block>"));
				return CommandResult.empty();
			}
			BlockType block = Utils.getBlockType(args.<String>getOne("block").get());
			
			String worldPlace;
			if(!args.hasAny("world")) {
				worldPlace = player.getWorld().getName();
			}else{
				worldPlace = args.<String>getOne("world").get();
			}
			
			Portal portal = new Portal(worldPlace, Integer.parseInt(point1[0]), Integer.parseInt(point1[1]), Integer.parseInt(point1[2]), Integer.parseInt(point2[0]), Integer.parseInt(point2[1]), Integer.parseInt(point2[2]));
			
			PortalBuilder.getActiveBuilders().remove(player);
			
            if(portal.getLocations() == null){
                player.sendMessage(Texts.of(TextColors.DARK_RED, "Portals cannot over lap over portals"));
                return CommandResult.empty();
            }
            List<String> locations = portal.getLocations();

            ConfigurationNode config = new ConfigManager().getConfig();
            
            int size = config.getNode("Options", "Portal", "Size").getInt();
            if(locations.size() > size){
            	src.sendMessage(Texts.of(TextColors.DARK_RED, "Portals cannot be larger than ", size, " blocks"));
            	return CommandResult.empty();
            }
            
            PortalBuilder.getCreators().add(player);
            
            for(String loc : locations){
            	String[] info = loc.split("\\.");

            	Location<World> location = Main.getGame().getServer().getWorld(info[0]).get().getLocation(Integer.parseInt(info[1]), Integer.parseInt(info[2]), Integer.parseInt(info[3]));
            	
            	if(config.getNode("Options", "Portal", "Replace-Frame").getBoolean()){	
                	if(location.getBlockType() != BlockTypes.AIR){
            			location.setBlock(Main.getGame().getRegistry().createBuilder(BlockState.Builder.class).blockType(block).build());
                	}
            	}
            	location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.EXPLOSION_LARGE).build(), location.getPosition());
            }
            
            String uuid = UUID.randomUUID().toString();
            
            ConfigManager loaderPortals = new ConfigManager("portals.conf");
    		ConfigurationNode configPortals = loaderPortals.getConfig();
    		
            configPortals.getNode("Portals", uuid, "Locations").setValue(locations);
            configPortals.getNode("Portals", uuid, "World").setValue(worldName);

            loaderPortals.save();
          
            player.sendMessage(Texts.of(TextColors.DARK_GREEN, "New portal created"));
            
			return CommandResult.success();
		}

		PortalBuilder builder = new PortalBuilder(worldName);

		PortalBuilder.getActiveBuilders().put(player, builder);

		player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Right click starting point"));

		return CommandResult.success();
	}
}
