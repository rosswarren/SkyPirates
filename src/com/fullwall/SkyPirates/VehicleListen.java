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

import com.fullwall.SkyPirates.BoatHandler.Modes;

/**
 * Listener
 * 
 * @author fullwall
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
				
			if (plugin.getBoat(((Boat) event.getVehicle()).getEntityId()) == null) return;
	
			from = event.getFrom();
			to = event.getTo();
	
			Boat tempBoat = (Boat) event.getVehicle();
			Vector vel = tempBoat.getVelocity();
			BoatHandler boat = plugin.getBoat(tempBoat.getEntityId());
	
			boat.doYaw(from, to);
			boat.updateCalendar();
			// boat.doRealisticFriction();
	
			if (boat.isMoving() && boat.getMovingLastTick() == true) {
				boat.movementHandler(vel);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleEnter(final VehicleEnterEvent event) {
		if (event.getEntered() instanceof Player) {
			Player player = (Player) event.getEntered();
			
			if  (event.getVehicle() instanceof Boat && player.hasPermission("skypirates.player.enable")) {
				BoatHandler boat;

				if (plugin.getBoat(((Boat) event.getVehicle()).getEntityId()) == null) {
					boat = new BoatHandler((Boat) event.getVehicle(), Modes.NORMAL, event.getVehicle().getEntityId());
					
					plugin.setBoat(boat.getEntityId(), boat);
				} else {
					boat = plugin.getBoat(event.getVehicle().getEntityId());
				}
				
				this.plugin.sendMessage(player, SkyPirates.Messages.ENTER);
				
				boat.setMode(Modes.NORMAL);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleExit(VehicleExitEvent event) {
		if (event.getExited() instanceof Player 
				&& event.getVehicle() instanceof Boat 
				&& plugin.getBoat(((Boat) event.getVehicle()).getEntityId()) != null) {
		
			BoatHandler boat = plugin.getBoat(event.getVehicle().getEntityId());
			Player p = (Player) event.getExited();
			this.plugin.sendMessage(p, SkyPirates.Messages.EXIT);
			boat.setMode(Modes.NORMAL);
			
			if (this.plugin.getDestroyBoatsOnExit()) {
				boat.destroy();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleDamage(VehicleDamageEvent event) {
		if (event.getVehicle() instanceof Boat 
				&& event.getVehicle().getPassenger() instanceof Player
				&& plugin.getBoat(((Boat) event.getVehicle()).getEntityId()) != null) {
	
			Player p = (Player) event.getVehicle().getPassenger();
			BoatHandler boat = plugin.getBoat(event.getVehicle().getEntityId());
	
			boolean blockDamage = false;
			
			if (p.hasPermission("skypirates.admin.invincible")) {
				blockDamage = true;
			} else if ((boat.getItemInHandID() == 49) && p.hasPermission("skypirates.items.obsidian")) {
				blockDamage = true;
			}
			
			if (blockDamage) {
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
			
			BoatHandler boat = plugin.getBoat(event.getVehicle().getEntityId());
			
			// handle ice breaker stuff
			if (boat.getMode() == BoatHandler.Modes.ICEBREAKER) {
		        Block block = event.getBlock();
		        
		        breakIce(block, 2);
			}
		}
	}
	
	/**
	 * Converts ice to water for the radius around the given block
	 * This method runs immediately
	 * 
	 * @param centreBlock	The point at which to break ice around
	 * @param radius		The radius at which to break ice around
	 */
	private void breakIce(Block centreBlock, int radius) {
        
        // iterate the x axis
        for (int x = -radius; x <= radius; x++) {
        	
        	// iterate the z axis
            for (int z = -radius; z <= radius; z++) {
            	
            	// if it is too far away, don't convert
                if (x * x + z * z > radius * radius) {
                    continue;
                }
                
                // get the block
                Block block = centreBlock.getRelative(x, 0, z);
                
                // convert to water if it is ice
                if (block.getType().equals(Material.ICE)) {
                    block.setType(Material.WATER);
                }
            }
        }
	}
}