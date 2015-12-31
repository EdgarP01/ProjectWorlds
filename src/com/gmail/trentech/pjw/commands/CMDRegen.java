package com.gmail.trentech.pjw.commands;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.WorldCreationSettings.Builder;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.utils.Utils;
import com.google.common.util.concurrent.ListenableFuture;

public class CMDRegen implements CommandExecutor {
	
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
		
		boolean preserve = false;
		if(args.hasAny("value")) {
			preserve = Boolean.parseBoolean(args.<String>getOne("value").get());
		}

		WorldProperties properties = world.getProperties();
		Builder builder = WorldCreationSettings.builder().name(world.getName()).dimension(properties.getDimensionType()).generatorSettings(properties.getGeneratorSettings());
		
		if(preserve){
			builder.seed(properties.getSeed());
		}
		
		src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Kicking players from world.."));
		for(Entity entity : world.getEntities()){
			if(entity instanceof Player){
				Player player = (Player) entity;
				WorldProperties defaultProperties = Main.getGame().getServer().getDefaultWorld().get();
				player.setLocationSafely(Main.getGame().getServer().getWorld(defaultProperties.getWorldName()).get().getSpawnLocation());
				player.sendMessage(Texts.of(TextColors.GOLD, world.getName(), " is being regenerated"));
			}
		}

		src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Unloading world.."));
		if(!Main.getGame().getServer().unloadWorld(world)){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not unload ", worldName));
			return CommandResult.empty();
		}

		try {
			ListenableFuture<Boolean> delete = Main.getGame().getServer().deleteWorld(properties);
			while(!delete.isDone()){}
			if(!delete.get()){
				src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not delete ", worldName));
				return CommandResult.empty();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		src.sendMessage(Texts.of(TextColors.DARK_GREEN, "Regenerating world.."));
		WorldCreationSettings settings = builder.enabled(true).loadsOnStartup(true).build();

		final Optional<WorldProperties> optProperties = Main.getGame().getServer().createWorldProperties(settings);

        if (!optProperties.isPresent()) {
        	src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not regenerate ", worldName));
        	return CommandResult.empty();
        }

		Optional<World> load = Main.getGame().getServer().loadWorld(optProperties.get());

		if(!load.isPresent()){
			src.sendMessage(Texts.of(TextColors.DARK_RED, "Could not load ", worldName));
			return CommandResult.empty();
		}

		Utils.createPlatform(load.get().getSpawnLocation().getRelative(Direction.DOWN));
		
		src.sendMessage(Texts.of(TextColors.DARK_GREEN, worldName, " regenerated successfully"));
		
		return CommandResult.success();
	}
	
	private Text invalidArg(){
		Text t1 = Texts.of(TextColors.YELLOW, "/world regen ");
		Text t2 = Texts.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Texts.of("Enter world name"))).append(Texts.of("<world> ")).build();
		Text t3 = Texts.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Texts.of("Preserve seed? true/false"))).append(Texts.of("[value]")).build();
		return Texts.of(t1,t2,t3);
	}
}
