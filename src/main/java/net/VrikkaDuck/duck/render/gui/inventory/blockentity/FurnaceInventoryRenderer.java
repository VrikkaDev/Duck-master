package net.VrikkaDuck.duck.render.gui.inventory.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.GuiUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import static fi.dy.masa.malilib.render.InventoryOverlay.renderStackAt;
import static fi.dy.masa.malilib.render.RenderUtils.color;
import static fi.dy.masa.malilib.render.RenderUtils.disableDiffuseLighting;

public class FurnaceInventoryRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public FurnaceInventoryRenderer(){
    }
    public void render(NbtCompound nbt, int baseX, int baseY, DrawContext context){

        if(nbt.isEmpty()){
            return;
        }

        InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.FURNACE;
        InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, 3);

        int screenWidth = GuiUtils.getScaledWindowWidth();
        int screenHeight = GuiUtils.getScaledWindowHeight();
        int height = props.height + 18;
        int x = MathHelper.clamp( baseX + 8, 0, screenWidth - props.width);
        int y = MathHelper.clamp(baseY - height, 0, screenHeight - height);

        color(1f, 1f, 1f, 1f);

        disableDiffuseLighting();
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        InventoryOverlay.renderInventoryBackground(type, x, y, props.slotsPerRow, 3, mc);

        NbtList list = nbt.getList("Items", 10);

        ItemStack slot0 = new ItemStack(Items.AIR, 1);
        ItemStack slot1 = new ItemStack(Items.AIR, 1);
        ItemStack slot2 = new ItemStack(Items.AIR, 1);

        for(int i = 0; i < list.size(); i++){
            byte slot = list.getCompound(i).getByte("Slot");
            if(slot == 0){
                slot0 = ItemStack.fromNbt(list.getCompound(i));
            }else if(slot == 1){
                slot1 = ItemStack.fromNbt(list.getCompound(i));
            }else if(slot == 2){
                slot2 = ItemStack.fromNbt(list.getCompound(i));
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);

        renderStackAt(slot0, x +   8, y +  8, 1, mc, context);
        renderStackAt(slot1, x +   8, y + 44, 1, mc, context);
        renderStackAt(slot2, x +  68, y + 26, 1, mc, context);

        matrixStack.push();
        RenderSystem.applyModelViewMatrix();
        matrixStack.translate(x,y,0);
        RenderSystem.disableDepthTest();
        matrixStack.loadIdentity();

        String xpString = nbt.contains("xp") ? String.valueOf(nbt.getFloat("xp")) : "0";

        context.drawText(mc.textRenderer, Text.of("xp: " + xpString),x+32, y+54, 4210752, false);

        matrixStack.pop();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }
}
