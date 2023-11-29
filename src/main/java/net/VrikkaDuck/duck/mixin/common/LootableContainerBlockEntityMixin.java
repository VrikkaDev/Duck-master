package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.networking.PacketsC2S;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.VrikkaDuck.duck.util.PacketUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(LootableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {
    @Inject(method = "setStack", at = @At("RETURN"))
    private void duck$setStack(int slot, ItemStack stack, CallbackInfo ci){

        LootableContainerBlockEntity self = ((LootableContainerBlockEntity)(Object)this);
        BlockPos pos = self.getPos();

        if(!self.hasWorld()){
            return;
        }

        Stream<? extends PlayerEntity> players = self.getWorld().getPlayers().stream().filter(p -> pos.isWithinDistance(p.getPos(), 10));

        for(var player : players.toList()){

            if(player instanceof ServerPlayerEntity splayer){

                ContainerPacket.ContainerC2SPacket p = new ContainerPacket.ContainerC2SPacket(
                        splayer.getUuid(), self.getPos());

                PacketsC2S.onContainerPacket(p, splayer, null);
            }
        }
    }

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"))
    private void removeStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir){

        LootableContainerBlockEntity self = ((LootableContainerBlockEntity)(Object)this);
        BlockPos pos = self.getPos();

        if(!self.hasWorld()){
            return;
        }

        Stream<? extends PlayerEntity> players = self.getWorld().getPlayers().stream().filter(p -> pos.isWithinDistance(p.getPos(), 10));

        for(var player : players.toList()){

            if(player instanceof ServerPlayerEntity splayer){

                ContainerPacket.ContainerC2SPacket p = new ContainerPacket.ContainerC2SPacket(
                        splayer.getUuid(), self.getPos());

                PacketsC2S.onContainerPacket(p, splayer, null);
            }
        }
    }
}
