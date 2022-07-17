package net.VrikkaDuck.duck;

import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;

public class Main implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        //TODO: make it so admin tweaks are not universal but saved in world
    }
}
