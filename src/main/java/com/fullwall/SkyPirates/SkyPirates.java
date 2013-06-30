package com.fullwall.SkyPirates;

import java.util.logging.Logger;

import com.fullwall.SkyPirates.command.*;
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
    private Boats boats;

	public static Logger log = Logger.getLogger("Minecraft");

    private MessageHandler messageHandler;

    private CommandHandler commandHandler;

    public SkyPirates() {
        commandHandler = new ClearCommandHandler(
                new HelpCommandHandler(
                        new PlaneCommandHandler(
                                new SubmarineCommandHandler(
                                        new HovercraftCommandHandler(
                                                new GliderCommandHandler(
                                                        new DrillCommandHandler(
                                                                new IcebreakerCommandHandler(
                                                                        new DefaultCommandHandler(
                                                                                new NonMatchedCommandHandler(null))))))))));
    }

	@Override
	public void onLoad() {

	}

	@Override
	public void onEnable() {
		PluginManager pluginManager = getServer().getPluginManager();
		
		// register listeners so that they can handle events
		pluginManager.registerEvents(new EventListener(boats, messageHandler), this);

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

        commandHandler.run(option, player, boats, messageHandler);

        return true;
	}

    private void loadConfiguration() {
        MemorySection optionsSection = (MemorySection) getConfig().get("options");
    }
}