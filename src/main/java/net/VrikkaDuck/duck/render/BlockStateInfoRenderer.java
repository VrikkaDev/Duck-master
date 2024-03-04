package net.VrikkaDuck.duck.render;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.BlockUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BlockStateInfoRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public BlockStateInfoRenderer(){
    }

    void render(DrawContext context){
        if(mc.world == null){
            return;
        }
        if(Configs.Actions.LOOKING_AT == null){
            return;
        }
        if(Configs.Actions.LOOKING_AT_ENTITY != null){
            return;
        }
        BlockState bs = mc.world.getBlockState(Configs.Actions.LOOKING_AT);
        if (bs == null) {
            return;
        }


        List<String> props = BlockUtils.getFormattedBlockStateProperties(bs);
        ItemStack stack = bs.getBlock().asItem().getDefaultStack();

        Identifier rl = Registries.BLOCK.getId(bs.getBlock());
        String blockRegistryname = rl.toString();
        TextRenderer textRenderer = mc.textRenderer;

        int width = Math.max(120, textRenderer.getWidth(blockRegistryname) + 20);
        int height = 70 + (props.size() * StringUtils.getFontHeight()) - StringUtils.getFontHeight();

        int x = GuiUtils.getScaledWindowWidth() / 2 - width / 2;
        int y = GuiUtils.getScaledWindowHeight() / 2 + 15;


        GuiRenderUtils.renderBackground(x, y, width, height);


        int x1 = x + 10;
        y += 5;

        context.drawText(textRenderer, "Block state", x1, y, 0x000000, false);

        y += 11;

        RenderUtils.enableDiffuseLightingGui3D();
        context.drawItem(stack, x1, y);
        context.drawItemInSlot(textRenderer, stack, x1, y);

        RenderUtils.disableDiffuseLighting();

        context.drawText(textRenderer, stack.getName(), x1 + 20, y + 4, 0x019104, false);


        y += 20;
        context.drawText(textRenderer, blockRegistryname, x1, y, 0x834ef5, false);
        y += textRenderer.fontHeight + 4;

        //RenderUtils.renderText(x1, y, 0xFFB0B0B0, props, context);
        if (!props.isEmpty())
        {
            for (String line : props)
            {
                context.drawText(textRenderer, line, x+10, y, 0xFFFFFF, true);
                y += textRenderer.fontHeight + 2;
            }
        }
    }
}
