package net.VrikkaDuck.duck.networking;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.network.ClientPacketChannelHandler;
import fi.dy.masa.malilib.network.IPluginChannelHandler;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.config.options.DuckConfigDouble;
import net.VrikkaDuck.duck.config.options.DuckConfigLevel;
import net.VrikkaDuck.duck.event.ClientNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ClientPacketReciever implements IPluginChannelHandler {
    public ClientPacketReciever(){
        ClientPacketChannelHandler.getInstance().registerClientChannelHandler(this);
        ClientPacketChannelHandler.getInstance().registerClientChannelHandler(new ClientActionPacketReciever());
    }
    @Override
    public List<Identifier> getChannels() {
        return List.of(Variables.ADMINID, Variables.ADMINSETID, Variables.GENERICID, Variables.ERRORID);
    }

    @Override
    public void onPacketReceived(PacketByteBuf buf) {
        int id = buf.readVarInt();
        if(id == 0){//GRNERICID
            this.processNbt(buf.readNbt());
        }else if(id == 1){//ADMINID
            this.processAdminNbt(buf.readNbt());
        }else if(id == 2){//ADMINSETID
            ClientNetworkHandler.refreshAdmin();
        }else if(id == 3){//ERROR_MESSAGE
            Variables.LOGGER.error(buf.readString());
        }
    }
    private void processNbt(NbtCompound nbt){
        List<IConfigBase> _list = new ArrayList<>();
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
        for(IConfigBase base : Configs.Admin.DEFAULT_OPTIONS){
            if(base instanceof DuckConfigDouble sbase){
                ((DuckConfigDouble)base).setDoubleValueWithoutEvent(nbt.getDouble(base.getName()));
                _list.add(base);

                if(sbase.getName().equals("inspectDistance")) {
                    Configs.Actions.INSPECT_DISTANCE = ((DuckConfigDouble) base).getDoubleValue();
                }
                continue;
            }
            if(nbt.getKeys().contains(base.getName())){
                ((DuckConfigLevel)base).setBooleanValue(nbt.getBoolean(base.getName()));
                ((DuckConfigLevel)base).setPermissionLevel(nbt.getInt(base.getName()+",level"));
                _list.add(base);
            }
        }
        Configs.Admin.OPTIONS = ImmutableList.copyOf(_list);
        ClientNetworkHandler.refreshGenericTweaks();
    }
}
