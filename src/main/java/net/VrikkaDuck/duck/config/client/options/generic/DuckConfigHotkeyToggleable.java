package net.VrikkaDuck.duck.config.client.options.generic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.IConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;
import net.VrikkaDuck.duck.Variables;

public class DuckConfigHotkeyToggleable extends ConfigBase<DuckConfigHotkeyToggleable> implements IHotkey, IConfigBoolean {
    private final IKeybind keybind;
    private boolean active = false;
    private boolean activeDefault = false;

    public DuckConfigHotkeyToggleable(String name, boolean defaultValue, String defaultStorageString, String comment)
    {
        this(name, defaultValue, defaultStorageString, comment, name);
    }

    public DuckConfigHotkeyToggleable(String name, boolean defaultValue, String defaultStorageString, KeybindSettings settings, String comment)
    {
        this(name, defaultValue, defaultStorageString, settings, comment, StringUtils.splitCamelCase(name));
    }

    public DuckConfigHotkeyToggleable(String name, boolean defaultValue, String defaultStorageString, String comment, String prettyName)
    {
        this(name, defaultValue, defaultStorageString, KeybindSettings.DEFAULT, comment, prettyName);
    }

    public DuckConfigHotkeyToggleable(String name, boolean defaultValue, String defaultStorageString, KeybindSettings settings, String comment, String prettyName)
    {
        super(null, name, comment, prettyName);

        this.keybind = KeybindMulti.fromStorageString(defaultStorageString, settings);
        this.active = defaultValue;
        this.activeDefault = defaultValue;
    }

    public boolean isKeybindHeld(){
        return this.getBooleanValue() && this.getKeybind().isKeybindHeld();
    }

    @Override
    public IKeybind getKeybind()
    {
        return this.keybind;
    }

    @Override
    public String getStringValue()
    {
        return this.keybind.getStringValue() + "&" + getBooleanValue();
    }

    @Override
    public String getDefaultStringValue()
    {
        return this.keybind.getDefaultStringValue() + "&" + getDefaultBooleanValue();
    }

    @Override
    public void setValueFromString(String value)
    {
        String[] values = value.split("&");
        this.keybind.setValueFromString(values[0]);
        this.active = Boolean.parseBoolean(values[1]);
    }

    @Override
    public boolean isModified()
    {
        return this.keybind.isModified() || this.getBooleanValue() != this.getDefaultBooleanValue();
    }

    @Override
    public boolean isModified(String newValue)
    {
        String[] values = newValue.split("&");
        return this.keybind.isModified(values[0]) || Boolean.parseBoolean(values[1]) != getDefaultBooleanValue();
    }

    @Override
    public void resetToDefault()
    {
        this.keybind.resetToDefault();
        this.active = activeDefault;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                JsonObject obj = element.getAsJsonObject();

                if(JsonUtils.hasObject(obj, "keybind")){
                    this.keybind.setValueFromJsonElement(obj.get("keybind"));
                }
                if(JsonUtils.hasBoolean(obj, "active")){
                    this.active = JsonUtils.getBoolean(obj, "active");
                }
            }
        }
        catch (Exception e)
        {
            Variables.LOGGER.warn("Failed to set config value for '{}'  from the JSON element '{}' {}", this.getName(), element, e);
        }
    }

    @Override
    public JsonElement getAsJsonElement()
    {
        JsonObject r = new JsonObject();
        r.add("keybind", this.keybind.getAsJsonElement());
        r.add("active", new JsonPrimitive(this.active));
        return r;
    }

    @Override
    public boolean getBooleanValue() {
        return active;
    }

    @Override
    public boolean getDefaultBooleanValue() {
        return activeDefault;
    }

    @Override
    public void setBooleanValue(boolean value) {
        boolean oldValue = this.active;
        this.active = value;

        if (oldValue != this.active)
        {
            this.onValueChanged();
        }
    }
}
