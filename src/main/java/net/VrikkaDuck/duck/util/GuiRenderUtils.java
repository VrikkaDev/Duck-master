package net.VrikkaDuck.duck.util;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.TextureHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.data.client.TextureMap;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

import java.util.List;

import static fi.dy.masa.malilib.render.InventoryOverlay.*;

public class GuiRenderUtils {

    public static final Identifier TEXTURE_DEMO_BACKGROUND = new Identifier("textures/gui/demo_background.png");
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void drawTexture(DrawContext context, int x, int y, int z, int u, int v, int width, int height) {
        drawTexture(context, x, y, z, (float)u, (float)v, width, height, 256, 256);
    }

    public static void drawTexture(DrawContext context, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight) {

        drawTexture(context, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
    }
    private static void drawTexture(DrawContext context, int x0, int x1, int y0, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight) {
        drawTexturedQuad(context.getMatrices().peek().getPositionMatrix(), x0, x1, y0, y1, z, (u + 0.0F) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight);
    }
    private static void drawTexturedQuad(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {

        RenderUtils.setupBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.applyModelViewMatrix();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        buffer.vertex(matrix, (float)x0, (float)y1, (float)z).texture(u0, v1).next();
        buffer.vertex(matrix, (float)x1, (float)y1, (float)z).texture(u1, v1).next();
        buffer.vertex(matrix, (float)x1, (float)y0, (float)z).texture(u1, v0).next();
        buffer.vertex(matrix, (float)x0, (float)y0, (float)z).texture(u0, v0).next();

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        tessellator.draw();
    }


    public static void renderScaledText(int x, int y, int color, float scale, int bgcolor, List<Text> lines, DrawContext drawContext)
    {
        if(lines.isEmpty()){
            return;
        }

        TextRenderer textRenderer = mc.textRenderer;
        final int bgMargin = 2;

        drawContext.getMatrices().push();
        Matrix4f m = drawContext.getMatrices().peek().getPositionMatrix();

        m.scale(scale);

        if (!lines.isEmpty())
        {

            for (Text line : lines)
            {
                final int width = (int) (textRenderer.getWidth(line) * scale);

                int tw = line.getString().length();

                if (bgcolor!=0)
                {
                    drawContext.getMatrices().push();
                    RenderUtils.drawRect((int) (x - (tw*2*scale) - bgMargin),y-1, (int) (width + bgMargin * 2), 7, bgcolor);
                    drawContext.getMatrices().pop();
                }

                textRenderer.draw(line, x / scale - tw*2, y/scale, color, false, m, drawContext.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
                y += 7;
            }
        }

        drawContext.getMatrices().pop();
    }

    public static void renderItemSlot(int x, int y, DrawContext context){
        context.drawTexture(TEXTURE_FURNACE, x-1, y-1, 55, 16, 18, 18);
    }


    public static void renderBackground(int x, int y, int width, int height){

        RenderUtils.setupBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.applyModelViewMatrix();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        RenderUtils.bindTexture(TEXTURE_DISPENSER);
        RenderUtils.drawTexturedRectBatched(x, y,0,0,7,  height, buffer); // left (top)
        RenderUtils.drawTexturedRectBatched(x +  7, y, 176-width,0, width,7,buffer); // top (right)
        RenderUtils.drawTexturedRectBatched(x,y + height,0,159, width,7, buffer); // bottom (left)
        RenderUtils.drawTexturedRectBatched(x + width, y + 7, 169, 166-height,7, height, buffer); // right (bottom)

        if(width >= 150){
            RenderUtils.drawTexturedRectBatched(x+7, y,10,0,150,7, buffer); // top
            RenderUtils.drawTexturedRectBatched(x+width-150, y+height,10,159,150,7, buffer); // bottom
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        tessellator.draw();


        RenderUtils.bindTexture(TEXTURE_DEMO_BACKGROUND);
        RenderUtils.drawTexturedRect(x+7 , y+7, 10,10,width - 7, height - 7);

        if (width >= 150){
            RenderUtils.bindTexture(TEXTURE_DEMO_BACKGROUND);
            RenderUtils.drawTexturedRect(x+150 , y+7, 10,10,width - 150, height - 7);
        }

        //RenderUtils.drawRect(x+7 , y+7, width - 7, height - 7, ColorHelper.Argb.getArgb(255, 198, 198, 198));

    }
        private static MinecraftClient mc()
    {
        return MinecraftClient.getInstance();
    }
}
