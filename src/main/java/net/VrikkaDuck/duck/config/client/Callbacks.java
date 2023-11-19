package net.VrikkaDuck.duck.config.client;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import net.VrikkaDuck.duck.config.client.options.admin.DuckConfigDouble;
import net.VrikkaDuck.duck.config.client.options.admin.DuckConfigLevel;
import net.VrikkaDuck.duck.config.client.options.generic.DuckConfigHotkeyToggleable;
import net.VrikkaDuck.duck.event.ClientBlockHitHandler;
import net.VrikkaDuck.duck.event.ClientNetworkHandler;
import net.VrikkaDuck.duck.gui.ConfigGui;
import net.minecraft.client.MinecraftClient;

public class Callbacks {

    private static ClientBlockHitHandler blockHitHandler = ClientBlockHitHandler.INSTANCE();
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static class AdminLevelCallback implements IValueChangeCallback<DuckConfigLevel>
    {
        @Override
        public void onValueChanged(DuckConfigLevel config)
        {
            ClientNetworkHandler.setAdminBoolean(config.getName(), config.getBooleanValue(), config.getPermissionLevel());
        }
    }
    public static class AdminDoubleCallback implements IValueChangeCallback<DuckConfigDouble>
    {
        @Override
        public void onValueChanged(DuckConfigDouble config)
        {
            ClientNetworkHandler.setAdminDouble(config.getName(), config.getDoubleValue());
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
        Configs.Generic.INSPECT_FURNACE.getKeybind().setCallback(callbackGeneric);
        Configs.Generic.INSPECT_BEEHIVE.getKeybind().setCallback(callbackGeneric);
        Configs.Generic.INSPECT_PLAYER_INVENTORY.getKeybind().setCallback(callbackGeneric);
    }
    private static class KeyCallbackHotkeysGeneric implements IHotkeyCallback
    {
        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key)
        {
            if (key == Hotkeys.OPEN_CONFIG_GUI.getKeybind())
            {
                ClientNetworkHandler.refreshAdmin();
                GuiBase.openGui(new ConfigGui());
                return true;
            }else if(key == Configs.Generic.INSPECT_CONTAINER.getKeybind()){
                blockHitHandler.reload();
            }else if(key == Configs.Generic.INSPECT_FURNACE.getKeybind()){
                blockHitHandler.reload();
            }else if (key == Configs.Generic.INSPECT_BEEHIVE.getKeybind()){
                blockHitHandler.reload();
            }else if (key == Configs.Generic.INSPECT_PLAYER_INVENTORY.getKeybind()){
                blockHitHandler.lookingNewEntity(mc.targetedEntity);
            }
            return false;
        }
    }
}

