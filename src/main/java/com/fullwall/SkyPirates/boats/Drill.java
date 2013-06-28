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
	}
	
	@Override
	public void movementHandler(Vector vel) {
		// no movement
	}
	
	@Override
	public void doRightClick(SkyPirates plugin) {
		super.doRightClick(plugin);
		
		if (canRightClick()) {
			drill();
		}
	}
	
	public void drill() {
        int boatX = boat.getLocation().getBlockX();
        int boatY = boat.getLocation().getBlockY();
        int boatZ = boat.getLocation().getBlockZ();

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				for (int y = 4; y >= 1; y--) {
					Block block = boat.getWorld().getBlockAt(boatX - x, boatY - y, boatZ - z);

                    if (isNotLiquidyOrGassy(block)) {
						block.setType(Material.AIR);
						boat.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType(), 1));
					}
				}
			}
		}
	}

    private boolean isNotLiquidyOrGassy(Block block) {
        return !block.getType().equals(Material.AIR)
                && (block.getType() != Material.BEDROCK)
                && (block.getType() != Material.WATER)
                && (block.getType() != Material.STATIONARY_WATER)
                && (block.getType() != Material.LAVA)
                && (block.getType() != Material.STATIONARY_LAVA);
    }
}
