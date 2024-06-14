package net.VrikkaDuck.duck.render.gui.inventory.blockentity;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.render.RenderUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.debug.DebugPrinter;
import net.VrikkaDuck.duck.util.DuckModUtils;
import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

public class BrewingInventoryRenderer {

    private static final Identifier FUEL_LENGTH_TEXTURE = new Identifier("container/brewing_stand/fuel_length");
    private static final Identifier BREW_PROGRESS_TEXTURE = new Identifier("container/brewing_stand/brew_progress");
    private static final Identifier BUBBLES_TEXTURE = new Identifier("container/brewing_stand/bubbles");
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/brewing_stand.png");
    private static final int[] BUBBLE_PROGRESS = new int[]{29, 24, 20, 16, 11, 6, 0};
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public BrewingInventoryRenderer(){
    }

    public void render(NbtCompound nbt, int baseX, int baseY, DrawContext context){
        //DebugPrinter.DebugPrint(nbt, true);

        baseX = baseX - 128+42;
        baseY = baseY - 46;


        GuiRenderUtils.renderBackground(baseX, baseY, 128+41, 50);

        context.drawTexture(TEXTURE, baseX, baseY-37, 0, 0, 256, 80);

        baseY -= 37;

        NbtCompound betag = nbt.getCompound("BlockEntityTag");

        int fuel = betag.getByte("Fuel");
        int l = MathHelper.clamp((18 * fuel + 20 - 1) / 20, 0, 18);
        if (l > 0) {
            context.drawGuiTexture(FUEL_LENGTH_TEXTURE, 18, 4, 0, 0, baseX + 60, baseY + 44, l, 4);
        }

        NbtList il = betag.getList("Items", NbtElement.COMPOUND_TYPE);
        boolean exists = getSlot(il, 3) != null;




        if (exists){
            int m = betag.getShort("BrewTime");
            if (m > 0) {
                int n = (int) (28.0F * (1.0F - (float) m / 400.0F));
                if (n > 0) {
                    context.drawGuiTexture(BREW_PROGRESS_TEXTURE, 9, 28, 0, 0, baseX + 97, baseY + 16, 9, n);
                }

                n = BUBBLE_PROGRESS[m / 2 % 7];
                if (n > 0) {
                    context.drawGuiTexture(BUBBLES_TEXTURE, 12, 29, 0, 29 - n, baseX + 63, baseY + 14 + 29 - n, 12, n);
                }
            }
        }

        /*
        56, 51)
        79, 58)
        102, 51
         */
        ImmutableList<Vec2f> coordlist = ImmutableList.of(
                new Vec2f(56, 51),
                new Vec2f(79, 58),
                new Vec2f(102, 51),
                new Vec2f(79, 17),
                new Vec2f(17, 17)
        );

        for (int i = 0; i <= 4; i++){
            NbtCompound pn = getSlot(il, i);
            if (pn == null){
                continue;
            }
            ItemStack p = ItemStack.fromNbt(pn);
            InventoryOverlay.renderStackAt(p, baseX + (int) coordlist.get(i).x, baseY + (int) coordlist.get(i).y, 1, mc, context);
        }

    }
    private static NbtCompound getSlot(NbtList lst, int slot){
        for (NbtElement item : lst){
            NbtCompound nbtc = (NbtCompound) item;
            int sl = nbtc.getByte("Slot");
            if (sl == slot){
                return nbtc;
            }
        }
        return null;
    }
}
