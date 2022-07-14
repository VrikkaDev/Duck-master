package net.VrikkaDuck.duck.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.VrikkaDuck.duck.Variables;

public class ServerBoolean {
    private final boolean defaultValue;
    private boolean value;
    private final String name;

    public ServerBoolean(String name, boolean defaultValue)
    {
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.name = name;
    }
    public boolean getBooleanValue()
    {
        return this.value;
    }
    public void setBooleanValue(boolean value){this.value = value;}
    public boolean getDefaultBooleanValue()
    {
        return this.defaultValue;
    }
    public String getName(){return this.name;}
    public void setValueFromJsonElement(JsonElement element)
    {
        try
        {
            if (element.isJsonPrimitive())
            {
                this.value = element.getAsBoolean();
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
    public JsonElement getAsJsonElement()
    {
        return new JsonPrimitive(this.value);
    }
}
