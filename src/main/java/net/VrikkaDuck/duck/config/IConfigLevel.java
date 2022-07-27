package net.VrikkaDuck.duck.config;

import fi.dy.masa.malilib.config.IConfigValue;
import net.VrikkaDuck.duck.util.PermissionLevel;

public interface IConfigLevel extends IConfigValue
{
    boolean getBooleanValue();

    boolean getDefaultBooleanValue();

    void setBooleanValue(boolean value);

    default void toggleBooleanValue() {this.setBooleanValue(! this.getBooleanValue());}

    void setPermissionLevel(int level);

    default void togglePermissionLevel(){this.setPermissionLevel(PermissionLevel.nextInt(this.getPermissionLevel()));};

    int getPermissionLevel();

    int getDefaultPermissionLevel();
}
