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
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;

public class CMDKeepSpawnLoaded implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(invalidArg());
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();
		
		if(worldName.equalsIgnoreCase("@w")){
			if(src instanceof Player){
				worldName = ((Player) src).getWorld().getName();
			}
		}
		
		if(!Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "World ", worldName, " does not exist"));
			return CommandResult.empty();
		}
		World world = Main.getGame().getServer().getWorld(worldName).get();

		if(!args.hasAny("value")) {
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			if(world.doesKeepSpawnLoaded()){
				src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Keep Spawn Loaded: ", TextColors.GOLD, "true"));
			}else{
				src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Keep Spawn Loaded: ", TextColors.GOLD, "false"));
			}
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "Command: ",invalidArg()));
			src.sendMessage(Texts.of(TextColors.DARK_PURPLE, "-----------------------------------------"));
			return CommandResult.success();
		}
		String value = args.<String>getOne("value").get();
		
		Boolean bool;
		try{
			bool = Boolean.parseBoolean(value);
		}catch(Exception e){
			src.sendMessage(invalidArg());
			return CommandResult.empty();	
		}

		world.getProperties().setKeepSpawnLoaded(bool);

		src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Set keep spawn loaded of world ", worldName, " to ", value));

		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.GOLD, "/world keepspawnloaded ");
		Text t2 = Texts.builder().color(TextColors.GOLD).onHover(TextActions.showText(Texts.of("Enter world or @w for current world"))).append(Texts.of("<world> ")).build();
		Text t3 = Texts.of(TextColors.GOLD, "[true/false]");
		return Texts.of(t1,t2,t3);
	}
}
