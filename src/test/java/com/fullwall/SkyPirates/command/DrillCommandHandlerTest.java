package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.Messages;
import com.fullwall.SkyPirates.boats.Boats;
import com.fullwall.SkyPirates.boats.Drill;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class DrillCommandHandlerTest {
    @Mock private Player player;
    @Mock private Boats boats;
    @Mock private MessageHandler messageHandler;
    @Mock private Boat entity;

    private CommandHandler commandHandler;


    @Before
    public void setup() {
        commandHandler = new DrillCommandHandler(null);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsResponsibleWhenCommandIsCorrectAndPlayerHasPermission() {
        Mockito.when(player.hasPermission("skypirates.modes.drill")).thenReturn(true);

        commandHandler.isResponsible("drill", player);
    }

    @Test
    public void testCorrectMessageIsSent() {
        Mockito.when(player.getVehicle()).thenReturn(entity);

        commandHandler.handle(player, boats, messageHandler);

        Mockito.verify(messageHandler).sendMessage(player, Messages.DRILL);
    }

    @Test
    public void testCorrectBoatHandlerIsSetup() {
        Mockito.when(player.getVehicle()).thenReturn(entity);

        commandHandler.handle(player, boats, messageHandler);

        Mockito.verify(boats).handle(Mockito.eq(entity), Mockito.any(Drill.class));
    }
}
