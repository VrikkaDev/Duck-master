package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChiseledBookshelfBlockEntity.class)
public class ChiseledBookshelfBlockEntityMixin {
    @Inject(method = "updateState", at = @At("RETURN"))
    private void duck$updateState(int interactedSlot, CallbackInfo ci){
        ChiseledBookshelfBlockEntity self = ((ChiseledBookshelfBlockEntity) (Object)this);
        NetworkHandler.Server.SendBlockEntityToNearby(self.getWorld(), self.getPos());
    }
}
