package net.VrikkaDuck.duck.utils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class GameWorld {

    private static MinecraftServer server;
    private static World world;

    public static MinecraftServer getServer() {
        return server;
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
}
