package net.VrikkaDuck.duck.util;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.VrikkaDuck.duck.Main;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.event.ClientBlockHitHandler;
import net.VrikkaDuck.duck.event.ClientNetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.registry.Registry;

import java.util.List;

import static fi.dy.masa.malilib.render.InventoryOverlay.renderStackAt;
import static fi.dy.masa.malilib.render.RenderUtils.*;

public class GuiRenderUtils {
    private static MinecraftClient mc = MinecraftClient.getInstance();
    private static Identifier ORB_TEXTURE = new Identifier("textures/entity/experience_orb.png");
    private static float refreshFurnace = 0;

    public static void renderDoubleChestPreview(ItemStack stack, int baseX, int baseY, boolean useBgColors)
    {
        if (stack.hasNbt())
        {
            DefaultedList<ItemStack> items = InventoryUtils.getStoredItems(stack, -1);

            if (items.size() == 0)
            {
                return;
            }

            InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.FIXED_54;
            InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, items.size());

            int screenWidth = GuiUtils.getScaledWindowWidth();
            int screenHeight = GuiUtils.getScaledWindowHeight();
            int height = props.height + 18;
            int x = MathHelper.clamp(baseX + 8     , 0, screenWidth - props.width);
            int y = MathHelper.clamp(baseY - height, 0, screenHeight - height);

            if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock)
            {
                setShulkerboxBackgroundTintColor((ShulkerBoxBlock) ((BlockItem) stack.getItem()).getBlock(), useBgColors);
            }
            else
            {
                color(1f, 1f, 1f, 1f);
            }

            disableDiffuseLighting();
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            matrixStack.translate(0, 0, 500);
            RenderSystem.applyModelViewMatrix();

            InventoryOverlay.renderInventoryBackground(type, x, y, props.slotsPerRow, items.size(), mc());

            enableDiffuseLightingGui3D();

            Inventory inv = fi.dy.masa.malilib.util.InventoryUtils.getAsInventory(items);
            InventoryOverlay.renderInventoryStacks(type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1, mc());

            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
        }
    }
    public static void renderFurnacePreview(NbtCompound nbt, int baseX, int baseY, boolean useBgColors){

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
        InventoryOverlay.renderInventoryBackground(type, x, y, props.slotsPerRow, 3, mc());

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

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        renderStackAt(slot0, x +   8, y +  8, 1, mc);
        renderStackAt(slot1, x +   8, y + 44, 1, mc);
        renderStackAt(slot2, x +  68, y + 26, 1, mc);

        matrixStack.push();


        RenderSystem.applyModelViewMatrix();

        matrixStack.translate(x,y,0);

        RenderSystem.disableDepthTest();

        matrixStack.loadIdentity();

        String xpString = nbt.contains("xp") ? String.valueOf(nbt.getFloat("xp")) : "0";

        mc.textRenderer.draw(matrixStack, Text.of("xp: " + xpString),x+32, y+54, 0x8F4F4F4F);

        matrixStack.pop();

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();

        refreshFurnace += mc.getTickDelta();
        if(refreshFurnace >= 60){
            ClientBlockHitHandler.INSTANCE().reload();
            refreshFurnace = 0;
        }
    }
    private static MinecraftClient mc()
    {
        return MinecraftClient.getInstance();
    }
}
