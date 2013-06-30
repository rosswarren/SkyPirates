package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.Messages;
import com.fullwall.SkyPirates.boats.Boats;
import com.fullwall.SkyPirates.boats.Hovercraft;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class HovercraftCommandHandler extends CommandHandler {

    @Override
    public boolean isResponsible(String command, Player player) {
        return (command.contains("hover") || command.equals("h"))
                && player.hasPermission("skypirates.modes.hoverboat")
                && playerInBoat(player);
    }

    @Override
    public void handle(Player player, Boats boats, MessageHandler messageHandler) {
        messageHandler.sendMessage(player, Messages.HOVER);

        Entity entity = player.getVehicle();
        Hovercraft handler = new Hovercraft((Boat) entity);

        boats.handle(entity, handler);
    }
}
