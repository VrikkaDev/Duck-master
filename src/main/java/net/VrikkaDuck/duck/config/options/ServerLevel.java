package net.VrikkaDuck.duck.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.util.PermissionLevel;

public class ServerLevel {
    private final boolean defaultValue;
    private boolean value;
    private final int levelDefaultValue;
    private int levelValue;
    private final String name;

    public ServerLevel(String name, boolean defaultValue, int defaultIntValue){this(name,defaultValue,defaultIntValue,name);}

    public ServerLevel(String name, boolean defaultValue ,int levelValue,  String comment) {this(name, defaultValue, levelValue, comment, name);}

    public ServerLevel(String name, boolean defaultValue, int levelValue, String comment, String prettyName) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.levelDefaultValue = levelValue;
        this.levelValue = levelValue;
    }

    public String getName(){
        return this.name;
    }

    public boolean getBooleanValue() {
        return this.value;
    }

    public boolean getDefaultBooleanValue() {
        return this.defaultValue;
    }

    public void setBooleanValue(boolean value) {
        //boolean oldValue = this.value;
        this.value = value;

        /*if (oldValue != this.value)
        {
            this.onValueChanged();
        }*/
    }

    public void setPermissionLevel(int level) {
       // int oldValue = this.levelValue;
        this.levelValue = level;

        /*if (oldValue != this.levelValue)
        {
            this.onValueChanged();
        }*/
    }

    public int getPermissionLevel() {
        return this.levelValue;
    }

    public int getDefaultPermissionLevel() {
        return this.levelDefaultValue;
    }

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

    public JsonElement getAsJsonElement() {
        return new JsonPrimitive(this.value + "," + this.levelValue);
    }

    public boolean isModified() {
        return this.value != this.defaultValue || this.levelValue != this.levelDefaultValue;
    }

    public void resetToDefault() {
        this.setBooleanValue(this.defaultValue);
        this.setPermissionLevel(this.levelDefaultValue);
    }

    public String getDefaultStringValue() {
        return this.defaultValue + "," + this.levelDefaultValue;
    }

    public void setValueFromString(String value) {
        String[] values = value.split(",");
        this.setBooleanValue(Boolean.parseBoolean(values[0]));
        this.setPermissionLevel(Integer.valueOf(values[1]));
    }

    public boolean isModified(String newValue) {
        return Boolean.parseBoolean(newValue) != this.defaultValue;
    }
    public String getStringValue() {
        return this.value + "," + this.levelValue;
    }
}
