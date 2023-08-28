package net.VrikkaDuck.duck;

import fi.dy.masa.malilib.event.InitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashReport;

public class Main implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        // Throw error if client doesn't have Malilib

        try{
            if(!FabricLoader.getInstance().isModLoaded("malilib")){
                throw new Throwable();
            }
        } catch (Throwable t) {
            CrashReport cr = CrashReport.create(t, "Failed to load mod MALILIB!, Please install malilib to use this mod.");
            MinecraftClient.getInstance().setCrashReportSupplier(cr);
        }

        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }
}
