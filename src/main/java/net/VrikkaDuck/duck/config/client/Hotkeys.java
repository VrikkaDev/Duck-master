package net.VrikkaDuck.duck.config.client;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import net.VrikkaDuck.duck.config.client.options.generic.DuckConfigHotkey;

import java.util.List;

public class Hotkeys {

    public static final DuckConfigHotkey OPEN_CONFIG_GUI = new DuckConfigHotkey("openConfigGui","Y,C","The key open the in-game config GUI");

    public static final List<DuckConfigHotkey> HOTKEYS = ImmutableList.of(

            OPEN_CONFIG_GUI

    );
}
