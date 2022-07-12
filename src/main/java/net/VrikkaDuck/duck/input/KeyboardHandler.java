package net.VrikkaDuck.duck.input;

import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.*;
import fi.dy.masa.malilib.util.GuiUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.Callbacks;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.config.Hotkeys;

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
