package net.VrikkaDuck.duck;

import com.google.errorprone.annotations.Var;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.network.ClientPacketChannelHandler;
import fi.dy.masa.malilib.network.IPluginChannelHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.PacketCallbackListener;
import net.fabricmc.fabric.impl.screenhandler.Networking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPacketListener;
import net.minecraft.network.listener.ServerQueryPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

import java.util.List;

public class Main implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
        //TODO: Inspect playerInventory
    }
}
