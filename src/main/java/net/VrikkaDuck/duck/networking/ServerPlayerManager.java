package net.VrikkaDuck.duck.networking;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ServerPlayerManager {

    /*
     * Entries= {
     * "duckVersion": String; players duck version
     * "playerLastBlockpos": BlockPos; is players last blockposition sent to the server. useful to send data to client in freecam
     * }
     */
    public static final Map<UUID, Map<String, Object>> playerProperties = new HashMap<>();

    public static void putProperty(UUID uuid, String key, Object value){
        if(!playerProperties.containsKey(uuid)){
            playerProperties.put(uuid, new HashMap<>());
        }

        playerProperties.get(uuid).put(key, value);
    }

    public static Optional<Object> getProperty(UUID uuid, String key){
        if(!playerProperties.containsKey(uuid)){
            return Optional.empty();
        }

        Map<String, Object> _pm = playerProperties.get(uuid);

        Object r = _pm.getOrDefault(key, null);
        return r == null ? Optional.empty() : Optional.of(r);
    }
}
