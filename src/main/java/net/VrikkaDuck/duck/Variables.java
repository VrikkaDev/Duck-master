package net.VrikkaDuck.duck;

import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Variables {
    public static final Logger LOGGER = LogManager.getLogger("duck");
    public static final int PERMISSIONLEVEL = 4;
    public static final String MODNAME = "duck";
    public static final String MODID = "duck";
    public static final String MODVERSION = StringUtils.getModVersionString(MODID);
    public static final Identifier GENERICID = new Identifier("vrikkaduck", "duckgeneric");
    public static final Identifier ADMINID = new Identifier("vrikkaduck", "duckadmin");
    public static final Identifier ADMINSETID = new Identifier("vrikkaduck", "duckadminset");
    public static final Identifier ACTIONID = new Identifier("vrikkaduck", "duckaction");
}