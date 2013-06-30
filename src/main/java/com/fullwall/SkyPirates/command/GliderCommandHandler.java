package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.Messages;
import com.fullwall.SkyPirates.boats.Boats;
import com.fullwall.SkyPirates.boats.Glider;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GliderCommandHandler extends CommandHandler {

    @Override
    public boolean isResponsible(String command, Player player) {
        return (command.contains("glider") || command.equals("g"))
            && player.hasPermission("skypirates.modes.glider")
            && playerInBoat(player);
    }

    @Override
    public void handle(Player player, Boats boats, MessageHandler messageHandler) {
        messageHandler.sendMessage(player, Messages.GLIDER);

        Entity entity = player.getVehicle();
        Glider handler = new Glider((Boat) entity);

        boats.handle(entity, handler);
    }
}
