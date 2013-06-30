package com.fullwall.SkyPirates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.fullwall.SkyPirates.command.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import com.fullwall.SkyPirates.boats.*;

public class SkyPirates extends JavaPlugin {
    private Boats boats;

	public static Logger log = Logger.getLogger("Minecraft");

    private MessageHandler messageHandler;

    private CommandHandler commandHandler;

    private ConfigurationManager configurationManager;

    public SkyPirates() {
        List<CommandHandler> commandHandlers = new ArrayList<CommandHandler>(Arrays.asList(
            new ClearCommandHandler(),
            new HelpCommandHandler(),
            new PlaneCommandHandler(),
            new SubmarineCommandHandler(),
            new HovercraftCommandHandler(),
            new GliderCommandHandler(),
            new DrillCommandHandler(),
            new IcebreakerCommandHandler(),
            new DefaultCommandHandler(),
            new NonMatchedCommandHandler()
        ));

        chainCommandHandlers(commandHandlers);

        boats = new Boats();
        configurationManager = new ConfigurationManager(log);
    }

    @Override
	public void onLoad() {

	}

	@Override
	public void onEnable() {
		PluginManager pluginManager = getServer().getPluginManager();
		
		// register listeners so that they can handle events
		pluginManager.registerEvents(new EventListener(boats, messageHandler), this);

        configurationManager.init(this);

        messageHandler = new MessageHandler(getConfig());
	}

	@Override
	public void onDisable() {
		log.info("[" + configurationManager.getName() + "]: version [" + configurationManager.getVersion() + "] disabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		
		if (args.length < 1) {
			return true;
		}
		
		String option = args[0];
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("[Skypirates]: Must be ingame to use this command.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("skypirates.player.changemode")) {
			messageHandler.sendMessage(player, Messages.NO_PERMISSION);
			return true;
		}
		
		if (!commandName.equals("skypirates") && !commandName.equals("sky") && !commandName.equals("skypi")) {
			return true;
		}

        commandHandler.run(option, player, boats, messageHandler);

        return true;
	}

    private void chainCommandHandlers(List<CommandHandler> commandHandlers) {
        commandHandler = commandHandlers.get(0);

        CommandHandler lastLink = commandHandler;

        for (int i = 1; i < commandHandlers.size(); i++) {
            lastLink.setNext(commandHandlers.get(i));
            lastLink = commandHandlers.get(i);
        }
    }
}