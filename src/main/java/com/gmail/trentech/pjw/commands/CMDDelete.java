package com.gmail.trentech.pjw.commands;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.io.SpongeData;
import com.gmail.trentech.pjw.utils.ConfigManager;
import com.gmail.trentech.pjw.utils.Help;
import com.gmail.trentech.pjw.utils.Zip;

public class CMDDelete implements CommandExecutor {

	public CMDDelete() {
		Help help = new Help("delete", "delete", " Delete worlds you no longer need. Worlds must be unloaded before you can delete them");
		help.setSyntax(" /world delete <world>\n /w dl <world>");
		help.setExample(" /world delete OldWorld");
		help.save();
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		WorldProperties properties = args.<WorldProperties> getOne("world").get();

		if (Sponge.getServer().getWorld(properties.getWorldName()).isPresent()) {
			src.sendMessage(Text.of(TextColors.RED, properties.getWorldName(), " must be unloaded before you can delete"));
			return CommandResult.empty();
		}

		new Zip(properties.getWorldName()).save();

		int dimId = (int) properties.getPropertySection(DataQuery.of("SpongeData")).get().get(DataQuery.of("dimensionId")).get();
		try {
			if (Sponge.getServer().deleteWorld(properties).get()) {
				List<Integer> ids = SpongeData.getIds();

				for (int id = 0; id < ids.size(); id++) {
					int current = ids.get(id);
					if (current == dimId) {
						ids.remove(id);
						break;
					}
				}

				ConfigManager configManager = ConfigManager.get();
				configManager.getConfig().getNode("dimension_ids").setValue(SpongeData.getIds());
				configManager.save();

				src.sendMessage(Text.of(TextColors.DARK_GREEN, properties.getWorldName(), " deleted successfully"));

				return CommandResult.success();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		src.sendMessage(Text.of(TextColors.DARK_RED, "Could not delete ", properties.getWorldName()));

		return CommandResult.empty();
	}

}
