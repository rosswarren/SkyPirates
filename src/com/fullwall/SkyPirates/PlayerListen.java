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
		
		if (!(p.isInsideVehicle()))
			return;
		
		if (!(p.getVehicle() instanceof Boat))
			return;
		
		if (!(checkBoats((Boat) p.getVehicle())))
			return;
		
		BoatHandler boat = plugin.getBoat(((Boat) p.getVehicle()).getEntityId());
		
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			boat.doRightClick(this.plugin);
		} else if (boat.getDelay() == 0) {
			boat.doArmSwing();
		}
	}

	public boolean checkBoats(Boat boat) {
		plugin.getBoat(boat.getEntityId());
		
		if (plugin.getBoat(boat.getEntityId()) != null) {
			return true;
		}
		
		return false;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		Player p = event.getPlayer();
		
		if (!(p.isInsideVehicle()))
			return;
		
		if (!(p.getVehicle() instanceof Boat))
			return;
		
		if (!(checkBoats((Boat) p.getVehicle())))
			return;
		
		BoatHandler boat = plugin.getBoat(((Boat) p.getVehicle()).getEntityId());
		
		boat.doRightClick(this.plugin);
	}
}
