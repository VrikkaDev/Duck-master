package net.VrikkaDuck.duck.util;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.event.ClientBlockHitHandler;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.Iterator;

import static fi.dy.masa.malilib.render.InventoryOverlay.*;
import static fi.dy.masa.malilib.render.RenderUtils.*;

public class GuiRenderUtils {
    /*
    -----------------------------------------------

    This whole class is one BIG mess ☺
    need to clean up at some point

    -----------------------------------------------
     */

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static float refreshFurnace = 0;
    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    private static final Identifier[] EMPTY_SLOT_TEXTURES = new Identifier[] {
            new Identifier("item/empty_armor_slot_boots"),
            new Identifier("item/empty_armor_slot_leggings"),
            new Identifier("item/empty_armor_slot_chestplate"),
            new Identifier("item/empty_armor_slot_helmet") };
    private static final Identifier MERCHANT_TEXTURE = new Identifier("textures/gui/container/villager2.png");
    private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
    private static final ItemRenderer itemRenderer = mc.getItemRenderer();

    private static ItemStack getEquippedStack(EquipmentSlot slot, Inventory inv){
        if(slot.getArmorStandSlotId() == EquipmentSlot.CHEST.getArmorStandSlotId()){
            return inv.getStack(102);
        }else if(slot.getArmorStandSlotId() == EquipmentSlot.LEGS.getArmorStandSlotId()){
            return inv.getStack(101);
        }else if(slot.getArmorStandSlotId() == EquipmentSlot.FEET.getArmorStandSlotId()){
            return inv.getStack(100);
        }else if(slot.getArmorStandSlotId() == EquipmentSlot.HEAD.getArmorStandSlotId()){
            return inv.getStack(103);
        }else{
            return inv.getStack(120);
        }
    }

    public static void renderHopperPreview(ItemStack stack, int baseX, int baseY, boolean useBgColors){
        if (stack.hasNbt())
        {
            DefaultedList<ItemStack> items = InventoryUtils.getStoredItems(stack, -1);

            if (items.size() == 0)
            {
                return;
            }

            InventoryOverlay.InventoryRenderType type = InventoryOverlay.InventoryRenderType.HOPPER;
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
    public static void renderFurnacePreview(NbtCompound nbt, int baseX, int baseY){

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

        mc.textRenderer.draw(matrixStack, Text.of("xp: " + xpString),x+32, y+54, 4210752);//white = 0xffffffff

        matrixStack.pop();

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();

        refreshFurnace += mc.getTickDelta();
        if(refreshFurnace >= 60){
            ClientBlockHitHandler.INSTANCE().reload();
            refreshFurnace = 0;
        }
    }
    public static void renderBeehivePreview(NbtCompound nbt, int baseX, int baseY){
        renderBackground(baseX-35, baseY-31);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();

        matrixStack.loadIdentity();

        String honeyCount = nbt.contains("HoneyLevel") ? String.valueOf(nbt.getInt("HoneyLevel")) : "0";
        String beeCount = nbt.contains("BeeCount") ? String.valueOf(nbt.getInt("BeeCount")) : "0";

        mc.textRenderer.draw(matrixStack, "Honey: " + honeyCount, baseX-28, baseY-22,5);
        mc.textRenderer.draw(matrixStack, "Bees: " + beeCount, baseX-28, baseY-10,5);

        matrixStack.pop();
    }
    public static void renderPlayerInventory(Inventory inventory, int x, int y){

       // InventoryOverlay.renderInventoryBackground(InventoryOverlay.InventoryRenderType.GENERIC, x-88, y-41, 9, 36, mc);

        MatrixStack matrixStack = RenderSystem.getModelViewStack();

        x-=65;
        y-=50;

        matrixStack.push();


        //Render Equipment background

        x -= 50;

        RenderUtils.color(1f, 1f, 1f, 1f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.applyModelViewMatrix();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        RenderUtils.bindTexture(TEXTURE_DISPENSER);

        RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0, 50, 83, buffer); // top-left (main part)
       // RenderUtils.drawTexturedRectBatched(x + 50, y     , 173,   0,  3, 83, buffer); // right edge top
        RenderUtils.drawTexturedRectBatched(x, y + 83,   0, 160, 50,  6, buffer); // bottom edge left
        RenderUtils.drawTexturedRectBatched(x + 50, y + 90, 155, 173,  3,  3, buffer); // bottom right corner83

        for (int i = 0, xOff = 7, yOff = 7; i < 2; ++i, yOff += 18)
        {
            RenderUtils.drawTexturedRectBatched(x + xOff, y + yOff, 61, 16, 18, 18, buffer);
        }
        for (int i = 0, xOff = 25, yOff = 7; i < 2; ++i, yOff += 18)
        {
            RenderUtils.drawTexturedRectBatched(x + xOff, y + yOff, 61, 16, 18, 18, buffer);
        }

        //offhand
        RenderUtils.drawTexturedRectBatched(x + 25, y + 3 * 19 + 7, 61, 16, 18, 18, buffer);


        tessellator.draw();

        RenderUtils.bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        if (inventory.getStack(120).isEmpty())
        {
            Identifier texture = new Identifier("minecraft:item/empty_armor_slot_shield");
            RenderUtils.renderSprite(x + 28 + 1, y + 3 * 18 + 7 + 1, 16, 16, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, texture, matrixStack);
        }

        //Render equipment stacks

        for (int i = 0, xOff = 7, yOff = 7; i < 2; ++i, yOff += 18)
        {
            final EquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];
            ItemStack stack = getEquippedStack(eqSlot, inventory);

            if (!stack.isEmpty())
            {
                renderStackAt(stack, x + xOff + 1, y + yOff + 1, 1, mc);
            }
        }
        for (int i = 2, xOff = 25, yOff = 7; i < 4; ++i, yOff += 18)
        {
            final EquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];
            ItemStack stack = getEquippedStack(eqSlot, inventory);

            if (!stack.isEmpty())
            {
                renderStackAt(stack, x + xOff + 1, y + yOff + 1, 1, mc);
            }
        }

        ItemStack stack = getEquippedStack(EquipmentSlot.OFFHAND, inventory);

        if (!stack.isEmpty())
        {
            renderStackAt(stack, x + 25+1, y + 3 * 19 + 7 + 1, 1, mc);
        }

        x+=50+90-15;

        RenderUtils.setupBlend();
        Tessellator atessellator = Tessellator.getInstance();
        BufferBuilder abuffer = atessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.applyModelViewMatrix();
        abuffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        RenderUtils.bindTexture(TEXTURE_HOPPER);
        RenderUtils.drawTexturedRectBatched(x - 90 + 14, y,  4,   0,  180,   6, abuffer); // top (right)
        RenderUtils.drawTexturedRectBatched(x - 90 + 14, y+6,  4,  50,  177,  130, abuffer); // middle - 90 + 7

        atessellator.draw();

        x -= 90-18;
        y += 7;

        InventoryOverlay.renderInventoryStacks(InventoryRenderType.FIXED_27, Configs.Actions.TARGET_PLAYER_INVENTORY, x, y,9,9, 27, mc);
        y += 58;
        InventoryOverlay.renderInventoryStacks(InventoryRenderType.FIXED_27, Configs.Actions.TARGET_PLAYER_INVENTORY, x, y,9,0, 9, mc);

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }
    public static void renderTrades(TradeOfferList trades, int x, int y){

        MatrixStack matrices = RenderSystem.getModelViewStack();

        matrices.push();

        RenderSystem.applyModelViewMatrix();

        matrices.loadIdentity();

        RenderUtils.setupBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, MERCHANT_TEXTURE);

