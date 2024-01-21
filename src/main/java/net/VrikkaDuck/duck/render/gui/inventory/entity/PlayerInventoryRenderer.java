package net.VrikkaDuck.duck.render.gui.inventory.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

import static fi.dy.masa.malilib.render.InventoryOverlay.*;

public class PlayerInventoryRenderer {

    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
    private static final Identifier[] EMPTY_SLOT_TEXTURES = new Identifier[] { // todo maybe do someday
            new Identifier("item/empty_armor_slot_helmet"),
            new Identifier("item/empty_armor_slot_chestplate"),
            new Identifier("item/empty_armor_slot_leggings"),
            new Identifier("item/empty_armor_slot_boots")
    };

    private final MinecraftClient mc = MinecraftClient.getInstance();
    public PlayerInventoryRenderer(){
    }

    public void render(Inventory inventory, int x, int y, DrawContext context){
        x-=65;
        y-=50;
        //Render Equipment background

        x -= 50;

        RenderUtils.color(1f, 1f, 1f, 1f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.applyModelViewMatrix();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        RenderUtils.bindTexture(TEXTURE_DISPENSER);

        RenderUtils.drawTexturedRectBatched(x     , y     ,   0,   0, 50, 83, buffer); // top-left (main part)
        RenderUtils.drawTexturedRectBatched(x, y + 83,   0, 160, 50,  6, buffer); // bottom edge left
        RenderUtils.drawTexturedRectBatched(x + 50, y + 90, 155, 173,  3,  3, buffer); // bottom right corner83

        for (int i = 0, xOff = 7, yOff = 7; i < 2; ++i, yOff += 18) {
            RenderUtils.drawTexturedRectBatched(x + xOff, y + yOff, 61, 16, 18, 18, buffer);
        }
        for (int i = 0, xOff = 25, yOff = 7; i < 2; ++i, yOff += 18) {
            RenderUtils.drawTexturedRectBatched(x + xOff, y + yOff, 61, 16, 18, 18, buffer);
        }

        //offhand
        RenderUtils.drawTexturedRectBatched(x + 25, y + 3 * 19 + 7, 61, 16, 18, 18, buffer);
        tessellator.draw();
        RenderUtils.bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

        if (inventory.getStack(120).isEmpty()) {
            Identifier texture = new Identifier("minecraft:item/empty_armor_slot_shield");
            RenderUtils.renderSprite(x + 28 - 2, y + 3 * 18 + 7 + 4, 16, 16, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, texture, context);
        }
        //Render equipment stacks
        for (int i = 0, xOff = 7, yOff = 7; i < 2; ++i, yOff += 18) {
            final EquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];
            ItemStack stack = getEquippedStack(eqSlot, inventory);

            if (stack.isEmpty()) {
                RenderUtils.renderSprite(x + xOff + 1, y + yOff + 1, 16, 16, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_SLOT_TEXTURES[i], context);
            }else{
                renderStackAt(stack, x + xOff + 1, y + yOff + 1, 1, mc, context);
            }
        }
        for (int i = 2, xOff = 25, yOff = 7; i < 4; ++i, yOff += 18) {
            final EquipmentSlot eqSlot = VALID_EQUIPMENT_SLOTS[i];
            ItemStack stack = getEquippedStack(eqSlot, inventory);

            if (stack.isEmpty()) {
                RenderUtils.renderSprite(x + xOff + 1, y + yOff + 1, 16, 16, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_SLOT_TEXTURES[i], context);
            }else{
                renderStackAt(stack, x + xOff + 1, y + yOff + 1, 1, mc, context);
            }
        }
        ItemStack stack = getEquippedStack(EquipmentSlot.OFFHAND, inventory);
        if (!stack.isEmpty()) {
            renderStackAt(stack, x + 25+1, y + 3 * 19 + 7 + 1, 1, mc, context);
        }

        x+=50+90-15;

        RenderUtils.setupBlend();
        Tessellator atessellator = Tessellator.getInstance();
        BufferBuilder abuffer = atessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.applyModelViewMatrix();
        abuffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        RenderUtils.bindTexture(TEXTURE_HOPPER);
        RenderUtils.drawTexturedRectBatched(x - 90 + 14, y,  4,   0,  180,   6, abuffer); // top (right)
        RenderUtils.drawTexturedRectBatched(x - 90 + 14, y+6,  4,  50,  177,  130, abuffer); // middle - 90 + 7

        atessellator.draw();

        x -= 90-18;
        y += 7;


        InventoryOverlay.renderInventoryStacks(InventoryRenderType.FIXED_27, inventory, x, y,9,9, 27, mc, context);
        y += 58;
        InventoryOverlay.renderInventoryStacks(InventoryRenderType.FIXED_27, inventory, x, y,9,0, 9, mc, context);

        RenderSystem.applyModelViewMatrix();
    }

    private ItemStack getEquippedStack(EquipmentSlot slot, Inventory inv){
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
}
