package net.VrikkaDuck.duck.render.gui.inventory.blockentity;

import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;

import static fi.dy.masa.malilib.render.InventoryOverlay.renderStackAt;

public class ChiseledBookshelfInventoryRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public ChiseledBookshelfInventoryRenderer(){
    }
    public void render(NbtCompound nbt, int baseX, int baseY, DrawContext context){
        GuiRenderUtils.renderBackground(baseX-250/2, baseY-80, 250, 90);

        if(nbt.isEmpty()){
            return;
        }

        NbtCompound _n = nbt.getCompound("BlockEntityTag");

        NbtList _itemlist = _n.getList("Items", NbtList.COMPOUND_TYPE);

        for(int __i = 0; __i < 6; __i++){
            int x = (__i < 3 ? (__i - 1) * 80 : (__i - 4) * 80) + baseX - 7;
            int y = (__i < 3 ? -70 : -30) + baseY;
            GuiRenderUtils.renderItemSlot(x,y, context);
        }


        for(NbtElement element : _itemlist){

            if(!(element instanceof NbtCompound c)){
                continue;
            }

            int slot = c.getByte("Slot");

            int x = (slot < 3 ? (slot - 1) * 80 : (slot - 4) * 80) + baseX - 7;
            int y = (slot < 3 ? -70 : -30) + baseY;

            ItemStack is = ItemStack.fromNbt(c);

            //GuiRenderUtils.renderItemSlot(x,y, context);
            renderStackAt(is, x, y, 1, mc, context);
            //context.drawItemTooltip(mc.textRenderer, is, x-20, y+10);
            //context.drawTooltip(mc.textRenderer, Text.of("Depth Strider 3").getWithStyle(Style.EMPTY.withColor(Formatting.YELLOW)), x-40, y+40);



            NbtList _l = is.getOrCreateNbt().getList("StoredEnchantments", NbtList.COMPOUND_TYPE);

            List<Text> enchantmentss = new ArrayList<>();

            for(NbtElement __e : _l){
                if(!(__e instanceof NbtCompound __c)){
                    continue;
                }


                if(__c.contains("id")){

                    String __s = "enchantment." + __c.getString("id");
                    __s = __s.replace(":", ".");
                    enchantmentss.add(Text.translatable(__s).append(" ").append(String.valueOf(__c.getInt("lvl"))));
                }
            }

            GuiRenderUtils.renderScaledText(x, y + 19, ColorHelper.Argb.getArgb(255,255,255,255),
                    0.8f, ColorHelper.Argb.getArgb(100, 50,50,50), enchantmentss, context);
        }
    }
}
