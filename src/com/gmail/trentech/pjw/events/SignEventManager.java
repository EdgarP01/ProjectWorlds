package com.gmail.trentech.pjw.events;

import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.title.Titles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.gmail.trentech.pjw.Main;
import com.gmail.trentech.pjw.commands.CMDYes;
import com.gmail.trentech.pjw.utils.Resource;

public class SignEventManager {
	
	@Listener
	public void onSignCreateEvent(ChangeSignEvent event) {
		Player player = null;
		Optional<Player> playerOptional = event.getCause().first(Player.class);
		if(playerOptional.isPresent()){
			player = playerOptional.get();
		}

		SignData signData = event.getText();

		ListValue<Text> lines = signData.getValue(Keys.SIGN_LINES).get();

		Text portalSign = Texts.of("[Portal]");
		if(!lines.get(0).equals(portalSign)) {
			return;
		}

		if(!player.hasPermission("pjw.sign.place." + player.getWorld().getName())) {
			player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to place portal signs in this world"));
			event.setCancelled(true);
			return;
		}
		
		if(!Main.getGame().getServer().getWorld(Texts.toPlain(lines.get(1))).isPresent()){
			player.sendMessage(Texts.of(TextColors.DARK_RED, Texts.toPlain(lines.get(1)), " does not exist"));
			event.setCancelled(true);
			return;
		}

		lines.set(0, Texts.of(TextColors.DARK_BLUE, "[Portal]"));

		event.getText().set(lines);
		event.getTargetTile().getLocation().getExtent().spawnParticles(Main.getGame().getRegistry().createBuilder(ParticleEffect.Builder.class).type(ParticleTypes.EXPLOSION_NORMAL).build(), event.getTargetTile().getLocation().getPosition().add(0, 1, 0));
	}

	@Listener
	public void onSignInteractEvent(InteractBlockEvent.Secondary event) {
		if(!(event.getTargetBlock().getState().getType().equals(BlockTypes.WALL_SIGN) || event.getTargetBlock().getState().getType().equals(BlockTypes.STANDING_SIGN))){
			return;
		}

		Optional<Player> playerOptional = event.getCause().first(Player.class);
		if(!playerOptional.isPresent()){
			return;
			
		}
		Player player = playerOptional.get();

		Location<World> block = event.getTargetBlock().getLocation().get();

		Optional<SignData> data = block.getOrCreate(SignData.class);
		if(!data.isPresent()){
			return;
		}
		SignData signData = data.get();

		ListValue<Text> lines = signData.getValue(Keys.SIGN_LINES).get();

		Text portalSign = Texts.of(TextColors.DARK_BLUE, "[Portal]");
		if(!lines.get(0).equals(portalSign)) {
        	return;
		}

		if(!Main.getGame().getServer().getWorld(Texts.toPlain(lines.get(1))).isPresent()){
			player.sendMessage(Texts.of(TextColors.DARK_RED, Texts.toPlain(lines.get(1)), " does not exist"));
			event.setCancelled(true);
			return;
		}		
		World world = Main.getGame().getServer().getWorld(Texts.toPlain(lines.get(1))).get();
		
		if(!player.hasPermission("pjw.sign.interact." + world.getName())) {
			player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to interact with portal signs"));
			event.setCancelled(true);
			return;
		}
		
		Location<World> playerLocation = player.getLocation();
		
		if(!player.setLocationSafely(world.getSpawnLocation())){
			CMDYes.players.put(player, world.getSpawnLocation());
			player.sendMessage(Texts.builder().color(TextColors.DARK_RED).append(Texts.of("Unsafe spawn point detected. Teleport anyway? ")).onClick(TextActions.runCommand("/yes")).append(Texts.of(TextColors.GOLD, TextStyles.UNDERLINE, "Click Here")).build());
			return;
		}

		Resource.particles(playerLocation);
		Resource.particles(player.getLocation());
		
		player.sendTitle(Titles.of(Texts.of(TextColors.GOLD, world.getName()), Texts.of(TextColors.DARK_PURPLE, "x: ", world.getSpawnLocation().getBlockX(), ", y: ", world.getSpawnLocation().getBlockY(),", z: ", world.getSpawnLocation().getBlockZ())));
	}
	
	@Listener
	public void onSignBreakEvent(ChangeBlockEvent.Break event) {
		Optional<Player> playerOptional = event.getCause().first(Player.class);
	    if (!playerOptional.isPresent()) {
	    	return;
	    }

	    SignData signData = null;
	    for(Transaction<BlockSnapshot> blockTransaction : event.getTransactions()){
	    	Optional<Location<World>> blockOptional = blockTransaction.getOriginal().getLocation();	    	
	    	if(blockOptional.isPresent()){
	    		
	    		Location<World> block = blockOptional.get();
	    		if(block.getBlock().getType().equals(BlockTypes.WALL_SIGN) || block.getBlock().getType().equals(BlockTypes.STANDING_SIGN)){
    				Optional<SignData> signDataOptional = block.getOrCreate(SignData.class);
    				if(signDataOptional.isPresent()){
    					signData = signDataOptional.get();
    					break;
    				}
	    		}
	    	}
	    }

	    if(signData == null){
	    	return;
	    }
	    
		ListValue<Text> lines = signData.getValue(Keys.SIGN_LINES).get();

		Text kitSign = Texts.of(TextColors.DARK_BLUE, "[Portal]");
		if(!lines.get(0).equals(kitSign)) {
        	return;
		}

		Player player = playerOptional.get();
		if(!player.hasPermission("pjw.sign.break")) {
			player.sendMessage(Texts.of(TextColors.DARK_RED, "You do not have permission to break portal signs"));
			event.setCancelled(true);
			return;
		}
	}
}
