package com.fullwall.SkyPirates;

import java.util.TimerTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.TNTPrimed;

public class DropTNT extends TimerTask {
	private Item item;

	public DropTNT(Item item) {
		this.item = item;
	}

	@Override
	public void run() {
		Location loc = item.getLocation().subtract(0, 1, 0);
		
		if (item.getWorld().getBlockAt(loc).getType() == Material.AIR) { // check it is in the air
            item.getWorld().spawn(item.getLocation(), TNTPrimed.class);
		}
	}
}
