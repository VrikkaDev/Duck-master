package net.VrikkaDuck.duck.config.client.options.admin;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.options.ConfigBase;
import net.VrikkaDuck.duck.Variables;
import net.minecraft.util.math.MathHelper;

public class DuckConfigDouble extends ConfigBase<DuckConfigDouble> implements IConfigValue {

    private double value = 0;
    private final double defaultValue;

    public DuckConfigDouble(String name, double defaultValue, String comment) {
        super(ConfigType.DOUBLE, name, comment);
        this.defaultValue = defaultValue;
    }

    public double getDoubleValue() {
        return value;
    }

    public double getDefaultDoubleValue() {
        return defaultValue;
    }

    public void setDoubleValue(double value) {
        double oldValue = this.value;
        this.value = getClampedValue(value);

        if (oldValue != this.value)
        {
            this.onValueChanged();
        }
    }
    public void setDoubleValueWithoutEvent(double value) {
        this.value = getClampedValue(value);
    }
    protected double getClampedValue(double value)
    {
        return MathHelper.clamp(value, this.getMinDoubleValue(), this.getMaxDoubleValue());
    }

    public double getMinDoubleValue() {
        return 0;
    }

    public double getMaxDoubleValue() {
        return 100;
    }

    @Override
    public String getConfigGuiDisplayName() {
        return super.getConfigGuiDisplayName();
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        try
        {
            if (element.isJsonPrimitive())
            {
                String value = element.getAsString();
                this.value = value.isEmpty() ? getDefaultDoubleValue() : Double.parseDouble(value);
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
        return new JsonPrimitive(this.value);
    }

    @Override
    public boolean isModified() {
        return value != defaultValue;
    }

    @Override
    public void resetToDefault() {
        setDoubleValue(defaultValue);
    }

    @Override
    public String getDefaultStringValue() {
        return String.valueOf(this.defaultValue);
    }

    @Override
    public void setValueFromString(String value) {
        this.setDoubleValue(Double.parseDouble(value));
    }

    @Override
    public boolean isModified(String newValue) {
        return !newValue.isEmpty() && Double.parseDouble(newValue) != this.defaultValue;
    }

    @Override
    public String getStringValue() {
        return String.valueOf(value);
    }
}
