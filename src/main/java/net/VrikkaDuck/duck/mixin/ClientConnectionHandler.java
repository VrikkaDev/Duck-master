package net.VrikkaDuck.duck.mixin;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.config.ServerConfigs;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.VrikkaDuck.duck.event.ClientNetworkHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientConnectionHandler {
    @Inject(at = @At("RETURN"), method = "onCustomPayload")
    private void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo cb){
        if(packet.getChannel().equals(Variables.GENERICID)){
            this.processNbt(packet.getData().readNbt());
        }else if(packet.getChannel().equals(Variables.ADMINID)){
            this.processAdminNbt(packet.getData().readNbt());
        }else if(packet.getChannel().equals(Variables.ADMINSETID)){
            ClientNetworkHandler.refreshAdmin();
        }else if(packet.getChannel().equals(Variables.ACTIONID)){
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

        PacketByteBuf buf = PacketByteBufs.slice(packet.getData());
        PacketTypes type = PacketType.identifierToType(buf.readIdentifier());

        switch (type){
            case SHULKER:
                NbtCompound tnbt = buf.readNbt();
                NbtCompound nbt = new NbtCompound();
                nbt.put ("BlockEntityTag", tnbt);
                ItemStack stc = new ItemStack(Items.WHITE_SHULKER_BOX);
                stc.setNbt(nbt);
                Configs.Actions.SHULKER_ITEM_STACK = stc;
                Configs.Actions.RENDER_SHULKER_TOOLTIP = true;
                break;
            default:
                Variables.LOGGER.error("Could not get viable PacketType");
                break;
        }
    }
    @Inject(at = @At("RETURN"), method = "onGameJoin")
    private void onJoin(CallbackInfo ci){
        ServerConfigs.loadFromFile();
    }
}
