package com.fullwall.SkyPirates;

import org.bukkit.Location;
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
				
			if (!(PlayerListen.checkBoats((Boat) event.getVehicle()))) return;
	
			from = event.getFrom();
			to = event.getTo();
	
			Boat tempBoat = (Boat) event.getVehicle();
			Vector vel = tempBoat.getVelocity();
			BoatHandler boat = PlayerListen.getBoatHandler(tempBoat);
	
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
		
				if ((SkyPirates.playerModes.get(player) == null)) {
					SkyPirates.playerModes.put(player, Modes.NORMAL);
				}
				
				if (!(PlayerListen.checkBoats((Boat) event.getVehicle()))) {
					boat = new BoatHandler((Boat) event.getVehicle(), SkyPirates.playerModes.get(player), event.getVehicle().getEntityId());
					SkyPirates.boats.put(boat.getEntityId(), boat);
				} else {
					boat = SkyPirates.boats.get(event.getVehicle().getEntityId());
					
				}
				
				this.plugin.sendMessage(player, SkyPirates.Messages.ENTER);
				
				boat.setMode(SkyPirates.playerModes.get(player));
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleExit(VehicleExitEvent event) {
		if (event.getExited() instanceof Player 
				&& event.getVehicle() instanceof Boat 
				&& PlayerListen.checkBoats((Boat) event.getVehicle())) {
		
			BoatHandler boat = SkyPirates.boats.get(event.getVehicle().getEntityId());
			Player p = (Player) event.getExited();
			this.plugin.sendMessage(p, SkyPirates.Messages.EXIT);
			boat.setMode(Modes.NORMAL);
			
			if (this.plugin.destroyBoatsOnExit) {
				boat.destroy();
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleDamage(VehicleDamageEvent event) {
		if (event.getVehicle() instanceof Boat 
				&& event.getVehicle().getPassenger() instanceof Player
				&& PlayerListen.checkBoats((Boat) event.getVehicle())) {
	
			Player p = (Player) event.getVehicle().getPassenger();
			BoatHandler boat = SkyPirates.boats.get(event.getVehicle().getEntityId());
	
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
		if (event.getVehicle() instanceof Boat &&
				event.getVehicle().getPassenger() instanceof Player) {
			event.getVehicle().teleport(event.getVehicle().getPassenger());
		}
	}

	public SkyPirates getPlugin() {
		return plugin;
	}
}