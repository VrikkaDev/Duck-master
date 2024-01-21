package net.VrikkaDuck.duck.config.client.options.generic;

import com.google.gson.JsonElement;
import fi.dy.masa.malilib.MaLiLib;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.StringUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.options.IDuckOption;

public class DuckConfigHotkey extends ConfigBase<DuckConfigHotkey> implements IHotkey, IDuckOption
{
    private final IKeybind keybind;

    public DuckConfigHotkey(String name, String defaultStorageString, String comment)
    {
        this(name, defaultStorageString, comment, name);
    }

    public DuckConfigHotkey(String name, String defaultStorageString, KeybindSettings settings, String comment)
    {
        this(name, defaultStorageString, settings, comment, StringUtils.splitCamelCase(name));
    }

    public DuckConfigHotkey(String name, String defaultStorageString, String comment, String prettyName)
    {
        this(name, defaultStorageString, KeybindSettings.DEFAULT, comment, prettyName);
    }

    public DuckConfigHotkey(String name, String defaultStorageString, KeybindSettings settings, String comment, String prettyName)
    {
        super(ConfigType.HOTKEY, name, comment, prettyName);

        this.keybind = KeybindMulti.fromStorageString(defaultStorageString, settings);
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.keybind;
    }

    @Override
    public String getStringValue()
    {
        return this.keybind.getStringValue();
    }

    @Override
    public String getDefaultStringValue()
    {
        return this.keybind.getDefaultStringValue();
    }

    @Override
    public void setValueFromString(String value)
    {
        this.keybind.setValueFromString(value);
    }

    @Override
    public boolean isModified()
    {
        return this.keybind.isModified();
    }

    @Override
    public boolean isModified(String newValue)
    {
        return this.keybind.isModified(newValue);
    }

    @Override
    public void resetToDefault()
    {
        this.keybind.resetToDefault();
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonObject())
            {
                this.keybind.setValueFromJsonElement(element);
            }
            else if (element.isJsonPrimitive())
            {
                this.keybind.setValueFromString(element.getAsString());
            }
            else
            {
                Variables.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element);
            }
        }
        catch (Exception e)
        {
            Variables.LOGGER.warn("Failed to set config value for '{}' from the JSON element '{}'", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        return this.keybind.getAsJsonElement();
    }

    @Override
    public boolean canDisable() {
        return false;
    }
}
