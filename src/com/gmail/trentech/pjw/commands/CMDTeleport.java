package com.gmail.trentech.pjw.commands;

import java.util.HashMap;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.events.TeleportEvent;

public class CMDTeleport implements CommandExecutor {

	public static HashMap<Player, Location<World>> players = new HashMap<>();
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("arg0")) {
			Text t1 = Texts.of(TextColors.GOLD, "/world teleport ");
			Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter the player you want to teleport"))).append(Texts.of("[player] ")).build();
			Text t3 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter world or @w for current world, and optional coordinates"))).append(Texts.of("<world:[x,y,z]>")).build();
			
			src.sendMessage(Texts.of(t1,t2,t3));

			return CommandResult.empty();
		}
		String arg0 = args.<String>getOne("arg0").get();
		
		if(arg0.equalsIgnoreCase("confirm")){
			if(!(src instanceof Player)){
				return CommandResult.success();
			}
			Player player = (Player) src;
			
			if(!players.containsKey(player)){
				return CommandResult.success();
			}
			Location<World> location = players.get(player);
			
			player.setLocation(location);
			player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, location.getExtent().getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", location.getExtent().getSpawnLocation().getBlockX(), ", y: ", location.getExtent().getSpawnLocation().getBlockY(),", z: ", location.getExtent().getSpawnLocation().getBlockZ())));
			
			players.remove(player);
			
			return CommandResult.success();
		}
		
		String worldName = args.<String>getOne("arg0").get();
		
		if(!args.hasAny("arg1")) {
			if(!(src instanceof Player)){
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Must be a player"));
				return CommandResult.empty();
			}
			arg0 = ((Player) src).getName();
		}else{
			worldName = args.<String>getOne("arg1").get();
		}
		
		if(src instanceof Player){
			worldName = worldName.replace("@w", ((Player) src).getWorld().getName());
		}
		
		if(!Main.getGame().getServer().getPlayer(arg0).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Player ", arg0, " does not exist"));
			return CommandResult.empty();
		}
		Player player = Main.getGame().getServer().getPlayer(arg0).get();
		
		if((((Player) src) != player) && !src.hasPermission("pjw.cmd.world.teleport.others")){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to teleport others"));
			return CommandResult.empty();
		}
		
		String coords = null;
		if(worldName.contains(":")){
			String[] work = worldName.split(":");
			worldName = work[0];
			coords = work[1];
		}
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();

		Location<World> dest = world.getSpawnLocation();

		if(isValidLocation(coords)){
			String[] location = coords.split(",");
			dest = world.getLocation(Integer.parseInt(location[0]), Integer.parseInt(location[1]), Integer.parseInt(location[2]));
		}

		boolean result = Main.getGame().getEventManager().post(new TeleportEvent(player.getLocation(), dest, Cause.of(player)));
		
		if(!result){
			if(((Player) src) != player){
				src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Teleported ", player.getName(), " to ", world.getName()));
			}
			return CommandResult.success();
		}
		
		if(((Player) src) != player){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Failed to teleport ", player.getName()));
		}
		
		return CommandResult.empty();
	}
	
	private boolean isValidLocation(String coords){
		if(coords == null){
			return false;
		}
		
		for(String coord : coords.split(",")){
			try{
				Integer.parseInt(coord);
			}catch(Exception e){
				return false;
			}
		}
		return true;
	}

}
