package net.VrikkaDuck.duck.config.options;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.IServerLevel;
import net.VrikkaDuck.duck.util.PermissionLevel;

public class ServerDouble implements IServerLevel {
    private final double defaultValue;
    private double value;
    private final String name;

    public ServerDouble(String name, double defaultValue){this(name,defaultValue,name);}

    public ServerDouble(String name, double defaultValue ,String comment) {this(name, defaultValue, comment, name);}

    public ServerDouble(String name, double defaultValue, String comment, String prettyName) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }


    @Override
    public String getName(){
        return this.name;
    }


    public double getDoubleValue() {
        return this.value;
    }

    public double getDefaultDoubleValue() {
        return this.defaultValue;
    }

    public void setDoubleValue(double value) {
        this.value = value;
    }

    public void setValueFromJsonElement(JsonElement element) {
        try
        {
            if (element.isJsonPrimitive())
            {
                String value = element.getAsString();
                this.value = value.isEmpty() ? this.getDefaultDoubleValue() : Double.parseDouble(value);
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
        return new JsonPrimitive(this.value);
    }

    public boolean isModified() {
        return this.value != this.defaultValue;
    }

    public void resetToDefault() {
        this.setDoubleValue(this.defaultValue);
    }

    public String getDefaultStringValue() {
        return String.valueOf(this.defaultValue);
    }

    public void setValueFromString(String value) {
        this.setDoubleValue(Double.parseDouble(value));
    }

    public boolean isModified(String newValue) {
        return Double.parseDouble(newValue) != this.defaultValue;
    }
    public String getStringValue() {
        return String.valueOf(this.value);
    }
}
