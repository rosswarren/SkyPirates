package com.fullwall.SkyPirates.boats;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.fullwall.SkyPirates.*;

public final class Icebreaker extends BoatHandler {
	public Icebreaker(Boat newBoat) {
		super(newBoat);
	}
	
	@Override
	public void movementHandler(Vector velocity) {
		Entity passenger = boat.getPassenger();
		Vector playerVelocity = passenger.getVelocity().clone();
		
		double playerVelocityX = playerVelocity.getX();
		double playerVelocityZ = playerVelocity.getZ();

		if ((playerVelocityX != 0D || playerVelocityZ != 0D) && isGrounded()) {
			boat.getLocation().setYaw((float) (getYaw() * 2.5));
			speedUpBoat(10, boat.getVelocity());
		}

		double currentX = velocity.getX();
		double currentZ = velocity.getZ();
		
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
			this.setMotion(currentX, velocity.getY(), currentZ);
		}
		
		if (cal.getTimeInMillis() >= delay + 3000) {
			delay = 0;
		}
	}
	
	/**
	 * Converts ice to water for the radius around the given block
	 * This method runs immediately
	 * 
	 * @param centreBlock	The point at which to break ice around
	 * @param radius		The radius at which to break ice around
	 */
	public void breakIce(Block centreBlock, int radius) {
        // iterate the x axis
        for (int x = -radius; x <= radius; x++) {
        	
        	// iterate the z axis
            for (int z = -radius; z <= radius; z++) {

                for (int y = -radius; y <= radius; y++) {
                    // if it is too far away, don't convert
                    if (x * x + z * z + y * y > radius * radius) {
                        continue;
                    }

                    // get the block
                    Block block = centreBlock.getRelative(x, y, z);

                    // convert to water if it is ice
                    if (block.getType().equals(Material.ICE)) {
                        block.setType(Material.WATER);
                    }
                }
            }
        }
	}
}