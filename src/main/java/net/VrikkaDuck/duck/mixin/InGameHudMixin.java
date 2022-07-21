package net.VrikkaDuck.duck.mixin;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.minecraft.client.gui.hud.InGameHud;
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
                if(!Configs.Generic.INSPECT_CONTAINER.getKeybind().isKeybindHeld()){
                    Configs.Actions.RENDER_CONTAINER_TOOLTIP = false;
                    return;
                }
                if(Configs.Actions.CONTAINER_ITEM_STACK == null){
                    return;
                }
                if(Configs.Actions.RENDER_DOUBLE_CHEST_TOOLTIP == 1){
                        GuiRenderUtils.renderDoubleChestPreview(Configs.Actions.CONTAINER_ITEM_STACK, GuiUtils.getScaledWindowWidth() / 2 - 96,
                                GuiUtils.getScaledWindowHeight() / 2 + 60, true);
                }else if(Configs.Actions.RENDER_DOUBLE_CHEST_TOOLTIP == 2){

                    GuiRenderUtils.renderHopperPreview(Configs.Actions.CONTAINER_ITEM_STACK, GuiUtils.getScaledWindowWidth() / 2 - (52+8),
                            GuiUtils.getScaledWindowHeight() / 2 + (16+16), true);
                }else{
                    RenderUtils.renderShulkerBoxPreview(Configs.Actions.CONTAINER_ITEM_STACK, GuiUtils.getScaledWindowWidth() / 2 - 96,
                            GuiUtils.getScaledWindowHeight() / 2 + 30, true);
                }

            }
        }else if(Configs.Actions.RENDER_FURNACE_TOOLTIP){
            if(Configs.Admin.INSPECT_FURNACE.getBooleanValue()){
                if(!Configs.Generic.INSPECT_FURNACE.getKeybind().isKeybindHeld()){
                    Configs.Actions.RENDER_FURNACE_TOOLTIP = false;
                }
                GuiRenderUtils.renderFurnacePreview(Configs.Actions.FURNACE_NBT, GuiUtils.getScaledWindowWidth() / 2 - 59,
                        GuiUtils.getScaledWindowHeight() / 2 + 30, true);
            }
        }else if(Configs.Actions.RENDER_BEEHIVE_PREVIEW){
            if(Configs.Admin.INSPECT_BEEHIVE.getBooleanValue()){
                if(!Configs.Generic.INSPECT_BEEHIVE.getKeybind().isKeybindHeld()){
                    Configs.Actions.RENDER_BEEHIVE_PREVIEW = false;
                }
                GuiRenderUtils.renderBeehivePreview(Configs.Actions.BEEHIVE_NBT, GuiUtils.getScaledWindowWidth() / 2,
                        GuiUtils.getScaledWindowHeight() / 2);
            }
        }else if(Configs.Actions.RENDER_PLAYER_INVENTORY_PREVIEW){
            if(Configs.Admin.INSPECT_PLAYER_INVENTORY.getBooleanValue()){
                if(!Configs.Generic.INSPECT_PLAYER_INVENTORY.getKeybind().isKeybindHeld()){
                    Configs.Actions.RENDER_PLAYER_INVENTORY_PREVIEW = false;
                }
                GuiRenderUtils.renderPlayerInventory(Configs.Actions.TARGET_PLAYER_INVENTORY, GuiUtils.getScaledWindowWidth() / 2,
                        GuiUtils.getScaledWindowHeight() / 2);
            }
        }
    }
}
