package net.VrikkaDuck.duck.mixin.litematica;

import fi.dy.masa.litematica.event.WorldLoadListener;
import net.VrikkaDuck.duck.util.LitematicaUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldLoadListener.class)
public class WorldLoadListenerMixin {
    @Inject(method = "onWorldLoadPost", at = @At("RETURN"), remap = false)
    private void duck$onWorldLoadPost(CallbackInfo ci){
        LitematicaUtils.UpdateLitematicaWorld();
    }
}
