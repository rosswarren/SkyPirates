package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.boats.Boats;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;

public abstract class CommandHandler {
    private CommandHandler next;

    public CommandHandler(CommandHandler next) {
        this.next = next;
    }

    public abstract boolean isResponsible(String command, Player player);

    public abstract void handle(Player player, Boats boats, MessageHandler messageHandler);

    public void run(String command, Player player, Boats boats, MessageHandler messageHandler) {
        if (isResponsible(command, player)) {
            handle(player, boats, messageHandler);
        } else {
            next.run(command, player, boats, messageHandler);
        }
    }

    protected boolean playerInBoat(Player player) {
        return player.isInsideVehicle() && player.getVehicle() instanceof Boat;
    }
}
