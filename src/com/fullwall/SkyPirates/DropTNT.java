package com.fullwall.SkyPirates;

import java.util.TimerTask;

import net.minecraft.server.EntityTNTPrimed;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Item;

/**
 * Explodes TNT when the timer is up
 * @author Ross
 */
public class DropTNT extends TimerTask {
	private Item item;

	/**
	 * Constructor with the item passed in (should be TNT)
	 * @param i
	 */
	public DropTNT(Item item) {
		this.item = item;
	}
	
	/**
	 * runs when the timer completes. Will explode the dropped TNT.
	 */
	@Override
	public void run() {
		Location loc = item.getLocation(); // get the location of the dropped TNT
		
		int x = loc.getBlockX(); // get x coordinate
		int y = loc.getBlockY(); // get y coordinate
		int z = loc.getBlockZ(); // get z coordinate
		
		if (item.getWorld().getBlockAt(x, y - 1, z).getType() == Material.AIR) { // check it is in the air
			Block b = item.getWorld().getBlockAt(item.getLocation()); // get the block it is in
			item.remove(); // remove the non primed TNT
			b.setType(Material.TNT); // set the block it was in to TNT block instead of item
			
			CraftWorld world = (CraftWorld)b.getWorld(); // get the world object
			EntityTNTPrimed tnt = new EntityTNTPrimed(world.getHandle(), b.getX() + 0.5F, b.getY() + 0.5F, b.getZ() + 0.5F); // make a new primed TNT object in the location the non primed was in

			world.getHandle().addEntity(tnt);// add the primed TNT to the world
			b.setType(Material.AIR);  // remove the non primed TNT
		}
	}
}
