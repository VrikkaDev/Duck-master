package net.VrikkaDuck.duck.mixin.client;

import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.common.ServerConfigs;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.PacketsS2C;
import net.VrikkaDuck.duck.networking.packet.HandshakePacket;
import net.VrikkaDuck.duck.world.common.GameWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientConnectionHandler {
    @Inject(at = @At("RETURN"), method = "onGameJoin")
    private void onJoin(CallbackInfo ci){

        PacketsS2C.register();

        GameWorld.setServer(MinecraftClient.getInstance().getServer());

        ServerConfigs.loadFromFile();
        ServerConfigs.refreshFromServer();

        PlayerEntity player = MinecraftClient.getInstance().player;

        HandshakePacket.HandshakeC2SPacket packet = new HandshakePacket.HandshakeC2SPacket(player.getUuid(), Variables.MODVERSION);
        NetworkHandler.Client.SendToServer(packet);

        if(Variables.DEBUG){
            player.sendMessage(Text.of("§eWARNING you are using debug mode of the duck mod"));
        }
    }
}
