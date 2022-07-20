package net.VrikkaDuck.duck.config;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import net.VrikkaDuck.duck.Main;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.event.ClientBlockHitHandler;
import net.VrikkaDuck.duck.event.ClientNetworkHandler;
import net.VrikkaDuck.duck.gui.ConfigGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class Callbacks {

    private static ClientBlockHitHandler blockHitHandler = ClientBlockHitHandler.INSTANCE();
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static class AdminFeatureCallback implements IValueChangeCallback<ConfigBoolean>
    {
        @Override
        public void onValueChanged(ConfigBoolean config)
        {
            ClientNetworkHandler.setAdminBoolean(config.getName(), config.getBooleanValue());
        }
    }


    public static void setCallbacks(){
        KeyCallbackHotkeysGeneric callbackGeneric = new KeyCallbackHotkeysGeneric();
        AdminFeatureCallback adminCallback = new AdminFeatureCallback();

        for(IConfigBase base : Configs.Admin.DEFAULT_OPTIONS){
            ((ConfigBoolean)base).setValueChangeCallback(adminCallback);
        }
        Hotkeys.OPEN_CONFIG_GUI.getKeybind().setCallback(callbackGeneric);
        Configs.Generic.INSPECT_CONTAINER.getKeybind().setCallback(callbackGeneric);
        Configs.Generic.INSPECT_FURNACE.getKeybind().setCallback(callbackGeneric);
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
            }
            return false;
        }
    }
}

