package com.fullwall.SkyPirates.boats;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.util.Vector;

import com.fullwall.SkyPirates.*;

public final class Glider extends BoatHandler {
	public Glider(Boat newBoat) {
		super(newBoat);
	}
	
	@Override
	public void doArmSwing() {
		if (!getBlockBeneath().isLiquid()) {
			speedUpBoat(10, boat.getVelocity());
		}
	}
	
	@Override
	public void movementHandler(Vector vel) {
		if (getBlockBeneath().getType() == Material.AIR) {
			vel.setY(-0.075D);
		}
		
		if (vel.getY() < -0.075D) {
			vel.setY(-0.075D);
		}
		
		setMotion(vel.getX(), vel.getY(), vel.getZ());
	}

    private Block getBlockBeneath() {
        return boat.getWorld().getBlockAt(boat.getLocation().subtract(0, 1, 0));
    }
}
