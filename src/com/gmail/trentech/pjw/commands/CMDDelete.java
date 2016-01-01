package com.gmail.trentech.pjw.commands;

import java.util.concurrent.ExecutionException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import com.gmail.trentech.pjw.Main;

public class CMDDelete implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!args.hasAny("name")) {
			src.sendMessage(Text.of(TextColors.YELLOW, "/world delete <world>"));
			return CommandResult.empty();
		}
		String worldName = args.<String>getOne("name").get();

		if(Main.getGame().getServer().getWorld(worldName).isPresent()){
			src.sendMessage(Text.of(TextColors.DARK_RED, worldName, " must be unloaded before you can rename"));
			return CommandResult.empty();
		}

		for(WorldProperties worldInfo : Main.getGame().getServer().getUnloadedWorlds()){
			if(!worldInfo.getWorldName().equalsIgnoreCase(worldName)){
				continue;
			}

			try {
//				File worldFile = new File(Main.getGame().getSavesDirectory() + "/" + Main.getGame().getServer().getDefaultWorld().get().getWorldName() + "/" + worldName);
//				
//				FileOutputStream fileOutputStream = new FileOutputStream(new File(Main.getGame().getSavesDirectory() + "/" + worldName + ".zip"));
//	    		ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
//	    		
//	    		ZipEntry worldEntry = new ZipEntry(worldFile.getName());
//	    		zipOutputStream.putNextEntry(worldEntry);
//	    		
//	    		FileInputStream fileInputStream = new FileInputStream(worldFile.getAbsoluteFile());
//	   	   
//	    		byte[] buffer = new byte[1024];
//
//	    		int bytesRead;
//	    		while ((bytesRead = fileInputStream.read(buffer)) > 0) {
//	    			zipOutputStream.write(buffer, 0, bytesRead);
//	    		}
//
//	    		zipOutputStream.closeEntry();
//	    		fileInputStream.close();
//	    		fileOutputStream.close();    		
//	    		zipOutputStream.close();

				if(Main.getGame().getServer().deleteWorld(worldInfo).get()){
					src.sendMessage(Text.of(TextColors.DARK_GREEN, worldName, " deleted successfully"));
					return CommandResult.success();
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		
		src.sendMessage(Text.of(TextColors.DARK_RED, "Could not locate ", worldName));
		
		return CommandResult.empty();
	}

}
