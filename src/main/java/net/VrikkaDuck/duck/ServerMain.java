package net.VrikkaDuck.duck;

import net.VrikkaDuck.duck.config.ServerConfigs;
import net.fabricmc.api.ModInitializer;

public class ServerMain implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerConfigs.loadFromFile();
    }
}
