package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.networking.PacketsC2S;
import net.VrikkaDuck.duck.networking.ServerPlayerManager;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
    @Inject(method = "setStack*", at = @At("RETURN"))
    private void duck$setStack(int slot, ItemStack stack, CallbackInfo ci){

        LootableContainerBlockEntity self = ((LootableContainerBlockEntity)(Object)this);
        BlockPos pos = self.getPos();

        if(!self.hasWorld()){
            return;
        }

        Stream<? extends PlayerEntity> players = self.getWorld().getPlayers().stream().filter(p -> {
            Optional<Object> _fp = ServerPlayerManager.getProperty(p.getUuid(), "playerLastBlockpos");
            BlockPos _p = _fp.map(o -> (BlockPos) o).orElseGet(p::getBlockPos);

            return pos.isWithinDistance(_p, 10);
        });
        for(var player : players.toList()){
            if(player instanceof ServerPlayerEntity splayer){

                Optional<Object> _fp = ServerPlayerManager.getProperty(splayer.getUuid(), "playerLastBlockpos");
                BlockPos _p = _fp.map(o -> (BlockPos) o).orElseGet(splayer::getBlockPos);

                ContainerPacket.ContainerC2SPacket p = new ContainerPacket.ContainerC2SPacket(
                        splayer.getUuid(), _p);


                PacketsC2S.onContainerPacket(p, splayer, null);
            }
        }
    }

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"))
    private void duck$removeStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir){

        LootableContainerBlockEntity self = ((LootableContainerBlockEntity)(Object)this);
        BlockPos pos = self.getPos();

        if(!self.hasWorld()){
            return;
        }

        Stream<? extends PlayerEntity> players = self.getWorld().getPlayers().stream().filter(p -> {
            Optional<Object> _fp = ServerPlayerManager.getProperty(p.getUuid(), "playerLastBlockpos");
            BlockPos _p = _fp.map(o -> (BlockPos) o).orElseGet(p::getBlockPos);

            return pos.isWithinDistance(_p, 10);
        });

        for(var player : players.toList()){

            if(player instanceof ServerPlayerEntity splayer){

                Optional<Object> _fp = ServerPlayerManager.getProperty(splayer.getUuid(), "playerLastBlockpos");
                BlockPos _p = _fp.map(o -> (BlockPos) o).orElseGet(splayer::getBlockPos);

                ContainerPacket.ContainerC2SPacket p = new ContainerPacket.ContainerC2SPacket(
                        splayer.getUuid(), _p);

                PacketsC2S.onContainerPacket(p, splayer, null);
            }
        }
    }
}
