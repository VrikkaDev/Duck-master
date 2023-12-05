package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.networking.EntityDataType;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.ServerPlayerManager;
import net.VrikkaDuck.duck.networking.packet.EntityPacket;
import net.VrikkaDuck.duck.util.NbtUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(StorageMinecartEntity.class)
public class StorageMinecartEntityMixin {

    @Unique
    private void sendP(){
        StorageMinecartEntity self = ((StorageMinecartEntity) (Object)this);

        Stream<? extends PlayerEntity> players = self.getWorld().getPlayers().stream().filter(p -> {
            Optional<Object> _fp = ServerPlayerManager.INSTANCE().getProperty(p.getUuid(), "playerLastBlockpos");
            BlockPos _p = _fp.map(o -> (BlockPos) o).orElseGet(p::getBlockPos);

            return self.getPos().isInRange(_p.toCenterPos(), 10);
        });

        NbtCompound compound = new NbtCompound();

        NbtList _l = new NbtList();
        NbtCompound tc = NbtUtils.getMinecartContainerNbt((StorageMinecartEntity)self, null).orElse(new NbtCompound());

        tc.putUuid("uuid", self.getUuid());
        tc.putInt("entityType", self instanceof ChestMinecartEntity ? EntityDataType.MINECART_CHEST.value : EntityDataType.MINECART_HOPPER.value);

        _l.add(tc);
        compound.put("entities", _l);

        for(var player : players.toList()){
            if(player instanceof ServerPlayerEntity splayer){

                Optional<Object> _fp = ServerPlayerManager.INSTANCE().getProperty(splayer.getUuid(), "playerLastBlockpos");
                BlockPos _p = _fp.map(o -> (BlockPos) o).orElseGet(splayer::getBlockPos);

                EntityPacket.EntityS2CPacket p = new EntityPacket.EntityS2CPacket(splayer.getUuid(), compound);

                NetworkHandler.SendToClient(splayer, p);
            }
        }
    }
    @Inject(method = "setStack", at = @At("RETURN"))
    private void duck$setStack(int slot, ItemStack stack, CallbackInfo ci){
        sendP();
    }
    @Inject(method = "removeStack(I)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"))
    private void duck$removeStack1(int slot, CallbackInfoReturnable<ItemStack> cir){
        sendP();
    }
    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"))
    private void duck$removeStack2(int slot, int amount, CallbackInfoReturnable<ItemStack> cir){
        sendP();
    }
}
