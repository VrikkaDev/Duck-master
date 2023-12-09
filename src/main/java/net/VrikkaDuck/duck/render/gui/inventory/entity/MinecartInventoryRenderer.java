package net.VrikkaDuck.duck.render.gui.inventory.entity;

import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.GuiUtils;
import net.VrikkaDuck.duck.networking.EntityDataType;
import net.VrikkaDuck.duck.render.gui.inventory.blockentity.HopperInventoryRenderer;
import net.VrikkaDuck.duck.render.gui.inventory.blockentity.ShulkerInventoryRenderer;
import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.Map;

public class MinecartInventoryRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final ShulkerInventoryRenderer shulkerInventoryRenderer = new ShulkerInventoryRenderer();
    private final HopperInventoryRenderer hopperInventoryRenderer = new HopperInventoryRenderer();
    public MinecartInventoryRenderer(){
    }

    public void render(Map.Entry<NbtCompound, EntityDataType> entry, int x, int y, DrawContext context){
        ItemStack cis = new ItemStack(Items.WHITE_SHULKER_BOX);
        cis.setNbt(entry.getKey());

        if(entry.getValue() == EntityDataType.MINECART_CHEST){
            shulkerInventoryRenderer.render(cis, x - 96,
                    y + 30, true, context);
        }else{
            hopperInventoryRenderer.render(cis, x - (52 + 8), y + (16 + 16), context);
        }
    }
}
