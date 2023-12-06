package net.VrikkaDuck.duck.util;

import net.VrikkaDuck.duck.Variables;

public class DuckModUtils {
    public static boolean isCompatible(String otherVersion){

        int _other = getVersionAsInt(otherVersion);
        if(_other == 0){
            return false;
        }

        int _this = getVersionAsInt(Variables.MODVERSION);

        // this logic changes

        return !isOlderVersionThan(otherVersion, 1080);
    }

    public static boolean isOlderVersionThan(String otherVersion, int version){
        return getVersionAsInt(otherVersion) < version;
    }

    // Get duck version as int. so for example "1.0.9+b1" would return 1091
    public static int getVersionAsInt(String duckVersion){

        if(!duckVersion.contains("b")){
            return 0;
        }

        String[] _s = duckVersion.split("b");

        String _v = _s[0].replace(".", "").replace("+", "");
        String _b = _s[1];

        String _r = _v + _b;

        return Integer.parseInt(_r);
    }
}
