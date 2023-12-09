package net.VrikkaDuck.duck.render.gui.inventory.blockentity;

import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public class ShulkerInventoryRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public ShulkerInventoryRenderer(){
    }
    public void render(ItemStack stack, int baseX, int baseY, boolean useBgColors, DrawContext drawContext){
        RenderUtils.renderShulkerBoxPreview(stack, baseX, baseY, useBgColors, drawContext);
    }
}
