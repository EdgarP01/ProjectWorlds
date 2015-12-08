package com.gmail.trentech.pjw.commands;

import java.util.Map.Entry;
import java.util.Set;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
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
import com.gmail.trentech.pjw.utils.Portal;

public class CMDPortal implements CommandExecutor {

	private static boolean ran = false;
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Must be a player"));
			return CommandResult.empty();
		}
		Player player = (Player) src;

		Set<Portal> portals = Main.getPortalsList();
		
		for (Portal portal : portals){
			if (portal.getPlayerUUID().equals(player.getUniqueId())){
				for(Entry<BlockSnapshot, Boolean> blockData : portal.getBlockData().entrySet()){
					Location<World> location = blockData.getKey().getLocation().get();
					BlockType type = blockData.getKey().getState().getType();

					if(!blockData.getValue()){
						if(type == BlockTypes.WOOL){
							location.setBlockType(BlockTypes.OBSIDIAN);
						}else{
							location.setBlockType(BlockTypes.AIR);
						}
					}else{
						location.setBlockType(BlockTypes.AIR);
					}
				}
				
				player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Portal builder stopped"));
				
				Main.getPortalsList().remove(portal);	
				Main.getPlayersList().add(player);
				
				return CommandResult.success();
			}
		}

		if(!args.hasAny("name")) {
			Portal portal = new Portal(player.getUniqueId(), null);
			Main.getPortalsList().add(portal);
			player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Portal builder started (remove only)"));
			return CommandResult.success();
		}
		String worldName = args.<String>getOne("name").get();

		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		
		Portal portal = new Portal(player.getUniqueId(), worldName);
		Main.getPortalsList().add(portal);
		player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Portal builder started"));

		return CommandResult.success();
	}

	public static boolean isRan() {
		return ran;
	}

}
