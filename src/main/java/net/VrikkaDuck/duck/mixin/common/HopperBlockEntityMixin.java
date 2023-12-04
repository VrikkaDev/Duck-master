package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.util.PacketUtils;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {

    @Inject(method = "setStack*", at = @At("RETURN"))
    private void duck$setStack(int slot, ItemStack stack, CallbackInfo ci){
        HopperBlockEntity self = ((HopperBlockEntity)(Object)this);

        PacketUtils.createAndSendS2CContainer(self.getWorld(), self);
    }

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"))
    private void duck$removeStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir){
        HopperBlockEntity self = ((HopperBlockEntity)(Object)this);

        PacketUtils.createAndSendS2CContainer(self.getWorld(), self);
    }
}
