package com.gmail.trentech.pjw.commands.old.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.gmail.trentech.pjw.utils.Gamemode;

public class GameModeElement extends CommandElement {

    public GameModeElement(Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    	final String next = args.next().toUpperCase();
    	
    	Optional<GameMode> optional = Gamemode.get(next);
    	
    	if(optional.isPresent()) {
    		return optional.get();
    	}
    	
    	try {
    		optional = Gamemode.get(Integer.parseInt(next));
    		
        	if(optional.isPresent()) {
        		return optional.get();
        	}
    	} catch (Exception e) { }

		throw args.createError(Text.of(TextColors.RED, "Not a valid gamemode"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
    	List<String> list = new ArrayList<>();
    	
    	Optional<String> next = args.nextIfPresent();
    	
    	if(next.isPresent()) {
            for(Gamemode gamemode : Gamemode.values()) {
            	if(gamemode.name().toUpperCase().startsWith(next.get().toUpperCase())) {
            		list.add(gamemode.name().toUpperCase());
            	}
            	if(Integer.toString(gamemode.getIndex()).startsWith(next.get().toUpperCase())) {
            		list.add(Integer.toString(gamemode.getIndex()));
            	}
            }
    	}

        return list;
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of(getKey());
    }
}
