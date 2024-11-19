package net.VrikkaDuck.duck.mixin.litematica;


import fi.dy.masa.litematica.util.SchematicWorldRefresher;
import net.VrikkaDuck.duck.util.LitematicaUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(SchematicWorldRefresher.class)
public class SchematicWorldRefresherMixin {

    @Inject(method = "updateAll", at = @At("RETURN"), remap = false)
    private void duck$updateAll(CallbackInfo ci){
        LitematicaUtils.UpdateLitematicaWorld();
    }
}
