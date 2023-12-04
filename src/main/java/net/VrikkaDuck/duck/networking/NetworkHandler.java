package net.VrikkaDuck.duck.networking;

import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.debug.DebugPrinter;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class NetworkHandler {
    public static void SendToServer(FabricPacket packet){

        DebugPrinter.DebugPrint(packet, Configs.Debug.PRINT_PACKETS_C2S.getBooleanValue());

        ClientPlayNetworking.send(packet);
    }
    public static void SendToClient(ServerPlayerEntity player, FabricPacket packet){
        ServerPlayNetworking.send(player, packet);
    }
}
