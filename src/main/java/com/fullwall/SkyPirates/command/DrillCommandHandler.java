package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.Messages;
import com.fullwall.SkyPirates.boats.Boats;
import com.fullwall.SkyPirates.boats.Drill;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class DrillCommandHandler extends CommandHandler {
    @Override
    public boolean isResponsible(String command, Player player) {
        return (command.contains("drill") || command.equals("d"))
                && player.hasPermission("skypirates.modes.drill")
                && playerInBoat(player);
    }

    @Override
    public void handle(Player player, Boats boats, MessageHandler messageHandler) {
        messageHandler.sendMessage(player, Messages.DRILL);

        Entity entity = player.getVehicle();
        Drill handler = new Drill((Boat) entity);

        boats.handle(entity, handler);
    }
}
