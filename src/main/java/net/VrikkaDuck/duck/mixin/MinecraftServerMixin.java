package net.VrikkaDuck.duck.mixin;

import net.VrikkaDuck.duck.config.ServerConfigs;
import net.VrikkaDuck.duck.util.GameWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow public abstract ServerWorld getOverworld();

    @Inject(at = @At("RETURN"), method = "<init>")
    private void init(CallbackInfo ci){
        GameWorld.setWorld(this.getOverworld());
        GameWorld.setServer(((MinecraftServer)(Object)this));

        ServerConfigs.loadFromFile();
    }
}
