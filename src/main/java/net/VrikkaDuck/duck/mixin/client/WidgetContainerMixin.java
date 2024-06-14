package net.VrikkaDuck.duck.mixin.client;

import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetContainer;
import net.VrikkaDuck.duck.render.gui.config.ConfigGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WidgetContainer.class)
public class WidgetContainerMixin {
    @Shadow protected WidgetBase hoveredSubWidget;

    @Inject(method = "drawSubWidgets", at = @At("RETURN"))
    private void dsw(int mouseX, int mouseY, DrawContext drawContext, CallbackInfo ci){

        if(!(MinecraftClient.getInstance().currentScreen instanceof ConfigGui)){
            return;
        }

        if(!ConfigGui.shouldRenderConfigTooltip){
            hoveredSubWidget = null;
        }
    }
}
