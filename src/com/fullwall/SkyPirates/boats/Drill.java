package com.fullwall.SkyPirates.boats;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.fullwall.SkyPirates.*;

public final class Drill extends BoatHandler {
	public Drill(Boat newBoat) {
		super(newBoat);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void movementHandler(Vector vel) {
		// no movement
	}
	
	@Override
	public void doRightClick(SkyPirates plugin) {
		super.doRightClick(plugin);
		
		if (!cancelRightClick) {
			drill();
		}
	}
	
	public void drill() {		
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				for (int y = 4; y >= 1; y--) {
					Block block = boat.getWorld().getBlockAt(
							boat.getLocation().getBlockX() - x,
							boat.getLocation().getBlockY() - y,
							boat.getLocation().getBlockZ() - z);
					if (!block.getType().equals(Material.AIR)
							&& (block.getTypeId() != 7)
							&& (block.getTypeId() != 8)
							&& (block.getTypeId() != 9)
							&& (block.getTypeId() != 10)
							&& (block.getTypeId() != 11)) {
						Material mat = block.getType();
						block.setType(Material.AIR);
						boat.getWorld().dropItemNaturally(block.getLocation(),
								new ItemStack(mat, 1));
					}
				}
			}
		}
	}
}
