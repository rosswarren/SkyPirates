package com.fullwall.SkyPirates;

import com.fullwall.SkyPirates.boats.Boats;
import org.bukkit.Location;
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
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import com.fullwall.SkyPirates.boats.Icebreaker;
import com.fullwall.SkyPirates.boats.Normal;

public class EventListener implements Listener {
    private Boats boats;

    public Location from;
	public Location to;
    private MessageHandler messageHandler;

    public EventListener(Boats boats, MessageHandler messageHandler) {
		this.boats = boats;
        this.messageHandler = messageHandler;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event){
		if (event.getReason().equals("You moved too quickly :( (Hacking?)")) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.hasBlock() && event.getClickedBlock().getType() == Material.BOAT) {
			return;
		}
		
		Player p = event.getPlayer();
		
		if (p.isInsideVehicle()
				&& p.getVehicle() instanceof Boat
				&& boats.contains(p.getVehicle())) {


			BoatHandler boatHandler = boats.getHandler(p.getVehicle());

			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				doSneakOrRightClick(boatHandler);
			} else if (boatHandler.getDelay() == 0) {
				boatHandler.doArmSwing();
			}
		}
	}
	
	private void doSneakOrRightClick(BoatHandler boatHandler) {
		boatHandler.doRightClick();
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		Player p = event.getPlayer();
		
		if (p.isInsideVehicle()
				&& p.getVehicle() instanceof Boat
                && boats.contains(p.getVehicle())) {
			
			BoatHandler boatHandler = boats.getHandler(p.getVehicle());
			doSneakOrRightClick(boatHandler);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleMove(final VehicleMoveEvent event) {
		if (event.getVehicle() instanceof Boat && event.getVehicle().getPassenger() instanceof Player) {
			Player p = (Player) event.getVehicle().getPassenger();
			
			if (!(p.isInsideVehicle())) return;

            if (!boats.contains(p.getVehicle())) return;
	
			from = event.getFrom();
			to = event.getTo();
	
			Boat tempBoat = (Boat) event.getVehicle();
			Vector vel = tempBoat.getVelocity();
			BoatHandler boatHandler = boats.getHandler(p.getVehicle());
	
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

				if (!boats.contains(player.getVehicle())) {
					boatHandler = new Normal((Boat) event.getVehicle());

                    boats.handle(event.getVehicle(), boatHandler);
				}
				
				messageHandler.sendMessage(player, Messages.ENTER);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleExit(VehicleExitEvent event) {
		if (event.getExited() instanceof Player 
				&& event.getVehicle() instanceof Boat 
				&& boats.contains(event.getVehicle())) {
		
			BoatHandler boat = boats.getHandler(event.getVehicle());

            boats.remove(event.getVehicle());

			Player p = (Player) event.getExited();
            messageHandler.sendMessage(p, Messages.EXIT);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVehicleDamage(VehicleDamageEvent event) {
		if (event.getVehicle() instanceof Boat 
				&& event.getVehicle().getPassenger() instanceof Player
                && boats.contains(event.getVehicle())) {
	
			Player p = (Player) event.getVehicle().getPassenger();
			BoatHandler boat = boats.getHandler(p.getVehicle());
			
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
		if (event.getVehicle() instanceof Boat && event.getVehicle().getPassenger() instanceof Player) {
			
			BoatHandler boat = boats.getHandler(event.getVehicle());
			
			// handle ice breaker stuff
			if (boat instanceof Icebreaker) {
		        ((Icebreaker) boat).breakIce(boat.getBlockLocation(), 4);
			}
		}
	}
}
