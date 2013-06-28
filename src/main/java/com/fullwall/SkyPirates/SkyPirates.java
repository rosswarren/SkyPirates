package com.fullwall.SkyPirates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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

	public static Logger log = Logger.getLogger("Minecraft");

    private MessageHandler messageHandler;

	@Override
	public void onLoad() {

	}
	
	public Boolean getDestroyBoatsOnExit() {
		return this.destroyBoatsOnExit;
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

        messageHandler = new MessageHandler(getConfig());
	}

    public MessageHandler getMessageHandler() {
        return messageHandler;
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
			messageHandler.sendMessage(player, Messages.NO_PERMISSION);
			return true;
		}
		
		if (!commandName.equals("skypirates") && !commandName.equals("sky") && !commandName.equals("skypi")) {
			return true;
		}
		
		if (option.equals("clear") || option.equals("c")) {
			if (player.hasPermission("skypirates.admin.clear")) {
				if (this.boats.isEmpty()) {
                    messageHandler.sendMessage(player, Messages.NO_BOATS);
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
                messageHandler.sendMessage(player, Messages.NO_PERMISSION);
				return true;
			}
			
			return true;
		} else if (option.equals("help")) {
			if (!player.hasPermission("skypirates.player.help")) {
                messageHandler.sendMessage(player, Messages.NO_PERMISSION);
				return true;
			}

            messageHandler.sendMessage(player, Messages.HELP);
			return true;
		}
		
		
		if (!player.isInsideVehicle()) {
            messageHandler.sendMessage(player, Messages.NOT_IN_BOAT);
			return true;
		}
		
		if (!(player.getVehicle() instanceof Boat)) {
            messageHandler.sendMessage(player, Messages.NOT_IN_BOAT);
			return true;
		}
		
		int id = player.getVehicle().getEntityId();
		
		if (option.equals("p") || option.equals("plane")) {
			if (player.hasPermission("skypirates.modes.plane")) {
                messageHandler.sendMessage(player, Messages.PLANE);
				
				Plane handler = new Plane((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
                messageHandler.sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.equals("s") || option.contains("sub")) {
			if (player.hasPermission("skypirates.modes.submarine")) {
                messageHandler.sendMessage(player, Messages.SUBMARINE);
				
				Submarine handler = new Submarine((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
                messageHandler.sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("hover") || option.equals("h")) {
			if (player.hasPermission("skypirates.modes.hoverboat")) {
                messageHandler.sendMessage(player, Messages.HOVER);
				
				Hovercraft handler = new Hovercraft((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
                messageHandler.sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("glider") || option.equals("g")) {
			if (player.hasPermission("skypirates.modes.glider")) {
                messageHandler.sendMessage(player, Messages.GLIDER);
				
				Glider handler = new Glider((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
                messageHandler.sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("drill") || option.equals("d")) {
			if (player.hasPermission("skypirates.modes.drill")) {
                messageHandler.sendMessage(player, Messages.DRILL);
				
				Drill handler = new Drill((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
                messageHandler.sendMessage(player, Messages.NO_PERMISSION);
			}
		} else if (option.contains("ice") || option.equals("i")) {
			if (player.hasPermission("skypirates.modes.icebreaker")) {
                messageHandler.sendMessage(player, Messages.ICEBREAKER);
				
				Icebreaker handler = new Icebreaker((Boat) player.getVehicle());
				this.setBoat(id, handler);
			} else {
                messageHandler.sendMessage(player, Messages.NO_PERMISSION);
			}
		} else {
            messageHandler.sendMessage(player, Messages.NORMAL);
			
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

    private void loadConfiguration() {
        MemorySection optionsSection = (MemorySection) getConfig().get("options");

        String answer = optionsSection.getString("destroy-boat-on-exit");

        this.destroyBoatsOnExit = answer.contains("t");
    }
}