package net.VrikkaDuck.duck;

import net.VrikkaDuck.duck.networking.PacketsS2C;
import net.fabricmc.api.ClientModInitializer;
import fi.dy.masa.malilib.event.InitializationHandler;

public class Main implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }
}
