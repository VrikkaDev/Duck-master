package net.VrikkaDuck.duck.mixin;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.config.PacketType;
import net.VrikkaDuck.duck.event.ClientNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientConnectionHandler {
    @Inject(at = @At("RETURN"), method = "onCustomPayload")
    public void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo cb){
        Variables.LOGGER.info(packet.getChannel());
        if(packet.getChannel().equals(Variables.GENERICID)){
            this.processNbt(packet.getData().readNbt());
        }else if(packet.getChannel().equals(Variables.ADMINID)){
            this.processAdminNbt(packet.getData().readNbt());
        }else if(packet.getChannel().equals(Variables.ADMINSETID)){
            ClientNetworkHandler.refreshAdmin();
        }else if(packet.getChannel().equals(Variables.ACTIONID)){
            Variables.LOGGER.info(packet.getData().readNbt());
            //Variables.LOGGER.info(packet.getData().readString());
            this.processActionPacket(packet);
        }
    }
    private void processNbt(NbtCompound nbt){

        List<IConfigBase> _list = new ArrayList<>();
        //List<String> _keylist = List.copyOf(nbt.getKeys());
        for(IConfigBase base : Configs.Generic.DEFAULT_OPTIONS){
            if(nbt.getKeys().contains(base.getName())){
                if(nbt.getBoolean(base.getName())){
                    _list.add(base);
                }
            }
        }
        Configs.Generic.OPTIONS = ImmutableList.copyOf(_list);
    }
    private void processAdminNbt(NbtCompound nbt){

        List<IConfigBase> _list = new ArrayList<>();
        //List<String> _keylist = List.copyOf(nbt.getKeys());
        for(IConfigBase base : Configs.Admin.DEFAULT_OPTIONS){
            if(nbt.getKeys().contains(base.getName())){
                ((ConfigBoolean)base).setBooleanValue(nbt.getBoolean(base.getName()));
                _list.add(base);
            }
        }
        Configs.Admin.OPTIONS = ImmutableList.copyOf(_list);
        ClientNetworkHandler.refreshGenericTweaks();
    }
    private void processActionPacket(CustomPayloadS2CPacket packet){
        PacketType type = PacketType.valueOf("SHULKER");
        switch (type){
            case SHULKER:
                NbtCompound tnbt = packet.getData().readNbt();
                NbtCompound nbt = new NbtCompound();
                nbt.put ("BlockEntityTag", tnbt);
                ItemStack stc = new ItemStack(Items.WHITE_SHULKER_BOX);
                stc.setNbt(nbt);
                Configs.Actions.SHULKER_ITEM_STACK = stc;
                Configs.Actions.RENDER_SHULKER_TOOLTIP = true;

                Variables.LOGGER.info(stc);
                Variables.LOGGER.info(stc.getNbt());
                Variables.LOGGER.info(packet.getData().readNbt());
                //DefaultedList<ItemStack> items = InventoryUtils.getStoredItems(Configs.Actions.SHULKER_ITEM_STACK, -1);
                break;
        }
    }
}
