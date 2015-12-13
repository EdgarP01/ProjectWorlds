package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;

public class CMDTeleport implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("arg0")) {
			Text t1 = Texts.of(TextColors.GOLD, "/world teleport ");
			Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter the player you want to teleport"))).append(Texts.of("[player] ")).build();
			Text t3 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter world or @w for current world, and optional coordinates"))).append(Texts.of("<world:[x,y,z]>")).build();
			
			src.sendMessage(Texts.of(t1,t2,t3));

			return CommandResult.empty();
		}
		String playerName = args.<String>getOne("arg0").get();
		
		String worldName = args.<String>getOne("arg0").get();
		
		if(!args.hasAny("arg1")) {
			if(!(src instanceof Player)){
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Must be a player"));
				return CommandResult.empty();
			}
			playerName = ((Player) src).getName();
		}else{
			worldName = args.<String>getOne("arg1").get();
		}
		
		if(src instanceof Player){
			worldName = worldName.replace("@w", ((Player) src).getWorld().getName());
		}
		
		if(!Main.getGame().getServer().getPlayer(playerName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Player ", playerName, " does not exist"));
			return CommandResult.empty();
		}
		Player player = Main.getGame().getServer().getPlayer(playerName).get();
		
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

		boolean result = true;
		if(isValidLocation(coords)){
			String[] location = coords.split(",");
			if(!player.setLocationSafely(world.getLocation(Integer.parseInt(location[0]), Integer.parseInt(location[1]), Integer.parseInt(location[2])))){
				result = false;
			}
		}else{
			if(!player.setLocationSafely(world.getSpawnLocation())){
				result = false;
			}
		}

		if(result){
			if(((Player) src) != player){
				src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Teleported ", player.getName(), " to ", world.getName()));
			}
			player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, world.getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", player.getLocation().getBlockX(), ", y: ", player.getLocation().getBlockY(),", z: ", player.getLocation().getBlockZ())));
			
			return CommandResult.success();
		}

		if(((Player) src) != player){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Failed to teleport ", player.getName()));
		}else{
			CMDYes.players.put(player, world.getSpawnLocation());
			player.sendMessage(Texts.builder().color(TextColors.DARK_RED).append(Texts.of("Unsafe spawn point detected. Teleport anyway? ")).onClick(TextActions.runCommand("/yes")).append(Texts.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
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
