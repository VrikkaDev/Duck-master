package net.VrikkaDuck.duck.render.gui.inventory.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.GuiUtils;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

import static fi.dy.masa.malilib.render.RenderUtils.*;

public class DoubleChestInventoryRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public DoubleChestInventoryRenderer(){
    }
    public void render(ItemStack stack, int baseX, int baseY, DrawContext context)
    {
        if (stack.hasNbt())
        {
            DefaultedList<ItemStack> items = InventoryUtils.getStoredItems(stack, -1);

            if (items.isEmpty())
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

            color(1f, 1f, 1f, 1f);

            disableDiffuseLighting();

            InventoryOverlay.renderInventoryBackground(type, x, y, props.slotsPerRow, items.size(), mc);

            enableDiffuseLightingGui3D();

            Inventory inv = fi.dy.masa.malilib.util.InventoryUtils.getAsInventory(items);
            InventoryOverlay.renderInventoryStacks(type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1, mc, context);

            RenderSystem.applyModelViewMatrix();
        }
    }
}
