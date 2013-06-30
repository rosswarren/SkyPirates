package com.fullwall.SkyPirates.command;

import com.fullwall.SkyPirates.MessageHandler;
import com.fullwall.SkyPirates.boats.Boats;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ClearCommandHandlerTest {
    @Mock private Player player;
    @Mock private Boats boats;
    @Mock private MessageHandler messageHandler;

    private CommandHandler commandHandler;


    @Before
    public void setup() {
        commandHandler = new ClearCommandHandler(null);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsResponsibleWhenHasPermission() throws Exception {
        when(player.hasPermission("skypirates.admin.clear")).thenReturn(true);

        assertTrue(commandHandler.isResponsible("clear", player));
    }

    @Test
    public void testIsNotResponsibleWhenNotHasPermission() throws Exception {
        when(player.hasPermission("skypirates.admin.clear")).thenReturn(false);

        assertFalse(commandHandler.isResponsible("clear", player));
    }

    @Test
    public void testIsResponsibleWhenHasCorrectCommand() throws Exception {
        when(player.hasPermission("skypirates.admin.clear")).thenReturn(true);

        assertTrue(commandHandler.isResponsible("clear", player));
    }

    @Test
    public void testIsNotResponsibleWhenHasWrongCommand() throws Exception {
        when(player.hasPermission("skypirates.admin.clear")).thenReturn(true);

        assertFalse(commandHandler.isResponsible("blah", player));
    }

    @Test
    public void testHandle() throws Exception {
        commandHandler.handle(player, boats, messageHandler);

        verify(boats).clear();
    }
}
