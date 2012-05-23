package com.fullwall.SkyPirates.boats;

import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.fullwall.SkyPirates.*;

public final class Submarine extends BoatHandler {
	public Submarine(Boat newBoat) {
		super(newBoat);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void doArmSwing() {
		goingUp = true;
		setMotionY(0.1D);
	}
	
	@Override
	public void doRightClick(SkyPirates plugin) {
		super.doRightClick(plugin);

		if (!cancelRightClick) {
			goingDown = true;
			setMotionY(-0.2D);
		}
	}
	
	@Override
	public void movementHandler(Vector vel) {
		// apply 'gravity' - aims to just be gentle downward motion
		Player p = getPlayer();

		vel.setY(vel.getY() - 0.03);

		// cap y velocity to combat buoyancy
		if (vel.getY() > MAX_BUOYANCY) {
			vel.setY(MAX_BUOYANCY);
		}
		
		if (goingUp == false && vel.getY() > 0) {
			vel.setY(-0.15D);
		}

		// raise rotation to stop slow turning
		boat.getLocation().setYaw((float) (getYaw() * 2));
		
		// stop players from drowning underwater
		if (p.getRemainingAir() != p.getMaximumAir()) {		
			if ((p.hasPermission("skypirates.player.air")) || ((helmets.contains(getHelmetMaterial()) && p.hasPermission("skypirates.items.helmets")))) {
				p.setRemainingAir(p.getMaximumAir());
				p.setMaximumAir(p.getMaximumAir());
			}
		}

		if (goingUp == true) {
			vel.setY(vel.getY() - 0.009);
			
			if (vel.getY() <= 0.025D) {
				goingUp = false;
				vel.setY(0D);
			}
		} else if (goingDown == true) {
			if (vel.getY() <= -0.6D) {
				vel.setY(-0.6D);
				
				if (vel.getY() >= 0D) goingDown = false;
			}
		}
		
		setMotion(vel.getX(), vel.getY(), vel.getZ());
	}
}
