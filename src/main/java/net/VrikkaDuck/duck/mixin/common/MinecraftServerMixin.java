package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.config.common.ServerConfigs;
import net.VrikkaDuck.duck.handler.common.TickHandler;
import net.VrikkaDuck.duck.networking.PacketsC2S;
import net.VrikkaDuck.duck.world.common.GameWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow public abstract ServerWorld getOverworld();

    @Inject(at = @At("HEAD"), method = "runServer")
    private void duck$runServer(CallbackInfo ci){

        PacketsC2S.register();


        GameWorld.setWorld(this.getOverworld());
        GameWorld.setServer(((MinecraftServer)(Object)this));

        ServerConfigs.loadFromFile();
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void duck$tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci){
        TickHandler.INSTANCE().Tick();
    }
}
