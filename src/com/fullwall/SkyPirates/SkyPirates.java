package com.fullwall.SkyPirates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;

import com.fullwall.SkyPirates.boats.*;

public class SkyPirates extends JavaPlugin {
	private HashMap<Integer, BoatHandler> boats = new HashMap<Integer, BoatHandler>();
	private Boolean destroyBoatsOnExit;
	
	public HashMap<String, String> strings;
	
	public enum Messages {
		NO_PERMISSION,
		NO_BOATS,
		NOT_IN_BOAT,
		PLANE,
		SUBMARINE,
		HOVER,
		GLIDER,
		DRILL,
		ICEBREAKER,
		NORMAL,
		HELP,
		STOP,
		ENTER,
		EXIT
	}

	public static Logger log = Logger.getLogger("Minecraft");

	@Override
	public void onLoad() {

	}
	
	public Boolean getDestroyBoatsOnExit() {
		return this.destroyBoatsOnExit;
	}
	
	public void sendMessage(Player p, Messages message) {
		String text = "";
		
		switch (message) {
		case NO_PERMISSION:
			text = ChatColor.RED + strings.get("no-permission");
			break;
		case NO_BOATS:
			text = ChatColor.GRAY + strings.get("no-boats");
			break;
		case NOT_IN_BOAT:
			text = ChatColor.RED + strings.get("not-in-boat");
			break;
		case PLANE:
			text = ChatColor.GREEN + strings.get("plane");
			break;
		case SUBMARINE:
			text = ChatColor.BLUE + strings.get("submarine");
			break;
		case HOVER:
			text = ChatColor.GOLD + strings.get("hover");
			break;
		case GLIDER:
			text = ChatColor.GOLD + strings.get("glider");
			break;
		case DRILL:
			text = ChatColor.DARK_GRAY + strings.get("drill");
			break;
		case ICEBREAKER:
			text = ChatColor.DARK_GRAY + strings.get("icebreaker");
			break;
		case NORMAL:
			text = ChatColor.GRAY +  strings.get("normal");
			break;
		case STOP:
			text = ChatColor.DARK_RED + strings.get("stop");
			break;
		case ENTER:
			text = ChatColor.AQUA + strings.get("enter");
			break;
		case EXIT:
			text = ChatColor.LIGHT_PURPLE + strings.get("exit");
			break;
		case HELP:
			p.sendMessage(ChatColor.AQUA + "SkyPirates Modes List");
			p.sendMessage(ChatColor.YELLOW + "---------------------");
			p.sendMessage(ChatColor.GREEN + "plane|p - " + ChatColor.AQUA + "turns your boat into a plane.");
			p.sendMessage(ChatColor.GREEN + "submarine|sub|s - " + ChatColor.AQUA + "turns your boat into a submersible.");
			p.sendMessage(ChatColor.GREEN + "hoverboat|h - " + ChatColor.AQUA + "turns your boat into a hoverboat.");
			p.sendMessage(ChatColor.GREEN + "glider|g - " + ChatColor.AQUA + "turns your boat into a glider.");
			p.sendMessage(ChatColor.GREEN + "drill|d - " + ChatColor.AQUA + "turns your boat into a drill.");
			p.sendMessage(ChatColor.GREEN + "icebreaker|ice|i - " + ChatColor.AQUA + "turns your boat into an icebreaker.");
			p.sendMessage(ChatColor.GREEN + "anything else - " + ChatColor.AQUA + "turns your boat back into the regular old jumping variety.");
			p.sendMessage(ChatColor.YELLOW + "---------------------");
			p.sendMessage(ChatColor.AQUA + "If you are stuck, contact rosswarren4@gmail.com for help.");
			break;
		}
		
		p.sendMessage(text);
	}

	@Override
	public void onEnable() {
		PluginManager pluginManager = getServer().getPluginManager();
		
		// register listeners so that they can handle events
		pluginManager.registerEvents(new EventListener(this), this);

		PluginDescriptionFile pdfFile = this.getDescription();
		
		// In certain cases if you wish to append new defaults to an existing config.yml you can set the option copyDefaults to true
		this.getConfig().options().copyDefaults(true);
		
		// Should copy config.yml to the users files so that they can config things
		// http://wiki.bukkit.org/Introduction_to_the_New_Configuration
		this.saveDefaultConfig();
		
		// reload the config in memory
		this.reloadConfig();
		
		loadConfiguration();
		
		
		log.info("[" + pdfFile.getName() + "]: version [" + pdfFile.getVersion() + "] loaded");

	}
	
	/**
	 * Load the strings from the configuration file, so that they can be customised
	 */
	private void loadConfiguration() {
		this.strings = new HashMap<String, String>();

		MemorySection stringSection = (MemorySection) this.getConfig().get("strings");
		Set<String> keys = stringSection.getKeys(false);

		for (String key: keys) {
			this.strings.put(key, stringSection.getString(key));
		}

		MemorySection optionsSection = (MemorySection) this.getConfig().get("options");
		
		String answer = optionsSection.getString("destroy-boat-on-exit");

        this.destroyBoatsOnExit = answer.contains("t");
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "]: version [" + pdfFile.getVersion() + "] disabled");
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
				if (this.boats.isEmpty()) {
					sendMessage(player, Messages.NO_BOATS);
					return true;
				}

				for (Map.Entry<Integer, BoatHandler> entry : boats.entrySet()) {
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
		
		
		if (!player.isInsideVehicle()) {
			sendMessage(player, Messages.NOT_IN_BOAT);
			return true;
		}
		
		if (!(player.getVehicle() instanceof Boat)) {
			sendMessage(player, Messages.NOT_IN_BOAT);
			return true;
		}
		
		int id = player.getVehicle().getEntityId();
		
		if (option.equals("p") || option.equals("plane")) {
			if (player.hasPermission("skypirates.modes.plane")) {
				sendMessage(player, Messages.PLANE);
				
				Plane handler = new Plane((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.equals("s") || option.contains("sub")) {
			if (player.hasPermission("skypirates.modes.submarine")) {
				sendMessage(player, Messages.SUBMARINE);
				
				Submarine handler = new Submarine((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("hover") || option.equals("h")) {
			if (player.hasPermission("skypirates.modes.hoverboat")) {
				sendMessage(player, Messages.HOVER);
				
				Hovercraft handler = new Hovercraft((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("glider") || option.equals("g")) {
			if (player.hasPermission("skypirates.modes.glider")) {
				sendMessage(player, Messages.GLIDER);
				
				Glider handler = new Glider((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("drill") || option.equals("d")) {
			if (player.hasPermission("skypirates.modes.drill")) {
				sendMessage(player, Messages.DRILL);
				
				Drill handler = new Drill((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("ice") || option.equals("i")) {
			if (player.hasPermission("skypirates.modes.icebreaker")) {
				sendMessage(player, Messages.ICEBREAKER);
				
				Icebreaker handler = new Icebreaker((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else {
			sendMessage(player, Messages.NORMAL);
			
			Normal handler = new Normal((Boat) player.getVehicle());
			this.setBoat(id, handler);
		}
		
		return false;
	}
	
	public BoatHandler getBoatHandler(int id) {
		return boats.get(id);
	}
	
	public void setBoat(int id, BoatHandler handler) {
		boats.put(id, handler);
	}
	
	public void removeBoatHandler(int id) {
		boats.remove(id);
	}
}