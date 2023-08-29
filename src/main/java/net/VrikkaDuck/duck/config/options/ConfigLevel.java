package net.VrikkaDuck.duck.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.options.ConfigBase;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.IConfigLevel;
import net.VrikkaDuck.duck.util.PermissionLevel;

public class ConfigLevel extends ConfigBase<ConfigLevel> implements IConfigLevel {

    private final boolean defaultValue;
    private boolean value;
    private final int levelDefaultValue;
    private int levelValue;

    public ConfigLevel(String name, boolean defaultValue, int defaultIntValue){this(name,defaultValue,defaultIntValue,name);}

    public ConfigLevel(String name, boolean defaultValue ,int levelValue,  String comment) {this(name, defaultValue, levelValue, comment, name);}

    public ConfigLevel(String name, boolean defaultValue, int levelValue, String comment, String prettyName) {
        super(null, name, comment, prettyName);

        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.levelDefaultValue = levelValue;
        this.levelValue = levelValue;
    }

    @Override
    public boolean getBooleanValue() {
        return this.value;
    }

    @Override
    public boolean getDefaultBooleanValue() {
        return this.defaultValue;
    }

    @Override
    public void setBooleanValue(boolean value) {
        boolean oldValue = this.value;
        this.value = value;

        if (oldValue != this.value)
        {
            this.onValueChanged();
        }
    }

    /*public void setBooleanValue(boolean value, boolean internal){
        if (internal){
            this.value = value;
        }else{
            setBooleanValue(value);
        }
    }*/

    @Override
    public void setPermissionLevel(int level) {
        int oldValue = this.levelValue;
        this.levelValue = level;

        if (oldValue != this.levelValue)
        {
            this.onValueChanged();
        }
    }

    /*public void setPermissionLevel(int level, boolean internal){
        if(internal){
            this.levelValue = level;
        }else{
            setPermissionLevel(level);
        }
    }*/

    @Override
    public int getPermissionLevel() {
        return this.levelValue;
    }

    @Override
    public int getDefaultPermissionLevel() {
        return this.levelDefaultValue;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        try
        {
            if (element.isJsonPrimitive())
            {
                String[] values = element.getAsString().split(",");
                this.value = values[0].isEmpty() ? false : Boolean.parseBoolean(values[0]);
                this.levelValue = values[1].isEmpty() ? PermissionLevel.OP : Integer.valueOf(values[1]);
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
    public JsonElement getAsJsonElement() {
        return new JsonPrimitive(this.value + "," + this.levelValue);
    }

    @Override
    public boolean isModified() {
        return this.value != this.defaultValue || this.levelValue != this.levelDefaultValue;
    }

    @Override
    public void resetToDefault() {
        this.setBooleanValue(this.defaultValue);
        this.setPermissionLevel(this.levelDefaultValue);
    }

    @Override
    public String getDefaultStringValue() {
        return this.defaultValue + "," + this.levelDefaultValue;
    }

    @Override
    public void setValueFromString(String value) {
        String[] values = value.split(",");
        this.setBooleanValue(Boolean.parseBoolean(values[0]));
        this.setPermissionLevel(Integer.valueOf(values[1]));
    }

    @Override
    public boolean isModified(String newValue) {
        return Boolean.parseBoolean(newValue) != this.defaultValue;
    }

    @Override
    public String getStringValue() {
        return this.value + "," + this.levelValue;
    }
}
