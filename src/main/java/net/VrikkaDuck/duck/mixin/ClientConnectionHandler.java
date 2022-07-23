package net.VrikkaDuck.duck.mixin;

import net.VrikkaDuck.duck.config.ServerConfigs;
import net.VrikkaDuck.duck.event.ClientNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientConnectionHandler {
    @Inject(at = @At("RETURN"), method = "onGameJoin")
    private void onJoin(CallbackInfo ci){
        ServerConfigs.loadFromFile();
        ClientNetworkHandler.refreshAdmin();
    }
}
