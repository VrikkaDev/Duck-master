package net.VrikkaDuck.duck.render.gui.inventory.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.Iterator;

import static net.VrikkaDuck.duck.util.GuiRenderUtils.drawTexture;


public class VillagerTradeRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public VillagerTradeRenderer(){
    }
    private static final Identifier MERCHANT_TEXTURE = new Identifier("textures/gui/container/villager2.png");
    private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");

    public void render(TradeOfferList trades, int x, int y, DrawContext context){
        RenderSystem.applyModelViewMatrix();

        RenderUtils.setupBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, MERCHANT_TEXTURE);

        x -= 105;
        y -= 82;
        //Render Background
        int h = 84;

        //top
        drawTexture(context, x+9, y, 5, 0, 0, 93, h, 512, 256);//left
        drawTexture(context, x+102, y, 5, 100, 0, 7, h, 512, 256);//mid line
        drawTexture(context, x+105, y, 5, 4, 0, 90, h, 512, 256);//mid right
        drawTexture(context, x+105+90, y, 5, 100, 0, 2, h, 512, 256);//rightleft WHAT???
        drawTexture(context, x+105+92, y, 5, 273, 0, 3, h, 512, 256);//right

        y+=84;
        //bottom
        drawTexture(context, x+9, y, 5, 0, 124, 93, 42, 512, 256);//left
        drawTexture(context, x+102, y, 5, 100, 124, 7, 42, 512, 256);//mid line
        drawTexture(context, x+105, y, 5, 4, 124, 90, 42, 512, 256);//mid right
        drawTexture(context, x+105+90, y, 5, 100, 124, 2, 42, 512, 256);//rightleft WHAT???
        drawTexture(context, x+105+92, y, 5, 273, 124, 3, 42, 512, 256);//right
        y-=84;

        RenderUtils.setupBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, MERCHANT_TEXTURE);

        if (!trades.isEmpty()) {
            x+=9;
            int k = y + 16 + 1;
            int l = x + 5 + 5;
            RenderSystem.setShaderTexture(0, MERCHANT_TEXTURE);
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            Iterator<TradeOffer> var11 = trades.iterator();
            TradeOffer tradeOffer;
            int e = 0;
            while(var11.hasNext()) {
                tradeOffer = var11.next();
                ItemStack itemStack = tradeOffer.getOriginalFirstBuyItem();
                ItemStack itemStack2 = tradeOffer.getAdjustedFirstBuyItem();
                ItemStack itemStack3 = tradeOffer.getSecondBuyItem();
                ItemStack itemStack4 = tradeOffer.getSellItem();
                if(e == 5){
                    l = x + 5 + 5 + 93 - 1;
                    k = y + 16 + 1;
                }
                int n = k + 2;
                renderArrow(tradeOffer, l, n, context);
                renderTradeButton(l,n, context);

                renderFirstBuyItem(context, itemStack2, itemStack, l, n);
                if (!itemStack3.isEmpty()) {
                    context.drawItem(itemStack3, l + 30, n);
                    context.drawItemInSlot(mc.textRenderer, itemStack3, l + 30, n);
                }

                context.drawItem(itemStack4, l + 63, n);
                context.drawItemInSlot(mc.textRenderer, itemStack4, l + 63, n);

                k += 20;
                e++;
            }

            RenderSystem.enableDepthTest();
        }
    }
    private void renderFirstBuyItem(DrawContext context, ItemStack adjustedFirstBuyItem, ItemStack originalFirstBuyItem, int x, int y) {
        context.drawItem(adjustedFirstBuyItem, x, y);
        if (originalFirstBuyItem.getCount() == adjustedFirstBuyItem.getCount()) {
            context.drawItemInSlot(mc.textRenderer, adjustedFirstBuyItem, x, y);
        } else {
            context.drawItemInSlot(mc.textRenderer, originalFirstBuyItem, x, y, originalFirstBuyItem.getCount() == 1 ? "1" : null);
            context.drawItemInSlot(mc.textRenderer, adjustedFirstBuyItem, x + 14, y, adjustedFirstBuyItem.getCount() == 1 ? "1" : null);
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, MERCHANT_TEXTURE);
            drawTexture(context, x + 7, y + 12, 300, 0.0F, 176.0F, 9, 2, 512, 256);
        }

    }

    private void renderArrow(TradeOffer tradeOffer, int x, int y, DrawContext context) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, MERCHANT_TEXTURE);
        x -= 10;
        if (tradeOffer.isDisabled()) {
            drawTexture(context, x + 5 + 35 + 20, y + 3, 700, 25.0F, 171.0F, 10, 9, 512, 256);
        } else {
            drawTexture(context, x + 5 + 35 + 20, y + 3, 770, 15.0F, 171.0F, 10, 9, 512, 256);
        }
    }

    private void renderTradeButton(int x, int y, DrawContext context){
        RenderUtils.setupBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        drawTexture(context, x+35, y-2, 5,152, 65, 48, 21, 256, 256);//right
        drawTexture(context, x-5, y-2,5, 0, 65, 45, 21, 256, 256);//left
    }
}
