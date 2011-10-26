package com.fullwall.SkyPirates;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleListener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import com.fullwall.SkyPirates.BoatHandler.Mode;

/**
 * Listener
 * 
 * @author fullwall
 */
public class VehicleListen extends VehicleListener {
	@SuppressWarnings("unused")
	private final SkyPirates plugin;
	public double fromYaw;
	public double toYaw;
	public Location from;
	public Location to;

	public VehicleListen(final SkyPirates plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onVehicleMove(VehicleMoveEvent event) {
		if (!(event.getVehicle() instanceof Boat)
				|| !(event.getVehicle().getPassenger() instanceof Player)) {
			super.onVehicleMove(event);
			return;
		}
		Player p = (Player) event.getVehicle().getPassenger();
		if (!(p.isInsideVehicle()))
			return;
		if (!(PlayerListen.checkBoats((Boat) event.getVehicle())))
			return;

		from = event.getFrom();
		to = event.getTo();

		Boat tempBoat = (Boat) event.getVehicle();
		Vector vel = tempBoat.getVelocity();
		BoatHandler boat = PlayerListen.getBoatHandler(tempBoat);

		boat.doYaw(from, to);
		// boat.doRealisticFriction();

		if (boat.isMoving() && boat.getMovingLastTick())
			boat.movementHandler(vel);

		return;
	}

	@Override
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (!(event.getEntered() instanceof Player))
			return;
		if (!(event.getVehicle() instanceof Boat))
			return;
		Player player = (Player) event.getEntered();
		if (!(Permission.permission(player, "skypirates.player.enable")))
			return;

		BoatHandler boat;
		if ((SkyPirates.playerModes.get(player) == null))
			SkyPirates.playerModes.put(player, Mode.NORMAL);

		if (!(PlayerListen.checkBoats((Boat) event.getVehicle()))) {
			boat = new BoatHandler((Boat) event.getVehicle(),
					SkyPirates.playerModes.get(player), event.getVehicle()
							.getEntityId());

			SkyPirates.boats.put(boat.getEntityId(), boat);
			player.sendMessage(ChatColor.AQUA
					+ "You feel a tingling sensation as you step into the boat.");

		} else {

			boat = SkyPirates.boats.get(event.getVehicle().getEntityId());

			player.sendMessage(ChatColor.AQUA
					+ "You feel a tingling sensation as you step into the boat.");
		}
		boat.setMode(SkyPirates.playerModes.get(player));
		super.onVehicleEnter(event);
	}

	@Override
	public void onVehicleExit(VehicleExitEvent event) {
		if (!(event.getExited() instanceof Player))
			return;
		if (!(event.getVehicle() instanceof Boat))
			return;
		if (!(PlayerListen.checkBoats((Boat) event.getVehicle())))
			return;
		BoatHandler boat = SkyPirates.boats.get(event.getVehicle()
				.getEntityId());
		Player p = (Player) event.getExited();
		p.sendMessage(ChatColor.LIGHT_PURPLE
				+ "The tingling disappears as you hop out.");

		boat.setMode(Mode.NORMAL);
		super.onVehicleExit(event);
	}

	@Override
	public void onVehicleDamage(VehicleDamageEvent event) {
		if (!(event.getVehicle() instanceof Boat)
				|| !(event.getVehicle().getPassenger() instanceof Player)) {
			return;
		}

		if (!(PlayerListen.checkBoats((Boat) event.getVehicle())))
			return;

		Player p = (Player) event.getVehicle().getPassenger();
		BoatHandler boat = SkyPirates.boats.get(event.getVehicle()
				.getEntityId());

		if (!Permission.permission(p, "skypirates.admin.invincible")
				|| !(boat.getItemInHandID() == 49 && Permission.permission(p,
						"skypirates.items.obsidian")))
			return;
		event.setDamage(0);
		event.setCancelled(true);
	}

	@Override
	public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) {
		if (!(event.getVehicle() instanceof Boat)
				|| !(event.getVehicle().getPassenger() instanceof Player)) {
			return;
		}
		event.getVehicle().teleport(event.getVehicle().getPassenger());
	}
}