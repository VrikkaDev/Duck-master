package net.VrikkaDuck.duck;

import net.VrikkaDuck.duck.debug.DebugProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Variables {
    public static final Logger LOGGER = LogManager.getLogger("duck");
    public static final int PERMISSIONLEVEL = 4;
    public static final String MODNAME = "duck";
    public static final String MODID = "duck";
    public static final String MODVERSION = "1.0.9+b1";

    // DEBUG
    public static final Boolean DEBUG = true;
    public static DebugProfiler PROFILER = new DebugProfiler(100);
}
