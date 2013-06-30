package com.fullwall.SkyPirates;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.logging.Logger;

public class ConfigurationManager {
    private Logger logger;
    private String version;
    private String name;

    public ConfigurationManager(Logger logger) {
        this.logger = logger;
    }

    public void init(Plugin plugin) {
        copyConfigToUserDirectory(plugin);

        // reload the config in memory
        plugin.reloadConfig();

        PluginDescriptionFile pdfFile = plugin.getDescription();

        this.name = pdfFile.getName();
        this.version = pdfFile.getVersion();

        logger.info("[" +name + "]: version [" + version + "] loaded");
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    private void copyConfigToUserDirectory(Plugin plugin) {
        FileConfiguration config = plugin.getConfig();

        // In certain cases if you wish to append new defaults to an existing config.yml you can set the option copyDefaults to true
        config.options().copyDefaults(true);

        plugin.saveDefaultConfig();
    }
}
