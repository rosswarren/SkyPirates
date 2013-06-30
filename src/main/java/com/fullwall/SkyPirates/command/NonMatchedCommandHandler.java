package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.Messages;
import com.fullwall.SkyPirates.boats.Boats;
import org.bukkit.entity.Player;

public class NonMatchedCommandHandler extends CommandHandler {

    @Override
    public boolean isResponsible(String command, Player player) {
        return true;
    }

    @Override
    public void handle(Player player, Boats boats, MessageHandler messageHandler) {
        messageHandler.sendMessage(player, Messages.COMMAND_NOT_AVAILABLE);
    }
}
