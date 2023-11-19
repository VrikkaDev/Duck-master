package net.VrikkaDuck.duck;

import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Variables {
    public static final Logger LOGGER = LogManager.getLogger("duck");
    public static final int PERMISSIONLEVEL = 4;
    public static final String MODNAME = "duck";
    public static final String MODID = "duck";
    public static final String MODVERSION = "1.0.8+b1";


    // THIS IS HORRIBLE!!!!!!!!!! WHYYYY WHY WHY. PAST ME WHYYHYHY????
    public static final Identifier GENERICID = new Identifier("vrikkaduck", "duckgeneric");
    public static final Identifier ADMINID = new Identifier("vrikkaduck", "duckadmin");
    public static final Identifier ADMINSETID = new Identifier("vrikkaduck", "duckadminset");
    public static final Identifier ACTIONID = new Identifier("vrikkaduck", "duckaction");
    public static final Identifier ERRORID = new Identifier("vrikkaduck", "duckerror");
}
