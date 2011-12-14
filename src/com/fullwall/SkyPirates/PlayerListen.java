package com.fullwall.SkyPirates;

import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerListen extends PlayerListener {
	public SkyPirates plugin;

	public PlayerListen(SkyPirates plugin) {
		this.plugin = plugin;
	}

	/**
	 * Called when a player interacts with an object or air. 
	 */
	@Override
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
		
		BoatHandler boat = getBoatHandler((Boat) p.getVehicle());
		
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
			boat.doRightClick();
		
		else if (boat.getDelay() == 0)
			boat.doArmSwing();
	}

	public static boolean checkBoats(Boat boat) {
		if (SkyPirates.boats != null && SkyPirates.boats.get(boat.getEntityId()) != null) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		Player p = event.getPlayer();
		
		if (!(p.isInsideVehicle()))
			return;
		
		if (!(p.getVehicle() instanceof Boat))
			return;
		
		if (!(checkBoats((Boat) p.getVehicle())))
			return;
		
		BoatHandler boat = getBoatHandler((Boat) p.getVehicle());
		
		boat.doRightClick();
	}
	

	public static BoatHandler getBoatHandler(Boat boat) {
		return SkyPirates.boats.get(boat.getEntityId());
	}
}
