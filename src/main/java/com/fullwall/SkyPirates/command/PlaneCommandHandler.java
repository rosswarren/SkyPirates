package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.Messages;
import com.fullwall.SkyPirates.boats.Boats;
import com.fullwall.SkyPirates.boats.Plane;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

public class PlaneCommandHandler extends CommandHandler {
    public PlaneCommandHandler(CommandHandler next) {
        super(next);
    }

    @Override
    public boolean isResponsible(String command, Player player) {
        return (command.equals("p") || command.equals("plane"))
                && player.hasPermission("skypirates.modes.plane")
                && playerInBoat(player);
    }

    @Override
    public void handle(Player player, Boats boats, MessageHandler messageHandler) {
        messageHandler.sendMessage(player, Messages.PLANE);

        Entity entity = player.getVehicle();

        Plane handler = new Plane((Boat) entity);

        boats.handle(entity, handler);
    }
}
