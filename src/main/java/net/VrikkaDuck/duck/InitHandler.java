package net.VrikkaDuck.duck;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import net.VrikkaDuck.duck.config.Configs;
import net.VrikkaDuck.duck.event.ClientTickHandler;
import net.VrikkaDuck.duck.input.KeyboardHandler;

public class InitHandler implements IInitializationHandler {
    @Override
    public void registerModHandlers()
    {
        KeyboardHandler keyboardHandler = new KeyboardHandler();

        InputEventHandler.getKeybindManager().registerKeybindProvider(keyboardHandler);
        InputEventHandler.getInputManager().registerKeyboardInputHandler(keyboardHandler);

        TickHandler.getInstance().registerClientTickHandler(new ClientTickHandler());

        ConfigManager.getInstance().registerConfigHandler(Variables.MODID, new Configs());
    }
}
