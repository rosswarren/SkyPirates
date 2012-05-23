package com.fullwall.SkyPirates;

import java.util.ArrayList;
import java.util.Calendar;
import net.minecraft.server.EntityBoat;
import org.bukkit.craftbukkit.entity.CraftBoat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class BoatHandler {
	public final Boat boat;
	
	protected Calendar cal;
	
	protected long delay = 0;
	
	private final double maxMomentum = 10D;
	public double fromYaw = 0D;
	public double toYaw = 0D;
	protected int hoverHeight = 0;
	private double maxSpeed = 0.8D;
	protected Boolean goingDown = false;
	protected Boolean goingUp = false;
	protected ArrayList<Material> helmets = new ArrayList<Material>();

	protected final double DOWNWARD_DRIFT = -0.037999998673796664D;
	protected final double COMPENSATION = 0.0379999999999999999999999999999999999999999999D;
	protected final double MAX_BUOYANCY = 0.1D;
	protected int MAX_HOVER_HEIGHT = 1;
	
	protected Boolean cancelRightClick = false;

	public BoatHandler(Boat newBoat) {
		boat = newBoat;
		boat.setMaxSpeed(maxSpeed);
		boat.setWorkOnLand(true);
		cal = Calendar.getInstance();

		populateHelmets();
	}
	
	/**
	 * remove the boat
	 */
	public void destroy() {
		boat.remove();
		getPlayer().getInventory().addItem(new ItemStack(Material.BOAT, 1));
	}

	protected double getYaw() {
		return boat.getLocation().getYaw();
	}

	public void setYaw(double fromYaw, double toYaw) {
		this.fromYaw = fromYaw;
		this.toYaw = toYaw;
		return;
	}

	protected void setMotion(double motionX, double motionY, double motionZ) {
		Vector newVelocity = new Vector();
		newVelocity.setX(motionX);
		newVelocity.setY(motionY);
		newVelocity.setZ(motionZ);
		boat.setVelocity(newVelocity);
	}

	public void setMotionX(double motionX) {
		motionX = RangeHandler.range(motionX, maxMomentum, -maxMomentum);
		setMotion(motionX, boat.getVelocity().getY(), boat.getVelocity().getZ());
	}

	public void setMotionY(double motionY) {
		motionY = RangeHandler.range(motionY, maxMomentum, -maxMomentum);
		setMotion(boat.getVelocity().getX(), motionY, boat.getVelocity().getZ());
	}

	public void setMotionZ(double motionZ) {
		motionZ = RangeHandler.range(motionZ, maxMomentum, -maxMomentum);
		setMotion(boat.getVelocity().getX(), boat.getVelocity().getY(), motionZ);
	}

	public int getX() {
		return boat.getLocation().getBlockX();
	}

	public int getY() {
		return boat.getLocation().getBlockY();
	}

	public int getZ() {
		return boat.getLocation().getBlockZ();
	}

	public Block getBlockBeneath() {
		return boat.getWorld().getBlockAt(getX(), getY() - 1, getZ());
	}
	
	public Material getMaterialInHand() {
		return getPlayer().getItemInHand().getType();
	}
	
	protected Material getHelmetMaterial() {
		return getPlayer().getInventory().getHelmet().getType();
	}

	protected Player getPlayer() {
		final Player p = (Player) boat.getPassenger();
		return p;
	}

	public int getEntityId() {
		return boat.getEntityId();
	}

	protected boolean isGrounded() {
		EntityBoat be = (EntityBoat) ((CraftBoat) this.boat).getHandle();
		return be.onGround;
	}


	public void stopBoat() {
		setMotion(0D, 0D, 0D);
	}

	public void updateCalendar() {
		Calendar current = Calendar.getInstance();
		if (cal.get(Calendar.SECOND) != current.get(Calendar.SECOND)) {
			cal = current;
		}
	}

	public void resetValues() {
		goingDown = false;
		goingUp = false;
		delay = 0;
	}

	public void doRealisticFriction() {
		if (getPlayer() == null) {
			setMotion(boat.getVelocity().getX() * 0.53774, boat.getVelocity().getY(), boat.getVelocity().getZ() * 0.53774);
		}
	}

	public void speedUpBoat(double factor, Vector vel) {
		double curX = vel.getX();
		double curZ = vel.getZ();
		double newX = curX * factor;

		if (newX < 0) {
			newX = -maxSpeed;
		} else {
			newX = maxSpeed;
		}
		
		double newZ = 0D;
		
		if (curZ != 0D) {
			newZ = maxSpeed / Math.abs(curX / curZ);
			
			if (curZ < 0) {
				newZ *= -1;
			}
		}
		this.setMotion(newX, vel.getY(), newZ);
		return;
	}

	public abstract void movementHandler(Vector vel);

	public void doArmSwing() {
		Player p = getPlayer();
		
		if (getMaterialInHand() == Material.COAL && p.hasPermission("skypirates.items.coal")) {
			setMotionY(0.75D);
			delay = cal.getTimeInMillis() + 750;
		} else {
			setMotionY(0.5D);
			delay = cal.getTimeInMillis();
		}
	}

	public void doRightClick(SkyPirates plugin) {
		Player p = getPlayer();
		
		cancelRightClick = true;
		
		if (getMaterialInHand() == Material.DIAMOND && p.hasPermission("skypirates.items.diamond")) {
			if (!p.isSneaking()) {
				speedUpBoat(10, boat.getVelocity());
				p.sendMessage(ChatColor.BLUE + "Boost!");
			}
		} else if (getMaterialInHand() == Material.ARROW && p.hasPermission("skypirates.items.arrow")) {
			p.launchProjectile(Arrow.class);
		} else if (getMaterialInHand() == Material.SNOW_BLOCK && p.hasPermission("skypirates.items.snowblock")) {
			stopBoat();
			plugin.sendMessage(p, SkyPirates.Messages.STOP);
		} else {
			cancelRightClick = false;
		}
	}

	public void doYaw(Location from, Location to) {
		fromYaw = (double) from.getYaw();
		toYaw = (double) to.getYaw();
		// attempt to increase rotation on land
		if (toYaw >= fromYaw - .025 && toYaw <= fromYaw + .025) {
			to.setYaw((float) (fromYaw * 2.8));
		}
		// turning while high forward speed
		else if (toYaw >= fromYaw - .7 && toYaw <= fromYaw + .7) {
			to.setYaw((float) (fromYaw * 5.3));
		}
		// turning at low forward speed (catch-all)
		else if (toYaw >= fromYaw - 3 && toYaw <= fromYaw + 3) {
			to.setYaw((float) (fromYaw * 3.3));
		}
		return;
	}
	
	public long getDelay() {
		return delay;
	}
	
	private void populateHelmets() {
		helmets.add(Material.LEATHER_HELMET);
		helmets.add(Material.IRON_HELMET);
		helmets.add(Material.DIAMOND_HELMET);
		helmets.add(Material.GOLD_HELMET);
		helmets.add(Material.PUMPKIN);
	}
}
