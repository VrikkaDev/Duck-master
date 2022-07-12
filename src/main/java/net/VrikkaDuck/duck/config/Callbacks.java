package net.VrikkaDuck.duck.config;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IValueChangeCallback;
import io.netty.buffer.Unpooled;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.event.ClientNetworkHandler;
import net.VrikkaDuck.duck.gui.ConfigGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.checkerframework.checker.units.qual.A;

public class Callbacks {

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
            }
            return false;
        }
    }
}

