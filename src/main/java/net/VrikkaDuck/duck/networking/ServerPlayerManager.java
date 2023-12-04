package net.VrikkaDuck.duck.networking;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ServerPlayerManager {

    public ServerPlayerManager(){
    }

    private static ServerPlayerManager instance = null;

    public static ServerPlayerManager INSTANCE(){
        if(instance == null){
            instance = new ServerPlayerManager();
        }

        return instance;
    }


    /*
     * Entries= {
     * "containerWorld": ContainerWorld; players containerworld
     * "duckVersion": String; players duck version
     * "playerLastBlockpos": BlockPos; is players last blockposition sent to the server. useful to send data to client in freecam
     * }
     */
    public final Map<UUID, Map<String, Object>> playerProperties = new HashMap<>();

    public void putProperty(UUID uuid, String key, Object value){
        if(!playerProperties.containsKey(uuid)){
            playerProperties.put(uuid, new HashMap<>());
        }

        playerProperties.get(uuid).put(key, value);
    }

    public Optional<Object> getProperty(UUID uuid, String key){
        if(!playerProperties.containsKey(uuid)){
            return Optional.empty();
        }

        Map<String, Object> _pm = playerProperties.get(uuid);

        Object r = _pm.getOrDefault(key, null);
        return r == null ? Optional.empty() : Optional.of(r);
    }
}
