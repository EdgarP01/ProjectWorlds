package com.gmail.trentech.pjw.utils;

import java.util.concurrent.TimeUnit;

import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weather;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.managers.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class TimeLock {

    public Task task;

    public void start() {
    	Main.getLog().info("Creating tasks...");
        task = Main.getGame().getScheduler().createTaskBuilder().interval(5, TimeUnit.SECONDS).name("timelock").execute(new Runnable() {
			@Override
            public void run() {			
				for(World world : Main.getGame().getServer().getWorlds()){
					ConfigurationNode timeNode = new ConfigManager().getConfig().getNode("Worlds", world.getName(), "Time");
					if(timeNode.getNode("Lock").getBoolean()){
						long time = timeNode.getNode("Set").getLong();
						world.getProperties().setWorldTime(time);
					}
					// TEMPORARY UNTIL WEATHER EVENT IMPLEMENTED
					ConfigurationNode weatherNode = new ConfigManager().getConfig().getNode("Worlds", world.getName(), "Weather");
					if(weatherNode.getNode("Lock").getBoolean()){
						String weather = weatherNode.getNode("Set").getString();
						if(!world.getWeather().getName().equalsIgnoreCase(weather)){
							world.forecast(Main.getGame().getRegistry().getType(Weather.class, weather).get());
						}	
					}
				}
            }
        }).submit(Main.getPlugin());
	}    
}
