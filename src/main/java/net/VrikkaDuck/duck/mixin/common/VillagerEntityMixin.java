package net.VrikkaDuck.duck.mixin.common;

import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {
    @Inject(method = "restock", at = @At("RETURN"))
    private void duck$restock(CallbackInfo ci){

    }

    @Inject(method = "updateDemandBonus", at = @At("RETURN"))
    private void duck$updateDemandBonus(CallbackInfo ci){

    }
}
