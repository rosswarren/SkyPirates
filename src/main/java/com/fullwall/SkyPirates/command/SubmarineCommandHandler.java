package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.Messages;
import com.fullwall.SkyPirates.boats.Boats;
import com.fullwall.SkyPirates.boats.Submarine;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SubmarineCommandHandler extends CommandHandler {

    @Override
    public boolean isResponsible(String command, Player player) {
        return player.hasPermission("skypirates.modes.submarine")
                && (command.equals("s") || command.contains("sub"))
                && playerInBoat(player);
    }

    @Override
    public void handle(Player player, Boats boats, MessageHandler messageHandler) {
        messageHandler.sendMessage(player, Messages.SUBMARINE);

        Entity entity = player.getVehicle();
        Submarine handler = new Submarine((Boat) entity);

        boats.handle(entity, handler);
    }
}
