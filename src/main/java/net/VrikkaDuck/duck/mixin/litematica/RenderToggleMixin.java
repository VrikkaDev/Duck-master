package net.VrikkaDuck.duck.mixin.litematica;

import fi.dy.masa.litematica.event.KeyCallbacks;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import net.VrikkaDuck.duck.util.LitematicaUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {"fi.dy.masa.litematica.event.KeyCallbacks$RenderToggle"})
public class RenderToggleMixin {
    @Inject(method = "onKeyAction", at = @At("RETURN"), remap = false)
    private void duck$onKeyAction(KeyAction action, IKeybind key, CallbackInfoReturnable<Boolean> cir){
        LitematicaUtils.UpdateLitematicaWorld();
    }
}
