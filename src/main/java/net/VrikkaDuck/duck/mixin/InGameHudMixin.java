package net.VrikkaDuck.duck.mixin;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(at = @At("RETURN"), method = "render")
    private void render(CallbackInfo cb){
        if(Configs.Actions.RENDER_CONTAINER_TOOLTIP){
            if(Configs.Admin.INSPECT_CONTAINER.getBooleanValue()){
                if(Configs.Generic.INSPECT_CONTAINER.getKeybind().isKeybindHeld()){
                    if(Configs.Actions.RENDER_DOUBLE_CHEST_TOOLTIP){

                        GuiRenderUtils.renderDoubleChestPreview(Configs.Actions.CONTAINER_ITEM_STACK, GuiUtils.getScaledWindowWidth() / 2 - 96,
                                GuiUtils.getScaledWindowHeight() / 2 + 30, true);
                    }else{
                        RenderUtils.renderShulkerBoxPreview(Configs.Actions.CONTAINER_ITEM_STACK, GuiUtils.getScaledWindowWidth() / 2 - 96,
                                GuiUtils.getScaledWindowHeight() / 2 + 30, true);
                    }
                }
            }
        }
    }
}
