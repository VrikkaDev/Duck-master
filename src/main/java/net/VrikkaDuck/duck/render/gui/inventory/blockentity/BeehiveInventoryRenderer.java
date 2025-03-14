package net.VrikkaDuck.duck.render.gui.inventory.blockentity;

import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;

public class BeehiveInventoryRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public BeehiveInventoryRenderer(){
    }
    public void render(NbtCompound nbt, int baseX, int baseY, String worldname, DrawContext context){

        context.drawText(mc.textRenderer, worldname, baseX-30, baseY-40, 0xFFFFFF, true);

        GuiRenderUtils.renderBackground(baseX-33, baseY-31, 61, 31);

        String honeyCount = nbt.contains("HoneyLevel") ? String.valueOf(nbt.getInt("HoneyLevel")) : "0";
        String beeCount = nbt.contains("BeeCount") ? String.valueOf(nbt.getInt("BeeCount")) : "0";

        context.drawText(mc.textRenderer, "Honey: " + honeyCount, baseX-28, baseY-22,5, false);
        context.drawText(mc.textRenderer, "Bees: " + beeCount, baseX-28, baseY-10,5, false);
    }
}
