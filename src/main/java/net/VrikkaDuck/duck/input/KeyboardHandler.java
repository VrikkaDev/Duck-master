package net.VrikkaDuck.duck.input;

import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.IKeyboardInputHandler;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Callbacks;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.config.client.Hotkeys;

public class KeyboardHandler implements IKeybindProvider ,IKeyboardInputHandler{

    public KeyboardHandler(){
        super();
        Callbacks.setCallbacks();
    }

    @Override
    public void addKeysToMap(IKeybindManager manager) {
        for (IHotkey hotkey : Hotkeys.HOTKEYS)
        {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
        for (IHotkey hotkey : Configs.Generic.CONFIG_HOTKEYS)
        {
            manager.addKeybindToMap(hotkey.getKeybind());
        }
    }

    @Override
        public void addHotkeys(IKeybindManager manager) {
        manager.addHotkeysForCategory(Variables.MODNAME, "duck.hotkeys.category.generic_hotkeys", Configs.Generic.CONFIG_HOTKEYS);
        manager.addHotkeysForCategory(Variables.MODNAME, "duck.hotkeys.category.generic_hotkeys", Hotkeys.HOTKEYS);
    }

    @Override
    public boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)
    {
        return false;
    }
}
