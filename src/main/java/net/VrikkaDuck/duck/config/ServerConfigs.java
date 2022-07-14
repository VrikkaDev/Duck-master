package net.VrikkaDuck.duck.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.VrikkaDuck.duck.Variables;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.List;

public class ServerConfigs{
    private static final String CONFIG_FILE_NAME = Variables.MODID + "-server" + ".json";
    public static void loadFromFile()
    {
        File configFile = new File(FileUtils.getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                readConfigBase(root, "Admin", Generic.OPTIONS);
            }
        }
    }

    public static void saveToFile()
    {
        File dir = FileUtils.getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();

            writeConfigBase(root, "Admin", Generic.OPTIONS);

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }
    private static void readConfigBase(JsonObject root, String category, List<? extends ServerBoolean> options)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, false);

        if (obj != null)
        {
            for (ServerBoolean option : options)
            {
                String name = option.getName();

                if (obj.has(name))
                {
                    option.setValueFromJsonElement(obj.get(name));
                }
            }
        }
    }
    private static void writeConfigBase(JsonObject root, String category, List<? extends ServerBoolean> options)
    {
        JsonObject obj = JsonUtils.getNestedObject(root, category, true);

        for (ServerBoolean option : options)
        {
            obj.add(option.getName(), option.getAsJsonElement());
        }
    }

    public static class Generic {
        public static final ServerBoolean INSPECT_SHULKER = new ServerBoolean("InspectShulker", false);
        public static ImmutableList<ServerBoolean> OPTIONS = ImmutableList.of(INSPECT_SHULKER);
    }
}
