package net.VrikkaDuck.duck.config.common;

import com.google.gson.JsonElement;
import net.VrikkaDuck.duck.util.PermissionLevel;

public interface IServerLevel{
    default boolean getBooleanValue(){return false;}
    default boolean getDefaultBooleanValue(){return false;}
    default double getDoubleValue(){return 0;}
    default double getDefaultDoubleValue(){return 0;}

    default void setBooleanValue(boolean value){}
    default void setDoubleValue(double value){}

    String getName();

    default void toggleBooleanValue() {this.setBooleanValue(! this.getBooleanValue());}

    default void setPermissionLevel(int level){}

    default void togglePermissionLevel(){this.setPermissionLevel(PermissionLevel.nextInt(this.getPermissionLevel()));};

    default int getPermissionLevel(){return 0;}

    default int getDefaultPermissionLevel(){return 0;}

    JsonElement getAsJsonElement();

    void setValueFromJsonElement(JsonElement jsonElement);
}
