package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.boats.Boats;
import org.bukkit.entity.Player;

public class ClearCommandHandler extends CommandHandler {

    @Override
    public boolean isResponsible(String command, Player player) {
        return player.hasPermission("skypirates.admin.clear") && (command.equals("clear") || command.equals("c"));
    }

    @Override
    public void handle(Player player, Boats boats, MessageHandler messageHandler) {
        boats.clear();
    }
}
