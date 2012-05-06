package com.fullwall.SkyPirates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;

import com.fullwall.SkyPirates.BoatHandler.Modes;

/**
 * SkyPirates for Bukkit
 */
public class SkyPirates extends JavaPlugin {
	public static HashMap<Integer, BoatHandler> boats = new HashMap<Integer, BoatHandler>();
	public static ArrayList<String> helmets = new ArrayList<String>();
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
	
	public enum Commands {
		CLEAR,
		HELP,
		NORMAL,
		PLANE,
		HOVER,
		GLIDER,
		DRILL,
		ICEBREAKER
	}

	public static SkyPirates plugin;
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

	// runs when the plugin is loaded
	@Override
	public void onEnable() {
		PluginManager pluginManager = getServer().getPluginManager();
		
		// register listeners so that they can handle events
		pluginManager.registerEvents((Listener) new VehicleListen(this), this);
		pluginManager.registerEvents((Listener) new PlayerListen(this), this);

		populateHelmets();

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
		
		// get the strings section
		MemorySection stringSection = (MemorySection) this.getConfig().get("strings");
		
		// get all the keys, (the string names)
		Set<String> keys = stringSection.getKeys(false);
		
		// load all the strings to the hashmap from the configuration yaml file
		for(String key: keys) {
			this.strings.put(key, stringSection.getString(key));
		}
		
		
		MemorySection optionsSection = (MemorySection) this.getConfig().get("options");
		
		String answer = optionsSection.getString("destroy-boat-on-exit");
		
		if (answer.contains("t")) {
			this.destroyBoatsOnExit = true;
		} else {
			this.destroyBoatsOnExit = false;
		}
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
				if (SkyPirates.boats.isEmpty()) {
					sendMessage(player, Messages.NO_BOATS);
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
				boat.setMode(Modes.PLANE);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.equals("s") || option.contains("sub")) {
			if (player.hasPermission("skypirates.modes.submarine")) {
				sendMessage(player, Messages.SUBMARINE);
				boat.setMode(Modes.SUBMARINE);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("hover") || option.equals("h")) {
			if (player.hasPermission("skypirates.modes.hoverboat")) {
				sendMessage(player, Messages.HOVER);
				boat.setMode(Modes.HOVER);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("glider") || option.equals("g")) {
			if (player.hasPermission("skypirates.modes.glider")) {
				sendMessage(player, Messages.GLIDER);
				boat.setMode(Modes.GLIDER);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("drill") || option.equals("d")) {
			if (player.hasPermission("skypirates.modes.drill")) {
				sendMessage(player, Messages.DRILL);
				boat.setMode(Modes.DRILL);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("ice") || option.equals("i")) {
			if (player.hasPermission("skypirates.modes.icebreaker")) {
				sendMessage(player, Messages.ICEBREAKER);
				boat.setMode(Modes.ICEBREAKER);
			} else {
				sendMessage(player, Messages.NO_PERMISSION);
			}
		} else {
			sendMessage(player, Messages.NORMAL);
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