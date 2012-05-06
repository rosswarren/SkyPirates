package com.fullwall.SkyPirates;

import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Handles Player Events
 * 
 * @author Ross Warren
 */
public class PlayerListen implements Listener {
	public SkyPirates plugin;

	public PlayerListen(SkyPirates plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event){
		if (event.getReason().equals("You moved too quickly :( (Hacking?)")) {
			event.setCancelled(true);
		}
	}

	/**
	 * Called when a player interacts with an object or air. 
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.hasBlock() && event.getClickedBlock().getType() == Material.BOAT) {
			return;
		}
		
		Player p = event.getPlayer();
		
		if (p.isInsideVehicle()
				&& p.getVehicle() instanceof Boat
				&& checkBoats((Boat) p.getVehicle())) {
		
			BoatHandler boatHandler = plugin.getBoatHandler(((Boat) p.getVehicle()).getEntityId());

			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				doSneakOrRightClick(boatHandler);
			} else if (boatHandler.getDelay() == 0) {
				boatHandler.doArmSwing();
			}
		}
	}

	/**
	 * Check if the boat has a handler
	 * 
	 * @param boat	the boat to check for
	 * @return		true if found, else false
	 */
	public Boolean checkBoats(Boat boat) {
		return (plugin.getBoatHandler(boat.getEntityId()) != null);
	}
	
	private void doSneakOrRightClick(BoatHandler boatHandler) {
		boatHandler.doRightClick(this.plugin);
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		Player p = event.getPlayer();
		
		if (p.isInsideVehicle()
				&& p.getVehicle() instanceof Boat
				&& checkBoats((Boat) p.getVehicle())) {
			
			BoatHandler boatHandler = plugin.getBoatHandler(((Boat) p.getVehicle()).getEntityId());
			doSneakOrRightClick(boatHandler);
		}
	}
}
