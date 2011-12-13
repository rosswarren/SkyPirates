package com.fullwall.SkyPirates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;

import com.fullwall.SkyPirates.BoatHandler.Modes;

/**
 * SkyPirates for Bukkit
 * 
 * @author fullwall
 */
public class SkyPirates extends JavaPlugin {
	public VehicleListen vehicleListen = new VehicleListen(this);
	public PlayerListen playerListen = new PlayerListen(this);
	public static HashMap<Player, Modes> playerModes = new HashMap<Player, Modes>();
	public static HashMap<Integer, BoatHandler> boats = new HashMap<Integer, BoatHandler>();
	public static ArrayList<String> helmets = new ArrayList<String>();
	
	public enum Messages {
		NO_PERMISSION,
		NO_BOATS_TO_REMOVE,
		NOT_IN_BOAT,
		PLANE,
		PLANE_NO_PERMISSION,
		SUBMARINE,
		SUBMARINE_NO_PERMISSION,
		HOVER,
		HOVER_NO_PERMISSION,
		GLIDER,
		GLIDER_NO_PERMISSION,
		DRILL,
		DRILL_NO_PERMISSION,
		NORMAL,
		HELP
	}
	
	public enum Commands {
		CLEAR,
		HELP,
		NORMAL,
		PLANE,
		HOVER,
		GLIDER,
		DRILL
	}

	public static SkyPirates plugin;

	private static final String codename = "Frodo";
	public static Logger log = Logger.getLogger("Minecraft");

	@Override
	public void onLoad() {

	}
	
	public void sendMessage(Player p, Messages message) {
		String text = "";
		
		switch (message) {
		case NO_PERMISSION:
			text = ChatColor.RED + "You don't have permission to use that command.";
			break;
		case NO_BOATS_TO_REMOVE:
			text = ChatColor.GRAY + "There are no SkyPirates boats to remove.";
			break;
		case NOT_IN_BOAT:
			text = ChatColor.RED + "You are not in a boat.";
			break;
		case PLANE:
			text = ChatColor.GREEN + "The boat feels suddenly weightless, like a breath of wind would carry you away!";
			break;
		case PLANE_NO_PERMISSION:
			text = ChatColor.RED + "As much as you will it to float, the boat remains stubbornly on the ground.";
			break;
		case SUBMARINE:
			text = ChatColor.BLUE + "You feel the boat getting heavier and heavier as you sink beneath the waves.";
			break;
		case SUBMARINE_NO_PERMISSION:
			text = ChatColor.RED + "As hard as you try, the boat refuses to sink below the water.";
			break;
		case HOVER:
			text = ChatColor.GOLD + "The boat lifts into the air, hovering over the world below.";
			break;
		case HOVER_NO_PERMISSION:
			text = ChatColor.RED + "The boat retains its usual weight.";
			break;
		case GLIDER:
			text = ChatColor.WHITE + "The boat prepares to float gently downwards.";
			break;
		case GLIDER_NO_PERMISSION:
			text = ChatColor.RED + "The boat retains its usual weight.";
			break;
		case DRILL:
			text = ChatColor.DARK_GRAY + "The boat feels like it has immense force behind it, enough to drill through solid earth.";
			break;
		case DRILL_NO_PERMISSION:
			text = ChatColor.RED + "The boat retains its usual strength.";
			break;
		case NORMAL:
			text = ChatColor.GRAY + "The boat is just that, an ordinary vehicle.";
			break;
		case HELP:
			p.sendMessage(ChatColor.AQUA + "SkyPirates Modes List");
			p.sendMessage(ChatColor.YELLOW + "---------------------");
			p.sendMessage(ChatColor.GREEN + "plane|p - " + ChatColor.AQUA + "turns your boat into a plane.");
			p.sendMessage(ChatColor.GREEN + "submarine|sub|s - " + ChatColor.AQUA + "turns your boat into a submersible.");
			p.sendMessage(ChatColor.GREEN + "hoverboat|h - " + ChatColor.AQUA + "turns your boat into a hoverboat.");
			p.sendMessage(ChatColor.GREEN + "glider|g - " + ChatColor.AQUA + "turns your boat into a glider.");
			p.sendMessage(ChatColor.GREEN + "drill|d - " + ChatColor.AQUA + "turns your boat into a drill.");
			p.sendMessage(ChatColor.GREEN + "anything else - " + ChatColor.AQUA + "turns your boat back into the regular old jumping variety.");
			p.sendMessage(ChatColor.YELLOW + "---------------------");
			break;
		}
		
		p.sendMessage(text);
	}

