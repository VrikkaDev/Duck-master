package net.VrikkaDuck.duck.config.client;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import net.VrikkaDuck.duck.config.client.options.admin.DuckConfigDouble;
import net.VrikkaDuck.duck.config.client.options.admin.DuckConfigLevel;
import net.VrikkaDuck.duck.config.common.ServerConfigs;
import net.VrikkaDuck.duck.event.ClientBlockHitHandler;
import net.VrikkaDuck.duck.event.ClientEntityHitHandler;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.packet.AdminPacket;
import net.VrikkaDuck.duck.render.gui.config.ConfigGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

public class Callbacks {

    private static final ClientBlockHitHandler blockHitHandler = ClientBlockHitHandler.INSTANCE();
    private static final ClientEntityHitHandler entityHitHandler = ClientEntityHitHandler.INSTANCE();
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static class AdminLevelCallback implements IValueChangeCallback<DuckConfigLevel>
    {
        @Override
        public void onValueChanged(DuckConfigLevel config)
        {
            NbtCompound compound = new NbtCompound();
            compound.putBoolean("request", false);
            compound.put("options", Configs.Admin.getAsNbtList());
            AdminPacket.AdminC2SPacket packet = new AdminPacket.AdminC2SPacket(MinecraftClient.getInstance().player.getUuid(), compound);
            NetworkHandler.Client.SendToServer(packet);
        }
    }
    public static class AdminDoubleCallback implements IValueChangeCallback<DuckConfigDouble>
    {
        @Override
        public void onValueChanged(DuckConfigDouble config)
        {
            NbtCompound compound = new NbtCompound();
            compound.putBoolean("request", false);
            compound.put("options", Configs.Admin.getAsNbtList());
            AdminPacket.AdminC2SPacket packet = new AdminPacket.AdminC2SPacket(MinecraftClient.getInstance().player.getUuid(), compound);
            NetworkHandler.Client.SendToServer(packet);
        }
    }


    public static void setCallbacks(){
        KeyCallbackHotkeysGeneric callbackGeneric = new KeyCallbackHotkeysGeneric();
        AdminLevelCallback adminCallback = new AdminLevelCallback();
        AdminDoubleCallback adminDoubleCallback = new AdminDoubleCallback();

        for(IConfigBase base : Configs.Admin.DEFAULT_OPTIONS){
           if(base instanceof DuckConfigLevel){
               ((DuckConfigLevel)base).setValueChangeCallback(adminCallback);
           }else if(base instanceof DuckConfigDouble){
               ((DuckConfigDouble)base).setValueChangeCallback(adminDoubleCallback);
           }
        }
        Hotkeys.OPEN_CONFIG_GUI.getKeybind().setCallback(callbackGeneric);
        Configs.Generic.INSPECT_CONTAINER.getKeybind().setCallback(callbackGeneric);
        Configs.Generic.INSPECT_PLAYER_INVENTORY.getKeybind().setCallback(callbackGeneric);
    }
    private static class KeyCallbackHotkeysGeneric implements IHotkeyCallback
    {
        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            if (key == Hotkeys.OPEN_CONFIG_GUI.getKeybind())
            {
                ServerConfigs.refreshFromServer();
                GuiBase.openGui(new ConfigGui());
                return true;
            }
            return false;
        }
    }
}

