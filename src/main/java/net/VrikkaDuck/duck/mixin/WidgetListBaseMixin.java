package net.VrikkaDuck.duck.mixin;

import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import net.VrikkaDuck.duck.gui.ConfigGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WidgetListBase.class)
public class WidgetListBaseMixin<WIDGET> {
    @Shadow @Final protected List<WIDGET> listWidgets;

    @Inject(at = @At("RETURN"), method = "drawContents")
    public void drawContents(CallbackInfo cb){
        ConfigGui.listWidgets = this.listWidgets;
    }
}