package com.gmail.trentech.pjw.commands;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
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

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.events.PortalConstructEvent;
import com.gmail.trentech.pjw.portal.Portal;
import com.gmail.trentech.pjw.portal.PortalBuilder;

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
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
			String[] point2 = args.<String>getOne("point2").get().split(",");
			
			if(!(isValidLocation(point1) && isValidLocation(point1))){
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}

			if(!args.hasAny("block")) {
				src.sendMessage(invalidArg());
				return CommandResult.empty();
			}
			
			BlockType blockType = BlockTypes.STONE;
			if(Main.getGame().getRegistry().getType(BlockType.class, args.<String>getOne("block").get()).isPresent()){
				blockType = Main.getGame().getRegistry().getType(BlockType.class, args.<String>getOne("block").get()).get();
			}
			BlockState block = BlockState.builder().blockType(blockType).build();
			
			String worldPlace;
			if(!args.hasAny("world")) {
				worldPlace = player.getWorld().getName();
			}else{
				worldPlace = args.<String>getOne("world").get();
			}
			
			Portal portal = new Portal(block, worldPlace, worldName, Integer.parseInt(point1[0]), Integer.parseInt(point1[1]), Integer.parseInt(point1[2]), Integer.parseInt(point2[0]), Integer.parseInt(point2[1]), Integer.parseInt(point2[2]));
			
			boolean portalConstructEvent = Main.getGame().getEventManager().post(new PortalConstructEvent(portal.getLocations(), Cause.of(player)));
			if(!portalConstructEvent) {
				portal.build();
			}

			return CommandResult.success();
		}

		PortalBuilder builder = new PortalBuilder(worldName);

		PortalBuilder.getActiveBuilders().put(player, builder);

		player.sendMessage(Texts.of(TextColors.DARK_GREEN, "Right click starting point"));

		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.GOLD, "/world portal ");
		Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter destination world"))).append(Texts.of("<world> ")).build();
		Text t3 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter first point"))).append(Texts.of("<x,y,z> ")).build();
		Text t4 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter second point"))).append(Texts.of("<x,y,z> ")).build();
		Text t5 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter portal frame block type"))).append(Texts.of("<block> ")).build();
		Text t6 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter world to place the portal"))).append(Texts.of("[world]")).build();
		return Texts.of(t1,t2,t3,t4,t5,t6);
	}
	
	private boolean isValidLocation(String[] coords){
		for(String coord : coords){
			try{
				Integer.parseInt(coord);
			}catch(Exception e){
				return false;
			}
		}
		return true;
	}
}
