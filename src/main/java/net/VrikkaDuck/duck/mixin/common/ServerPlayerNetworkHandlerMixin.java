package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.networking.PacketsC2S;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayerNetworkHandlerMixin {

    @Shadow public abstract ServerPlayerEntity getPlayer();

    @Inject(method = "onDisconnected", at = @At("RETURN"))
    private void duck$onDisconnected(CallbackInfo c){
        PacketsC2S.playerProperties.remove(getPlayer().getUuid());
    }
}
