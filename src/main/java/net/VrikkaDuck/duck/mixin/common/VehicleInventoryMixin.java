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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(VehicleInventory.class)
public interface VehicleInventoryMixin {
    @Inject(method = "setInventoryStack", at = @At(value = "RETURN"))
    private void duck$setInventoryStack(int slot, ItemStack stack, CallbackInfo ci){
        if(!(((VehicleInventory) (Object)this) instanceof StorageMinecartEntity sme)){
            return;
        }
        NetworkHandler.Server.SendEntityToNearby(sme);
    }

    @Inject(method = "removeInventoryStack(I)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"))
    private void duck$removeInventoryStack(int slot, CallbackInfoReturnable<ItemStack> cir){

        if(!(((VehicleInventory) (Object)this) instanceof StorageMinecartEntity sme)){
            return;
        }

        NetworkHandler.Server.SendEntityToNearby(sme);
    }
}
