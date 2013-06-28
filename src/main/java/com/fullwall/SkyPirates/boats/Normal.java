package com.fullwall.SkyPirates.boats;

import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.fullwall.SkyPirates.*;

public final class Normal extends BoatHandler {
	public Normal(Boat newBoat) {
		super(newBoat);
	}
	
	@Override
	public void movementHandler(Vector vel) {
		Entity ce = this.boat.getPassenger();
		Vector playerVelocity = ce.getVelocity().clone();
		
		double playerVelocityX = playerVelocity.getX();
		double playerVelocityZ = playerVelocity.getZ();

		if ((playerVelocityX != 0D || playerVelocityZ != 0D) && isGrounded()) {
			boat.getLocation().setYaw((float) (getYaw() * 2.5));
			speedUpBoat(10, boat.getVelocity());
		}

		double currentX = vel.getX();
		double currentZ = vel.getZ();
		
		boolean boostSteering = false;

		if ((playerVelocityX < 0 && currentX > 0) || (playerVelocityX > 0 && currentX < 0)) {
			boostSteering = true;
		}
		
		if (!boostSteering && (playerVelocityZ < 0 && currentZ > 0) || (playerVelocityZ > 0 && currentZ < 0)) {
			boostSteering = true;
		}
		
		if (boostSteering) {
			currentX = currentX / 1.2D + playerVelocityX;
			currentZ = currentZ / 1.2D + playerVelocityZ;
			this.setMotion(currentX, vel.getY(), currentZ);
		}
		
		if (cal.getTimeInMillis() >= delay + 3000) {
			delay = 0;
		}
	}
}
