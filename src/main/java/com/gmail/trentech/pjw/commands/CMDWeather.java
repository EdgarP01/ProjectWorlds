package com.gmail.trentech.pjw.commands;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.weather.Weathers;

public class CMDWeather implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		Optional<World> optionalWorld = Sponge.getServer().getWorld(properties.getUniqueId());
		
		if(!optionalWorld.isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, properties.getWorldName(), " must be loaded"), false);
		}
		World world = optionalWorld.get();
		
		if (!args.hasAny("clear|rain|thunder")) {
			if(properties.isRaining()) {
				src.sendMessage(Text.of(TextColors.GREEN, "Weather: ", TextColors.WHITE, "rain"));
			} else if(properties.isThundering()) {
				src.sendMessage(Text.of(TextColors.GREEN, "Weather: ", TextColors.WHITE, "thunder"));
			} else {
				src.sendMessage(Text.of(TextColors.GREEN, "Weather: ", TextColors.WHITE, "clear"));
			}
			
			return CommandResult.success();
		}
		String value = args.<String> getOne("clear|rain|thunder").get();
		
		int duration = 0;
		if (args.hasAny("duration")) {
			duration = args.<Integer> getOne("duration").get();
		}
		
		if(duration == 0) {
			if(value.equalsIgnoreCase("clear")) {
				world.setWeather(Weathers.CLEAR);
			} else if(value.equalsIgnoreCase("rain")) {
				world.setWeather(Weathers.RAIN);
			} else if(value.equalsIgnoreCase("thunder")) {
				world.setWeather(Weathers.THUNDER_STORM);
			} else {
				throw new CommandException(Text.of(TextColors.RED, "Not a valid value"), true);
			}
		} else {
			if(value.equalsIgnoreCase("clear")) {
				world.setWeather(Weathers.CLEAR, duration);
			} else if(value.equalsIgnoreCase("rain")) {
				world.setWeather(Weathers.RAIN, duration);
			} else if(value.equalsIgnoreCase("thunder")) {
				world.setWeather(Weathers.THUNDER_STORM, duration);
			} else {
				throw new CommandException(Text.of(TextColors.RED, "Not a valid value"), true);
			}
		}

		src.sendMessage(Text.of(TextColors.DARK_GREEN, "Set wheather of ", properties.getWorldName(), " to ", TextColors.YELLOW, value, TextColors.DARK_GREEN, " for ", TextColors.YELLOW, world.getRemainingDuration()));

		return CommandResult.success();
	}
}
