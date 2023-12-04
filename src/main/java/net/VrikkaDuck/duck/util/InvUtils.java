package net.VrikkaDuck.duck.util;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

public class InvUtils {

    // Optimizations: so use the last calculated inventory if its the same
    private static Inventory lastCalcInv;
    private static NbtCompound nbtCompound;
    public static Inventory fromNbt(NbtCompound nbt){

        if(nbt == nbtCompound){
            return lastCalcInv;
        }

        NbtCompound invnbt = nbt;
        NbtList invList = invnbt.getList("Inventory", 10);
        DefaultedList<ItemStack> itemsasstack = DefaultedList.ofSize(121, new ItemStack(Items.AIR));
        for (NbtElement a : invList) {
            ItemStack sst = ItemStack.fromNbt((NbtCompound) a);
            if (((NbtCompound) a).getByte("Slot") == -106) {
                itemsasstack.set(120, sst);
            } else {
                itemsasstack.set(((NbtCompound) a).getByte("Slot"), sst);
            }
        }

        SimpleInventory inv = new SimpleInventory(itemsasstack.size());

        for (int slot = 0; slot < itemsasstack.size(); ++slot)
        {
            inv.setStack(slot, itemsasstack.get(slot));
        }

        nbtCompound = nbt;
        lastCalcInv = inv;

        return inv;
    }
}
