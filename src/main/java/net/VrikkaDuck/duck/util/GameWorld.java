package net.VrikkaDuck.duck.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;

import java.io.File;

public class GameWorld {

    private static MinecraftServer server;
    private static World world;
    private static MinecraftClient mc;
    private static boolean hasClient = false;

    public static MinecraftServer getServer() {
        return server;
    }

    public static MinecraftClient getClient(){
        if(getServer().getOverworld().isClient){
            if(mc == null){
                mc = MinecraftClient.getInstance();
            }
            return mc;
        }else{
            return null;
        }
    }

    public static void setServer(MinecraftServer server) {
        GameWorld.server = server;
    }

    public static World getWorld() {
        return world;
    }

    public static void setWorld(World world) {
        GameWorld.world = world;
    }

    public static boolean isSingleplayer(){
        return server.isSingleplayer();
    }

    public static File getDataFolder(){
        return GameWorld.getServer().getSavePath(WorldSavePath.ROOT).resolve("data").toFile();
    }
    public static boolean hasPermissionLevel(int level, MinecraftClient player){
        return player.player.hasPermissionLevel(level);
    }
    public static boolean hasClient(){

        if(!hasClient){
            if(MinecraftClient.getInstance() == null){
                return false;
            }
            hasClient = true;
        }

        return true;
    }
}
