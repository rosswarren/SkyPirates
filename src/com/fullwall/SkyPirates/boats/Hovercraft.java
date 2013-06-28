package com.fullwall.SkyPirates.boats;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.fullwall.SkyPirates.*;

public final class Hovercraft extends BoatHandler {
	public Hovercraft(Boat newBoat) {
		super(newBoat);
	}
	
	@Override
	public void movementHandler(Vector vel) {
		Player p = getPlayer();
		
		if (getMaterialInHand() == Material.COAL && p.hasPermission("skypirates.items.coal")) {
			MAX_HOVER_HEIGHT = 4;
		} else {
			MAX_HOVER_HEIGHT = 2;
		}

		int x = boat.getLocation().getBlockX();
		int y = boat.getLocation().getBlockY();
		int z = boat.getLocation().getBlockZ();

		boolean goDown = false;
		int blockY = 0;
		Block block = null;
		boat.getLocation().setYaw((float) (getYaw() * 6));

		for (int i = 0; i != MAX_HOVER_HEIGHT + 64; i++) {
			block = boat.getWorld().getBlockAt(x, y - blockY, z);
			
			if (block.getType() == Material.AIR) {
				blockY += 1;
			} else if (block.getType() == Material.WATER) {
				break;
			} else {
				break;
			}
			
			if (i > MAX_HOVER_HEIGHT + 1) {
				goDown = true;
			}
		}

        if (block != null) {
            hoverHeight = block.getY() + (MAX_HOVER_HEIGHT * 2);
        }

		if (boat.getLocation().getY() < hoverHeight + 0.6) {
			setMotionY(0.35D);
		} else if (goDown && boat.getLocation().getY() > hoverHeight + 0.6) {
			setMotionY(-.25D);
		} else {
			setMotionY(0D);
		}
	}
}
