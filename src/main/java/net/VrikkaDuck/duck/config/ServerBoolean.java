package net.VrikkaDuck.duck.config;

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
}
