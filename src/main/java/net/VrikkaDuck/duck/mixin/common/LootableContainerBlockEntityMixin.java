package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.VrikkaDuck.duck.util.PacketUtils;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
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

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.CONTAINER));
                buf.writeBlockPos(self.getPos());

                PacketUtils.handleContainerInspection(new CustomPayloadC2SPacket(buf), splayer);
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

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.CONTAINER));
                buf.writeBlockPos(self.getPos());

                PacketUtils.handleContainerInspection(new CustomPayloadC2SPacket(buf), splayer);
            }
        }
    }
}
