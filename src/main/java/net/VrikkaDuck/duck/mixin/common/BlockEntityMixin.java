package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Duration;
import java.time.Instant;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {
    @Unique
    Instant lastTime = Instant.now();
    @Inject(method = "markDirty()V", at = @At("RETURN"))
    private void duck$markDirty(CallbackInfo ci){
        // Instead of actually doing it the smart way. im just gonna add a cooldown :)

        if(Duration.between(lastTime, Instant.now()).toMillis() > 10){
            lastTime = Instant.now();
            BlockEntity be = ((BlockEntity) (Object)this);
            NetworkHandler.Server.SendBlockEntityToNearby(be.getWorld(), be.getPos());
        }
    }
}