	@Override
	public void onEnable() {
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvent(Event.Type.VEHICLE_COLLISION_BLOCK, vehicleListen, Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.VEHICLE_MOVE, vehicleListen, Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.VEHICLE_ENTER, vehicleListen, Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.VEHICLE_EXIT, vehicleListen, Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.VEHICLE_DAMAGE, vehicleListen, Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, playerListen, Priority.Normal, this);
		pluginManager.registerEvent(Event.Type.PLAYER_TOGGLE_SNEAK, playerListen, Priority.Normal, this);
		populateHelmets();

		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]: version [" + pdfFile.getVersion() + "] (" + codename + ") loaded");

	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]: version [" + pdfFile.getVersion() + "] (" + codename + ") disabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		
		if (args.length < 1) {
			return true;
		}
		
		String option = args[0];
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("[Skypirates]: Must be ingame to use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("skypirates.player.changemode")) {
			sendMessage(player, Messages.NO_PERMISSION);
			return true;
		}
		
		if (!commandName.equals("skypirates") && !commandName.equals("sky") && !commandName.equals("skypi")) {
			return true;
		}
		
		if (option.equals("clear") || option.equals("c")) {
			if (player.hasPermission("skypirates.admin.clear")) {
				if (SkyPirates.boats.isEmpty()) {
					sendMessage(player, Messages.NO_BOATS_TO_REMOVE);
					return true;
				}
				
				for (Entry<Integer, BoatHandler> entry : boats.entrySet()) {
					BoatHandler boatHandler = entry.getValue();
					
					if (boatHandler.boat.isEmpty()) {
						boatHandler.boat.getWorld().getEntities().remove(entry.getValue());
						boats.remove(entry.getKey());
					}
				}
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
				return true;
			}
			
			return true;
		} else if (option.equals("help")) {
			if (!player.hasPermission("skypirates.player.help")) {
				sendMessage(player, Messages.NO_PERMISSION);
				return true;
			}
			
			sendMessage(player, Messages.HELP);
			return true;
		}
		
		ArrayList<String> string = new ArrayList<String>();
		string.add("p");
		string.add("s");
		string.add("g");
		string.add("d");
		string.add("h");
		
		if (!string.contains("" + option.charAt(0))) {
			return false;
		}
		
		if (!player.isInsideVehicle()) {
			sendMessage(player, Messages.NOT_IN_BOAT);
			return true;
		}
		
		if (!(player.getVehicle() instanceof Boat) && !(PlayerListen.checkBoats((Boat) player.getVehicle()))) {
			sendMessage(player, Messages.NOT_IN_BOAT);
			return true;
		}
		
		BoatHandler boat = PlayerListen.getBoatHandler((Boat) player.getVehicle());
		
		if (option.equals("p") || option.equals("plane")) {
			if (player.hasPermission("skypirates.modes.plane")) {
				sendMessage(player, Messages.PLANE);
				playerModes.put(player, Modes.PLANE);
				boat.setMode(Modes.PLANE);
			} else {
				sendMessage(player, Messages.PLANE_NO_PERMISSION);
			}
		} else if (option.equals("s") || option.contains("sub")) {
			if (player.hasPermission("skypirates.modes.submarine")) {
				playerModes.put(player, Modes.SUBMARINE);
				sendMessage(player, Messages.SUBMARINE);
				boat.setMode(Modes.SUBMARINE);
			} else {
				sendMessage(player, Messages.SUBMARINE_NO_PERMISSION);
			}
		} else if (option.contains("hover") || option.equals("h")) {
			if (player.hasPermission("skypirates.modes.hoverboat")) {
				sendMessage(player, Messages.HOVER);
				SkyPirates.playerModes.put(player, Modes.HOVER);
				boat.setMode(Modes.HOVER);
			} else {
				sendMessage(player, Messages.HOVER_NO_PERMISSION);
			}
		} else if (option.contains("glider") || option.equals("g")) {
			if (player.hasPermission("skypirates.modes.glider")) {
				sendMessage(player, Messages.GLIDER);
				SkyPirates.playerModes.put(player, Modes.GLIDER);
				boat.setMode(Modes.GLIDER);
			} else {
				sendMessage(player, Messages.GLIDER_NO_PERMISSION);
			}
		} else if (option.contains("drill") || option.equals("d")) {
			if (player.hasPermission("skypirates.modes.drill")) {
				sendMessage(player, Messages.DRILL);
				SkyPirates.playerModes.put(player, Modes.DRILL);
				boat.setMode(Modes.DRILL);
			} else {
				sendMessage(player, Messages.DRILL_NO_PERMISSION);
			}
		} else {
			sendMessage(player, Messages.NORMAL);
			SkyPirates.playerModes.put(player, Modes.NORMAL);
			boat.setMode(Modes.NORMAL);
			boat.resetValues();
		}
		
		return false;
	}

	private void populateHelmets() {
		helmets.add("298");
		helmets.add("306");
		helmets.add("310");
		helmets.add("314");
	}
}