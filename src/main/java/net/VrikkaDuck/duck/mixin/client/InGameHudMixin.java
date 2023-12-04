package net.VrikkaDuck.duck.mixin.client;

import net.VrikkaDuck.duck.rendering.OverlayRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Unique
    private final OverlayRenderer overlayRenderer = OverlayRenderer.INSTANCE();

    @Inject(at = @At("RETURN"), method = "render")
    private void render(DrawContext context, float tickDelta, CallbackInfo cb) {
        overlayRenderer.render(context);
    }

}
