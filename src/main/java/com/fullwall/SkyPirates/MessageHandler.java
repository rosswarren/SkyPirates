package com.fullwall.SkyPirates;

import org.bukkit.ChatColor;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

public class MessageHandler {
    public HashMap<String, String> strings;

    public MessageHandler(FileConfiguration config) {
        strings = new HashMap<String, String>();

        MemorySection stringSection = (MemorySection) config.get("strings");
        Set<String> keys = stringSection.getKeys(false);

        for (String key: keys) {
            strings.put(key, stringSection.getString(key));
        }
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
}
