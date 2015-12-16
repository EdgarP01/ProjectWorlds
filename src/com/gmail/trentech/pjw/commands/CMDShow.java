package com.gmail.trentech.pjw.commands;

import java.util.ArrayList;
import java.util.List;

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
import com.gmail.trentech.pjw.utils.ConfigManager;

public class CMDShow implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}

		String worldName = null;
		if(args.hasAny("name")) {
			worldName = args.<String>getOne("name").get();
		}

		ConfigManager loader = new ConfigManager("portals.conf");

		List<String> locationNames = loader.getAllLocations();
		List<Location<World>> locations = new ArrayList<>();
		
		for(String node : locationNames){
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
			
			locations.add(location);
		}
		
		for(Location<World> location : locations){
			location.getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class)
					.type(ParticleTypes.BARRIER).count(1).build(), location.getPosition().add(.5,.5,.5));
		}

		return CommandResult.success();
	}
}
