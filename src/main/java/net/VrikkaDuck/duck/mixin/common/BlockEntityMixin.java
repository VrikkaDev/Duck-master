package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.debug.DebugPrinter;
import net.VrikkaDuck.duck.handler.common.TickHandler;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Duration;
import java.time.Instant;

@Mixin(BlockEntity.class)
public class BlockEntityMixin {
    /*@Unique
    Instant lastTime = Instant.now();*/
    @Inject(method = "markDirty()V", at = @At("RETURN"))
    private void duck$markDirty(CallbackInfo ci){
        // Instead of actually doing it the smart way. im just gonna add a cooldown :)

        /*if(Duration.between(lastTime, Instant.now()).toMillis() > 10){
            lastTime = Instant.now();

        }*/

        // TODO. could be optimized when a lot of changes. like main storage or somethign

        BlockEntity be = ((BlockEntity) (Object)this);

        NetworkHandler.Server.SendBlockEntityToNearby(be.getWorld(), be.getPos());
    }

    @Inject(method = "markDirty(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", at = @At("RETURN"))
    private static void duck$markDirty2(World world, BlockPos pos, BlockState state, CallbackInfo ci){
        NetworkHandler.Server.SendBlockEntityToNearby(world, pos);
    }

    @Inject(method = "markRemoved", at = @At("RETURN"))
    private void duck$markRemoved(CallbackInfo ci){
        if(ContainerType.fromBlockEntity((BlockEntity) (Object)this) != ContainerType.NONE){
            BlockEntity be = ((BlockEntity) (Object)this);

            TickHandler.INSTANCE().AddTickDelay(() -> NetworkHandler.Server.SendBlockEntityToNearby(be.getWorld(), be.getPos()), 1);
        }
    }
}
