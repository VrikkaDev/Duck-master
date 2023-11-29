package net.VrikkaDuck.duck.mixin.client;

import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.common.ServerConfigs;
import net.VrikkaDuck.duck.networking.PacketsS2C;
import net.VrikkaDuck.duck.networking.packet.HandshakePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientConnectionHandler {
    @Inject(at = @At("RETURN"), method = "onGameJoin")
    private void onJoin(CallbackInfo ci){

        PacketsS2C.register();

        ServerConfigs.loadFromFile();
        ServerConfigs.refreshFromServer();

        HandshakePacket.HandshakeC2SPacket packet = new HandshakePacket.HandshakeC2SPacket(MinecraftClient.getInstance().player.getUuid(), Variables.MODVERSION);
        ClientPlayNetworking.send(packet);
    }
}
