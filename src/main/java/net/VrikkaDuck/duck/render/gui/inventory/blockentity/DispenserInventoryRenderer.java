package net.VrikkaDuck.duck.render.gui.inventory.blockentity;

import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class DispenserInventoryRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public DispenserInventoryRenderer(){
    }

    public void render(ItemStack stack, int x, int y, DrawContext context){

        DefaultedList<ItemStack> items = InventoryUtils.getStoredItems(stack, -1);

        Inventory inv = fi.dy.masa.malilib.util.InventoryUtils.getAsInventory(items);

        InventoryOverlay.renderInventoryBackground(InventoryOverlay.InventoryRenderType.DISPENSER, x, y, 3, 9, mc);
        InventoryOverlay.renderInventoryStacks(InventoryOverlay.InventoryRenderType.DISPENSER, inv, x + 8, y + 8, 3, 0, 9 ,mc, context);
    }
}
