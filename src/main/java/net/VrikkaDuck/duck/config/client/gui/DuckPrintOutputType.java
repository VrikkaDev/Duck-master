package net.VrikkaDuck.duck.config.client.gui;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.MessageOutputType;
import fi.dy.masa.malilib.util.StringUtils;

public enum DuckPrintOutputType implements IConfigOptionListEntry {
    NONE("none","duck.label.debug.print_output_type.none"),
    MESSAGE("message","duck.label.debug.print_output_type.message"),
    CONSOLE("console", "duck.label.debug.print_output_type.console");

    public static final ImmutableList<DuckPrintOutputType> VALUES = ImmutableList.copyOf(values());

    private final String configString;
    private final String translationKey;

    DuckPrintOutputType(String configString, String translationKey)
    {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward)
    {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public DuckPrintOutputType fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static DuckPrintOutputType fromStringStatic(String name)
    {
        for (DuckPrintOutputType val : VALUES)
        {
            if (val.configString.equalsIgnoreCase(name))
            {
                return val;
            }
        }

        return DuckPrintOutputType.NONE;
    }
}
