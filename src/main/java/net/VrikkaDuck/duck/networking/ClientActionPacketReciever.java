package net.VrikkaDuck.duck.networking;

import fi.dy.masa.malilib.network.IPluginChannelHandler;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.Configs;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientActionPacketReciever implements IPluginChannelHandler {
    @Override
    public List<Identifier> getChannels() {
        return List.of(Variables.ACTIONID);
    }

    @Override
    public void onPacketReceived(PacketByteBuf buf) {
        buf.resetReaderIndex();
        this.processActionPacket(buf);
    }
    private void processActionPacket(PacketByteBuf buffer){

        resetAll();

        PacketByteBuf buf = PacketByteBufs.slice(buffer);
        PacketTypes type = PacketType.identifierToType(buf.readIdentifier());

        switch (type){
            case CONTAINER:
                NbtCompound tnbt = buf.readNbt();
                NbtCompound nbt = new NbtCompound();
                nbt.put ("BlockEntityTag", tnbt);
                ItemStack stc = new ItemStack(Items.WHITE_SHULKER_BOX);
                stc.setNbt(nbt);
                if(stc.getNbt() == null || !(stc.getNbt().getCompound("BlockEntityTag").contains("Items")) ||
                        stc.getNbt().getCompound("BlockEntityTag").getList("Items", 10).isEmpty()){
                    NbtCompound n = stc.getNbt();
                    NbtList lst = new NbtList();
                    NbtCompound a =  new NbtCompound();
                    a.put("Count", NbtByte.of((byte) 1));
                    lst.add(0,a);
                    NbtCompound b =  new NbtCompound();
                    b.put("Slot", NbtByte.of((byte) 1));
                    lst.add(1,b);
                    NbtCompound c =  new NbtCompound();
                    c.put("Count", NbtString.of("minecraft:air"));
                    lst.add(2,c);
                    n.getCompound("BlockEntityTag").put("Items", lst);
                    stc.setNbt(n);
                }

                stc.setNbt(nbt);
                Configs.Actions.CONTAINER_ITEM_STACK = stc;
                Configs.Actions.RENDER_CONTAINER_TOOLTIP = true;
                Configs.Actions.RENDER_DOUBLE_CHEST_TOOLTIP = buf.readVarInt();

                break;
            case FURNACE:
                NbtCompound fnbt = buf.readNbt();
                Configs.Actions.RENDER_FURNACE_TOOLTIP = true;
                Configs.Actions.FURNACE_NBT = fnbt;
                break;
            case BEEHIVE:
                NbtCompound beenbt = buf.readNbt();
                Configs.Actions.RENDER_BEEHIVE_PREVIEW = true;
                Configs.Actions.BEEHIVE_NBT = beenbt;
                break;
            case PLAYERINVENTORY:
                NbtCompound invnbt = buf.readNbt();
                NbtList invList = invnbt.getList("Inventory", 10);
                DefaultedList<ItemStack> itemsasstack = DefaultedList.ofSize(121, new ItemStack(Items.AIR));

                for(NbtElement a : invList){
                    ItemStack sst = ItemStack.fromNbt((NbtCompound)a);
                    if(((NbtCompound) a).getByte("Slot") == -106){
                        itemsasstack.set(120, sst);
                    }else{
                        itemsasstack.set(((NbtCompound) a).getByte("Slot"), sst);
                    }
                }
                Configs.Actions.TARGET_PLAYER_INVENTORY = InventoryUtils.getAsInventory(itemsasstack);
                Configs.Actions.RENDER_PLAYER_INVENTORY_PREVIEW = true;
                break;
            default:
                Variables.LOGGER.error("Could not get viable PacketType");
                break;
        }
    }
    private void resetAll(){
        Configs.Actions.RENDER_CONTAINER_TOOLTIP = false;
        Configs.Actions.RENDER_FURNACE_TOOLTIP = false;
        Configs.Actions.RENDER_BEEHIVE_PREVIEW = false;
        Configs.Actions.RENDER_PLAYER_INVENTORY_PREVIEW = false;
    }
}
