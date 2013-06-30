package com.fullwall.SkyPirates.boats;

import com.fullwall.SkyPirates.BoatHandler;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class Boats {
    private HashMap<Integer, BoatHandler> boats = new HashMap<Integer, BoatHandler>();

    public void clear() {
        for (Map.Entry<Integer, BoatHandler> entry : boats.entrySet()) {
            BoatHandler boatHandler = entry.getValue();

            if (!boatHandler.hasPlayer()) {
                boatHandler.removeBoatFromWorld();
                boats.remove(entry.getKey());
            }
        }
    }

    public void handle(Entity entity, BoatHandler handler) {
        boats.put(entity.getEntityId(), handler);
    }

    public BoatHandler getHandler(Entity entity) {
        return boats.get(entity.getEntityId());
    }

    public boolean contains(Entity entity) {
        return boats.containsKey(entity.getEntityId());
}

    public void remove(Entity entity) {
        boats.remove(entity.getEntityId());
    }
}
