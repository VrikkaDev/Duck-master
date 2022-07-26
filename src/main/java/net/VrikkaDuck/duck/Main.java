package net.VrikkaDuck.duck;

import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;

public class Main implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        //make it so admins can set permission required to use feature
    }
}
