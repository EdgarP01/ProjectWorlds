package com.gmail.trentech.pjw.commands.old;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjc.help.Help;

public class CMDCreate implements CommandExecutor {

	public static List<String> worlds = new ArrayList<>();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Help help = Help.get("world create").get();
		
		if (args.hasAny("help")) {		
			help.execute(src);
			return CommandResult.empty();
		}
		
		if (!args.hasAny("name")) {
			throw new CommandException(Text.builder().onClick(TextActions.executeCallback(help.execute())).append(help.getUsageText()).build(), false);
		}
		String worldName = args.<String> getOne("name").get();

		if (Sponge.getServer().getWorldProperties(worldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " already exists"), false);
		}

		WorldArchetype.Builder builder = WorldArchetype.builder();

		if (args.hasAny("dimension")) {
			builder.dimension(args.<DimensionType> getOne("dimension").get());
		}

		if (args.hasAny("generator")) {
			builder.generator(args.<GeneratorType> getOne("generator").get());
		}

		Collection<WorldGeneratorModifier> modifiers = Collections.<WorldGeneratorModifier>emptyList();
		
		if (args.hasAny("modifier")) {
			modifiers = args.<WorldGeneratorModifier> getAll("modifier");
		}

		if (args.hasAny("seed")) {
			String seed = args.<String> getOne("seed").get();

			try {
				Long s = Long.parseLong(seed);
				builder.seed(s);
			} catch (Exception e) {
				builder.seed(seed.hashCode());
			}
		}

		if (args.hasAny("gamemode")) {
			builder.gameMode(args.<GameMode> getOne("gamemode").get());
		}
		
		if (args.hasAny("difficulty")) {
			builder.difficulty(args.<Difficulty> getOne("difficulty").get());
		}

		if (args.hasAny("l")) {
			builder.loadsOnStartup(args.<Boolean> getOne("l").get());
		}
		
		if (args.hasAny("k")) {
			builder.keepsSpawnLoaded(args.<Boolean> getOne("k").get());
		}
		
		if (args.hasAny("c")) {
			builder.commandsAllowed(args.<Boolean> getOne("c").get());
		}
		
		if (args.hasAny("b")) {
			builder.generateBonusChest(args.<Boolean> getOne("b").get());
		}
		
		if (args.hasAny("f")) {
			builder.usesMapFeatures(args.<Boolean> getOne("f").get());
		}
		
		WorldArchetype settings = builder.enabled(true).keepsSpawnLoaded(true).loadsOnStartup(true).build(worldName, worldName);

		WorldProperties properties;
		try {
			properties = Sponge.getServer().createWorldProperties(worldName, settings);
			
			properties.setGeneratorModifiers(modifiers);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
		}

		Sponge.getServer().saveWorldProperties(properties);

		worlds.add(worldName);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " created successfully"));

		return CommandResult.success();
	}
}