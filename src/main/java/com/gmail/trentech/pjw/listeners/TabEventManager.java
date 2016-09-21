package com.gmail.trentech.pjw.listeners;

import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.TabCompleteEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.storage.WorldProperties;

public class TabEventManager {

	@Listener
	public void onTabCompleteEvent(TabCompleteEvent event, @First CommandSource src) {
		String rawMessage = event.getRawMessage();

		String[] args = rawMessage.split(" ");
		
		List<String> list = event.getTabCompletions();
		
		if(!((args[0].equalsIgnoreCase("w") || args[0].equalsIgnoreCase("world"))) || args.length == 1) {
			return;
		}
		
		if((args[1].equalsIgnoreCase("gamerule") || args[1].equalsIgnoreCase("gr"))) {
			if(args.length == 3 || args.length == 4) {
				Optional<WorldProperties> optionalProperties = Sponge.getServer().getWorldProperties(args[2]);
				
				if(optionalProperties.isPresent()) {
					for(Entry<String, String> gamerule : optionalProperties.get().getGameRules().entrySet()) {
						String name = gamerule.getKey();
						
						if(args.length == 4) {
							if(name.contains(args[3].toLowerCase()) && !name.equalsIgnoreCase(args[3])) {
								list.add(name);
							}
						} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
							list.add(name);
						}
					}
				}
			}
			return;
		}// else if(args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("e") 
//				|| args[1].equalsIgnoreCase("hardcore") || args[1].equalsIgnoreCase("h")
//				|| args[1].equalsIgnoreCase("keepspawnloaded") || args[1].equalsIgnoreCase("k")
//				|| args[1].equalsIgnoreCase("pvp") || args[1].equalsIgnoreCase("p")
//				|| args[1].equalsIgnoreCase("regen") || args[1].equalsIgnoreCase("r")) {			
//			if(args.length == 3 || args.length == 4) {
//				String t = "true";
//				String f = "false";
//				
//				if(args.length == 4) {
//					if((t.contains(args[3].toLowerCase()) && !t.equalsIgnoreCase(args[3]))) {
//						list.add(t);
//					} else if((f.contains(args[3].toLowerCase()) && !f.equalsIgnoreCase(args[3]))) {
//						list.add(f);
//					}
//				} else if(rawMessage.substring(rawMessage.length() - 1).equalsIgnoreCase(" ")){
//					list.add(t);
//					list.add(f);
//				}
//			}
//		}
	}
}
