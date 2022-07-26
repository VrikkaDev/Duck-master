package net.VrikkaDuck.duck.util;

public class PermissionLevel {
    public static String fromInt(int i){
        if(i == NORMAL){return "NORMAL";}
        if(i == OP){return "OP";}
        return "UNKNOWN";
    }
    public static int nextInt(int currentInt){
        return currentInt == NORMAL ? 4 : 1;
    }
    public static final int NORMAL = 1;
    public static final int OP = 4;
}
