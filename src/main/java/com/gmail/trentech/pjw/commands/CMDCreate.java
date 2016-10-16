package com.gmail.trentech.pjw.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.utils.Help;

public class CMDCreate implements CommandExecutor {

	public static List<String> worlds = new ArrayList<>();

	public CMDCreate() {
		new Help("world create", "create", "Allows you to create new worlds with any combination of optional arguments -d " + "for dimension type, -g for generator type, -s for seed and -m for generator modifiers", false)
			.setPermission("pjw.cmd.world.create")
			.setUsage("/world create <world> [-d <dimensionType>] [-g <generatorType>] [-m <modifer>]  [-s <seed>]\n /w cr <world> [-d <dimensionType>] [-g <generatorType>] [-m <modifer>]  [-s <seed>]")
			.setExample("/world create NewWorld -s -12309830198412353456\n /world create NewWorld -d overworld -g overworld\n" + " /world create NewWorld -d nether -m sponge:skylands")
			.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		String worldName = args.<String> getOne("name").get();

		if (Sponge.getServer().getWorldProperties(worldName).isPresent()) {
			throw new CommandException(Text.of(TextColors.RED, worldName, " already exists"), false);
		}

		WorldArchetype.Builder builder = WorldArchetype.builder();

		if (args.hasAny("dimensionType")) {
			builder.dimension(args.<DimensionType> getOne("dimensionType").get());
		}

		if (args.hasAny("generatorType")) {
			builder.generator(args.<GeneratorType> getOne("generatorType").get());
		}

		if (args.hasAny("modifier")) {
			builder.generatorModifiers(args.<WorldGeneratorModifier> getOne("modifier").get());
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

		WorldArchetype settings = builder.enabled(true).keepsSpawnLoaded(true).loadsOnStartup(true).build(worldName, worldName);

		WorldProperties properties;
		try {
			properties = Sponge.getServer().createWorldProperties(worldName, settings);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(Text.of(TextColors.RED, "Something went wrong. Check server log for details"), false);
		}

		Sponge.getServer().saveWorldProperties(properties);

//		SpongeData.getIds().add((int) properties.getPropertySection(DataQuery.of("SpongeData")).get().get(DataQuery.of("dimensionId")).get());
//
//		ConfigManager configManager = ConfigManager.get();
//		configManager.getConfig().getNode("dimension_ids").setValue(SpongeData.getIds());
//		configManager.save();

		worlds.add(worldName);

		src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " created successfully"));

		return CommandResult.success();
	}
}
