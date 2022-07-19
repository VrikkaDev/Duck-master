package net.VrikkaDuck.duck.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.*;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.VrikkaDuck.duck.Variables;
import net.minecraft.item.ItemStack;

import java.io.File;

public class Configs implements IConfigHandler {
    private static final String CONFIG_FILE_NAME = Variables.MODID + ".json";

    public static class Generic {
        public static final ConfigHotkey INSPECT_CONTAINER = new ConfigHotkey("inspectContainers", "k", "Inspect Shulkerboxes when placed");
        public static ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(INSPECT_CONTAINER);

        public static final ImmutableList<IConfigBase> DEFAULT_OPTIONS = ImmutableList.of(INSPECT_CONTAINER);
        public static final ImmutableList<ConfigHotkey> CONFIG_HOTKEYS = ImmutableList.of(INSPECT_CONTAINER);
    }

    public static class Admin {
        public static final ConfigBoolean INSPECT_CONTAINER = new ConfigBoolean("inspectContainers", false, "Inspect Shulkerboxes when placed");

        public static ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(INSPECT_CONTAINER);

        public static final ImmutableList<IConfigBase> DEFAULT_OPTIONS = ImmutableList.of(INSPECT_CONTAINER);
    }

    public static class Actions{
        public static boolean RENDER_SHULKER_TOOLTIP = false;
        public static ItemStack SHULKER_ITEM_STACK;
    }

    public static void loadFromFile()
    {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Generic", Configs.Generic.OPTIONS);
            }
        }
    }

    public static void saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Generic", Configs.Generic.OPTIONS);

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveToFile();
    }
}