        x -= 105;
        y -= 82;

        //Render Background
        drawTexture(matrices, x, y, 5, 0.0F, 0.0F, 105, 166, 512, 256);
        drawTexture(matrices, x+105, y, 5, 4.0F, 0.0F, 97, 166, 512, 256);
        drawTexture(matrices, x+105+97, y, 5, 272.0F, 0.0F, 4, 166, 512, 256);

        matrices.pop();
        matrices.push();

        RenderUtils.setupBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, MERCHANT_TEXTURE);

        if (!trades.isEmpty()) {
            //int i = (this.width - this.backgroundWidth) / 2;
            //int j = (this.height - this.backgroundHeight) / 2;
            int k = y + 16 + 1;
            int l = x + 5 + 5;
            RenderSystem.setShaderTexture(0, MERCHANT_TEXTURE);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            int m = 0;
            Iterator<TradeOffer> var11 = trades.iterator();

                TradeOffer tradeOffer;
                while(var11.hasNext()) {
                    tradeOffer = (TradeOffer)var11.next();
                        ItemStack itemStack = tradeOffer.getOriginalFirstBuyItem();
                        ItemStack itemStack2 = tradeOffer.getAdjustedFirstBuyItem();
                        ItemStack itemStack3 = tradeOffer.getSecondBuyItem();
                        ItemStack itemStack4 = tradeOffer.getSellItem();

                        itemRenderer.zOffset = 500.0F;
                        int n = k + 2;
                                                            //Make this thing 5x2
                    renderArrow(matrices, tradeOffer, x, n);
                    renderTradeButton(matrices, l,n);

                    renderFirstBuyItem(matrices, itemStack2, itemStack, l, n);
                   if (!itemStack3.isEmpty()) {
                        itemRenderer.renderInGui(itemStack3, x + 5 + 35, n);
                        itemRenderer.renderGuiItemOverlay(mc.textRenderer, itemStack3, x + 5 + 35, n);
                    }

                    itemRenderer.renderInGui(itemStack4, x + 5 + 68, n);
                    itemRenderer.renderGuiItemOverlay(mc.textRenderer, itemStack4, x + 5 + 68, n);
                    itemRenderer.zOffset = 0.0F;
                    k += 20;
                    ++m;


                    //renderTradeButton(matrices, l, n);
                }

                RenderSystem.enableDepthTest();
        }



        matrices.pop();
    }
    private static void renderFirstBuyItem(MatrixStack matrices, ItemStack adjustedFirstBuyItem, ItemStack originalFirstBuyItem, int x, int y) {
        itemRenderer.renderInGui(adjustedFirstBuyItem, x, y);
        if (originalFirstBuyItem.getCount() == adjustedFirstBuyItem.getCount()) {
            itemRenderer.renderGuiItemOverlay(mc.textRenderer, adjustedFirstBuyItem, x, y);
        } else {
            itemRenderer.renderGuiItemOverlay(mc.textRenderer, originalFirstBuyItem, x, y, originalFirstBuyItem.getCount() == 1 ? "1" : null);
            itemRenderer.renderGuiItemOverlay(mc.textRenderer, adjustedFirstBuyItem, x + 14, y, adjustedFirstBuyItem.getCount() == 1 ? "1" : null);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, MERCHANT_TEXTURE);
            drawTexture(matrices, x + 7, y + 12, 300, 0.0F, 176.0F, 9, 2, 512, 256);
        }

    }

    private static void renderArrow(MatrixStack matricess, TradeOffer tradeOffer, int x, int y) {
        MatrixStack matrices = RenderSystem.getModelViewStack();
        matrices.push();
        matrices.loadIdentity();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, MERCHANT_TEXTURE);
        if (tradeOffer.isDisabled()) {
            drawTexture(matrices, x + 5 + 35 + 20, y + 3, 700, 25.0F, 171.0F, 10, 9, 512, 256);
        } else {
            drawTexture(matrices, x + 5 + 35 + 20, y + 3, 770, 15.0F, 171.0F, 10, 9, 512, 256);
        }
        matrices.pop();
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, int z, int u, int v, int width, int height) {
        drawTexture(matrices, x, y, z, (float)u, (float)v, width, height, 256, 256);
    }

    public static void drawTexture(MatrixStack matrices, int x, int y, int z, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        drawTexture(matrices, x, x + width, y, y + height, z, width, height, u, v, textureWidth, textureHeight);
    }
    private static void drawTexture(MatrixStack matrices, int x0, int x1, int y0, int y1, int z, int regionWidth, int regionHeight, float u, float v, int textureWidth, int textureHeight) {
        drawTexturedQuad(matrices.peek().getPositionMatrix(), x0, x1, y0, y1, z, (u + 0.0F) / (float)textureWidth, (u + (float)regionWidth) / (float)textureWidth, (v + 0.0F) / (float)textureHeight, (v + (float)regionHeight) / (float)textureHeight);
    }
    private static void drawTexturedQuad(Matrix4f matrix, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, (float)x0, (float)y1, (float)z).texture(u0, v1).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y1, (float)z).texture(u1, v1).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y0, (float)z).texture(u1, v0).next();
        bufferBuilder.vertex(matrix, (float)x0, (float)y0, (float)z).texture(u0, v0).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
    }

    private static void renderTradeButton(MatrixStack matricess,int x, int y){
        MatrixStack matrices = RenderSystem.getModelViewStack();
        matrices.push();
        matrices.loadIdentity();
        RenderUtils.setupBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        drawTexture(matrices, x+32-7, y-1, 100, 142, 66, 57, 20);//left
        drawTexture(matrices, x-5, y-1, 100, 0, 66, 32, 20);//right
        matrices.pop();
    }

    private static void renderBackground(int x, int y){

        RenderUtils.setupBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.applyModelViewMatrix();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        RenderUtils.bindTexture(TEXTURE_DISPENSER);
        RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0,   7,  31, buffer); // left (top)//61
        RenderUtils.drawTexturedRectBatched(x +  7, y     , 115,   0,  61,   7, buffer); // top (right)
        RenderUtils.drawTexturedRectBatched(x     , y + 31,   0, 159,  61,   7, buffer); // bottom (left)61
        RenderUtils.drawTexturedRectBatched(x + 61, y +  7, 169, 135,   7,  31, buffer); // right (bottom)61  //105
        RenderUtils.drawTexturedRectBatched(x +  7, y +  7,  5,  16,  54,  24, buffer); // middle

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();

        tessellator.draw();
    }
        private static MinecraftClient mc()
    {
        return MinecraftClient.getInstance();
    }
}
