package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.Messages;
import com.fullwall.SkyPirates.boats.Boats;
import com.fullwall.SkyPirates.boats.Icebreaker;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class IcebreakerCommandHandler extends CommandHandler {
    public IcebreakerCommandHandler(CommandHandler next) {
        super(next);
    }

    @Override
    public boolean isResponsible(String command, Player player) {
        return (command.contains("ice") || command.equals("i"))
                && player.hasPermission("skypirates.modes.icebreaker")
                && playerInBoat(player);
    }

    @Override
    public void handle(Player player, Boats boats, MessageHandler messageHandler) {
        messageHandler.sendMessage(player, Messages.ICEBREAKER);

        Entity entity = player.getVehicle();
        Icebreaker handler = new Icebreaker((Boat) entity);

        boats.handle(entity, handler);
    }
}
