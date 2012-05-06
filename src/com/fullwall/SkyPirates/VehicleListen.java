package com.fullwall.SkyPirates;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;
import com.fullwall.SkyPirates.boats.*;

/**
 * Handles Vehicle Events
 * 
 * @author Ross Warren
 */
public class VehicleListen implements Listener {
	private final SkyPirates plugin;
	public double fromYaw;
	public double toYaw;
	public Location from;
	public Location to;

	public VehicleListen(final SkyPirates plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleMove(final VehicleMoveEvent event) {
		if (event.getVehicle() instanceof Boat && event.getVehicle().getPassenger() instanceof Player) {
			Player p = (Player) event.getVehicle().getPassenger();
			
			if (!(p.isInsideVehicle())) return;
				
			if (plugin.getBoatHandler(((Boat) event.getVehicle()).getEntityId()) == null) return;
	
			from = event.getFrom();
			to = event.getTo();
	
			Boat tempBoat = (Boat) event.getVehicle();
			Vector vel = tempBoat.getVelocity();
			BoatHandler boatHandler = plugin.getBoatHandler(tempBoat.getEntityId());
	
			boatHandler.doYaw(from, to);
			boatHandler.updateCalendar();

			boatHandler.movementHandler(vel);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleEnter(final VehicleEnterEvent event) {
		if (event.getEntered() instanceof Player) {
			Player player = (Player) event.getEntered();
			
			if  (event.getVehicle() instanceof Boat && player.hasPermission("skypirates.player.enable")) {
				BoatHandler boatHandler;

				if (plugin.getBoatHandler(((Boat) event.getVehicle()).getEntityId()) == null) {
					boatHandler = new Normal((Boat) event.getVehicle());
					
					plugin.setBoat(boatHandler.getEntityId(), boatHandler);
				} else {
					boatHandler = plugin.getBoatHandler(event.getVehicle().getEntityId());
				}
				
				this.plugin.sendMessage(player, SkyPirates.Messages.ENTER);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleExit(VehicleExitEvent event) {
		if (event.getExited() instanceof Player 
				&& event.getVehicle() instanceof Boat 
				&& plugin.getBoatHandler(((Boat) event.getVehicle()).getEntityId()) != null) {
		
			BoatHandler boat = plugin.getBoatHandler(event.getVehicle().getEntityId());
			plugin.removeBoatHandler(event.getVehicle().getEntityId());
			Player p = (Player) event.getExited();
			this.plugin.sendMessage(p, SkyPirates.Messages.EXIT);
			
			if (this.plugin.getDestroyBoatsOnExit()) {
				boat.destroy();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleDamage(VehicleDamageEvent event) {
		if (event.getVehicle() instanceof Boat 
				&& event.getVehicle().getPassenger() instanceof Player
				&& plugin.getBoatHandler(((Boat) event.getVehicle()).getEntityId()) != null) {
	
			Player p = (Player) event.getVehicle().getPassenger();
			BoatHandler boat = plugin.getBoatHandler(event.getVehicle().getEntityId());
			
			if (p.hasPermission("skypirates.admin.invincible")
					|| (boat.getMaterialInHand() == Material.OBSIDIAN && p.hasPermission("skypirates.items.obsidian"))) {
				event.setDamage(0);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
		// check that the vehicle is a boat and that the passenger is a player
		if (event.getVehicle() instanceof Boat &&
			event.getVehicle().getPassenger() instanceof Player) {
			
			BoatHandler boat = plugin.getBoatHandler(event.getVehicle().getEntityId());
			
			// handle ice breaker stuff
			if (boat.getClass() == Icebreaker.class) {
		        Block block = event.getBlock();
		        ((Icebreaker) boat).breakIce(block, 2);
			}
		}
	}
}