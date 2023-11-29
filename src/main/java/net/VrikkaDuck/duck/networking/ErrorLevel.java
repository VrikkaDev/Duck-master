package net.VrikkaDuck.duck.networking;

public enum ErrorLevel {
    NONE(0),
    INFO(1),
    INFO_INCHAT(2),
    WARN(5),
    ERROR(10);

    public final int value;
    ErrorLevel(int l){
        value = l;
    }

    public static ErrorLevel fromValue(int l){
        for (ErrorLevel e : ErrorLevel.values()){
            if(e.value == l){
                return e;
            }
        }

        return NONE;
    }
}
