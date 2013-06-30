package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.Messages;
import com.fullwall.SkyPirates.boats.Boats;
import com.fullwall.SkyPirates.boats.Normal;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class DefaultCommandHandler extends CommandHandler {
    @Override
    public boolean isResponsible(String command, Player player) {
        return playerInBoat(player);
    }

    @Override
    public void handle(Player player, Boats boats, MessageHandler messageHandler) {
        messageHandler.sendMessage(player, Messages.NORMAL);

        Entity entity = player.getVehicle();
        Normal handler = new Normal((Boat) entity);

        boats.handle(entity, handler);
    }
}
