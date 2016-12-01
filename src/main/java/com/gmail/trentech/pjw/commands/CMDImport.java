package com.gmail.trentech.pjw.commands;

import java.io.IOException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.io.WorldData;

public class CMDImport implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String worldName = args.<String> getOne("world").get();

		if (Sponge.getServer().getWorld(worldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " is already loaded"), false);
		}

		WorldData worldData = new WorldData(worldName);

		if (!worldData.exists()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " is not a valid world"), false);
		}

		SpongeData spongeData = new SpongeData(worldName);

		if (spongeData.exists()) {
			src.sendMessage(Text.of(TextColors.RED, "Sponge world detected"));
			src.sendMessage(Text.builder().color(TextColors.YELLOW).onHover(TextActions.showText(Text.of("Click command for more information "))).onClick(TextActions.runCommand("/pjw:world load")).append(Text.of(" /world load")).build());
			return CommandResult.success();
		}

		DimensionType dimensionType = args.<DimensionType> getOne("dimensionType").get();
		GeneratorType generatorType = args.<GeneratorType> getOne("generatorType").get();

		WorldArchetype.Builder builder = WorldArchetype.builder().dimension(dimensionType)
				.generator(generatorType).enabled(true).keepsSpawnLoaded(true).loadsOnStartup(true);

		if(args.hasAny("modifier")) {
			builder.generatorModifiers(args.<WorldGeneratorModifier> getOne("modifier").get());
		}
		
		WorldArchetype settings = builder.build(worldName, worldName);
		
		WorldProperties properties;
		try {
			properties = Sponge.getServer().createWorldProperties(worldName, settings);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
		}

		Sponge.getServer().saveWorldProperties(properties);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " imported successfully"));

		return CommandResult.success();
	}

}
