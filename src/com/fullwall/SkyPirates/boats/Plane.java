package com.fullwall.SkyPirates.boats;

import java.util.Timer;

import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.fullwall.SkyPirates.*;

public final class Plane extends BoatHandler {
	public Plane(Boat newBoat) {
		super(newBoat);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void doArmSwing() {
		Player p = getPlayer();
		
		if (getMaterialInHand() == Material.COAL && p.hasPermission("skypirates.items.coal")) {
			goingUp = true;
			setMotionY(0.5D);
		} else {
			goingUp = true;
			setMotionY(0.5D);
		}
	}
	
	@Override
	public void doRightClick(SkyPirates plugin) {
		super.doRightClick(plugin);
		
		Player p = getPlayer();
		
		if (getMaterialInHand() == Material.TNT & p.hasPermission("skypirates.items.tnt")) {
			Item item = getPlayer().getWorld().dropItemNaturally(p.getLocation(), new ItemStack(Material.TNT, 1));
			Timer t = new Timer();
			t.schedule(new DropTNT(item), 1000);
			
			cancelRightClick = true;
		}
		
		if (!cancelRightClick) {
			goingDown = true;
			setMotionY(-0.65D);
		}
	}
	
	@Override
	public void movementHandler(Vector vel) {
		if (goingUp) {
			// vel.setY(vel.getY() - 0.02);
			if (vel.getY() <= 0D) {
				goingUp = false;
				vel.setY(0D);
			}
			
			setMotion(vel.getX(), vel.getY(), vel.getZ());
			return;
		}
		
		if (goingDown) {
			if (vel.getY() <= 0D) {
				vel.setY(vel.getY() + 0.25);
				if (vel.getY() >= 0D) goingDown = false;
			}
			
			setMotion(vel.getX(), vel.getY(), vel.getZ());
		} else if (vel.getY() <= 0D) {
			// workaround for bukkit glitch - setting motion to 0 still results
			// in downward moving. Not perfect, but it's the best it's going to
			// get without
			// more manipulation, like -(vel.getY())/2.5) etc.
			if (boat.getVelocity().getY() <= 0 && boat.getVelocity().getY() >= DOWNWARD_DRIFT) {
				vel.setY(COMPENSATION);
			} else {
				vel.setY(0D);
			}
			
			setMotion(vel.getX(), vel.getY(), vel.getZ());
		} else {
			// see above.
			if (boat.getVelocity().getY() <= 0 && boat.getVelocity().getY() >= DOWNWARD_DRIFT) {
				vel.setY(COMPENSATION);
			} else {
				vel.setY(0D);
			}
			
			setMotion(vel.getX(), vel.getY(), vel.getZ());
		}
	}
}
