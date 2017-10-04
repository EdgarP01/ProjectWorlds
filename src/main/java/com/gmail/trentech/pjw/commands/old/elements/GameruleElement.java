package com.gmail.trentech.pjw.commands.old.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

public class GameruleElement extends CommandElement {

	CommandContext context = new CommandContext();
	
    public GameruleElement(Text key) {
        super(key);
    }

    @Override
    public void parse(CommandSource source, CommandArgs args, CommandContext context) throws ArgumentParseException {
    	this.context = context;
    	
        Object val = parseValue(source, args);
        String key = getUntranslatedKey();
        if (key != null && val != null) {
            if (val instanceof Iterable<?>) {
                for (Object ent : ((Iterable<?>) val)) {
                    context.putArg(key, ent);
                }
            } else {
                context.putArg(key, val);
            }
        }
    }
    
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
    	final String next = args.next();

    	if(context.hasAny("world")) {
    		WorldProperties properties = context.<WorldProperties>getOne("world").get();

    		for(Entry<String, String> entry : properties.getGameRules().entrySet()) {
    			if(entry.getKey().equals(next)) {
    				return next;
    			}
    		}
    	}

		throw args.createError(Text.of(TextColors.RED, "Command not found"));
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {  	
    	List<String> list = new ArrayList<>();

    	Optional<String> next = args.nextIfPresent();
    	
    	if(next.isPresent()) {
    		if(context.hasAny("world")) {
        		WorldProperties properties = context.<WorldProperties>getOne("world").get();

        		for(Entry<String, String> entry : properties.getGameRules().entrySet()) {
        			if(entry.getKey().startsWith(next.get())) {
        				list.add(entry.getKey());
        			}
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
