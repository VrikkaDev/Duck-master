package net.VrikkaDuck.duck.render.gui.inventory.blockentity;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.render.InventoryOverlay;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.VrikkaDuck.duck.util.GuiRenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBook;
import net.minecraft.screen.CrafterScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CrafterInputSlot;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fi.dy.masa.malilib.render.InventoryOverlay.TEXTURE_FURNACE;

public class CrafterInventoryRenderer {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Identifier DISABLED_SLOT_TEXTURE = new Identifier("container/crafter/disabled_slot");
    private static final Identifier POWERED_REDSTONE_TEXTURE = new Identifier("container/crafter/powered_redstone");
    private static final Identifier UNPOWERED_REDSTONE_TEXTURE = new Identifier("container/crafter/unpowered_redstone");
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/crafter.png");

    private static final RecipeManager recipeManager = new RecipeManager();

    private final int width = 120, height = 50;
    private final int backgroundWidth = 120, backgroundHeight = 60;

    public CrafterInventoryRenderer(){
    }

    public void render(NbtCompound nbt, int baseX, int baseY, DrawContext context){

        baseX -= backgroundWidth/2;
        baseY -= backgroundHeight/2+30;

        GuiRenderUtils.renderBackground(baseX, baseY, backgroundWidth, backgroundHeight);

        NbtCompound _n = nbt.getCompound("BlockEntityTag");

        NbtList _itemlist = _n.getList("Items", NbtList.COMPOUND_TYPE);
        int[] _disabled = _n.getIntArray("disabled_slots");
        List<Integer> disabledList = Arrays.stream(_disabled)
                .boxed().toList();;

        for(int i = 0; i < 9; i++) {

            int __x = i % 3;
            int __y = Math.floorDiv(i, 3);

            drawSlot(context, baseX + (__x * 18) + 10, baseY + (__y * 18) + height/2 - 20 + 2, !disabledList.contains(i));

            for (int j = 0; j < _itemlist.size(); j++){
                NbtCompound __n = _itemlist.getCompound(j);
                byte slot = __n.getByte("Slot");

                if ((int)slot == i){
                    ItemStack st = ItemStack.fromNbt(__n);
                    InventoryOverlay.renderStackAt(st, baseX + (__x * 18) + 10, baseY + (__y * 18) + height/2 - 20 + 2, 1, mc, context);
                }
            }

        }

        this.drawArrowTexture(context, baseX, baseY+50-3, _n.getBoolean("triggered"));
        this.drawResult(context, _n, baseX + width - 30, baseY + 20);
    }

    private void drawSlot(DrawContext context, int x, int y, boolean isenabled) {
        if (!isenabled) {
            this.drawDisabledSlot(context, x, y);
            return;
        }
        context.drawTexture(TEXTURE, x-1, y-1, 61, 16, 18, 18);
    }

    private void drawDisabledSlot(DrawContext context, int x, int y) {
        context.drawGuiTexture(DISABLED_SLOT_TEXTURE, x - 1, y - 1, 18, 18);
    }

    private void drawArrowTexture(DrawContext context, int x, int y, boolean triggered) {
        int i = this.width / 2 + 9 + x;
        int j = this.height / 2 - 48 + y;
        Identifier identifier;
        if (triggered) {
            identifier = POWERED_REDSTONE_TEXTURE;
        } else {
            identifier = UNPOWERED_REDSTONE_TEXTURE;
        }
        context.drawGuiTexture(identifier, i, j, 16, 16);
    }

    private void drawResult(DrawContext context, NbtCompound nbt, int x, int y) {
        context.drawTexture(TEXTURE, x, y, 129, 30, 26, 26);
        ItemStack st = ItemStack.fromNbt(nbt.getCompound("result"));
        InventoryOverlay.renderStackAt(st, x+5, y+5, 1, mc, context);
    }
}
