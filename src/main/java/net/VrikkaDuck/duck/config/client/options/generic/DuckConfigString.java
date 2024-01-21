package net.VrikkaDuck.duck.config.client.options.generic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.options.IDuckOption;

public class DuckConfigString extends ConfigBase<DuckConfigString> implements IDuckOption {

    public DuckConfigString(String name, String comment)
    {
        super(null, name, comment, name);
    }

    @Override
    public boolean isModified()
    {
        return false;
    }


    @Override
    public void resetToDefault()
    {
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return new JsonObject();
    }

    @Override
    public boolean canDisable() {
        return false;
    }
}
