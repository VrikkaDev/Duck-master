package net.VrikkaDuck.duck.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.options.ConfigLevel;
import net.VrikkaDuck.duck.config.options.ServerLevel;
import net.VrikkaDuck.duck.util.GameWorld;
import net.VrikkaDuck.duck.util.PermissionLevel;
import net.minecraft.client.MinecraftClient;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class ServerConfigs{

    private static final String CONFIG_FILE_NAME = Variables.MODID + "-server" + ".json";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static class Generic {
        public static final ServerLevel INSPECT_CONTAINER = new ServerLevel("inspectContainers", false, PermissionLevel.NORMAL);
        public static final ServerLevel INSPECT_FURNACE = new ServerLevel("inspectFurnace", false, PermissionLevel.NORMAL);
        public static final ServerLevel INSPECT_BEEHIVE = new ServerLevel("inspectBeehive", false, PermissionLevel.NORMAL);
        public static final ServerLevel INSPECT_PLAYER_INVENTORY = new ServerLevel("inspectPlayerInventory", false, PermissionLevel.NORMAL);
        public static final ServerLevel INSPECT_VILLAGER_TRADES = new ServerLevel("inspectVillagerTrades", false, PermissionLevel.NORMAL);

        public static ImmutableList<ServerLevel> OPTIONS = ImmutableList.of(
                INSPECT_CONTAINER,
                INSPECT_FURNACE,
                INSPECT_BEEHIVE,
                INSPECT_PLAYER_INVENTORY,
                INSPECT_VILLAGER_TRADES
        );
    }

    public static void loadFromFile()
    {
        if(GameWorld.getServer() == null){
            return;
        }

       /* if(!GameWorld.isSingleplayer()){
            return;
          }
        */

        File configFile = new File(GameWorld.getDataFolder(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();

                readConfigBase(root, "Admin", Generic.OPTIONS);
            }
        }
    }
    public static void saveToFile()
    {
        if(GameWorld.getServer() == null){
            return;
        }

       /* if(!GameWorld.isSingleplayer()){
            return;
        }*/

        File dir = GameWorld.getDataFolder();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();

            writeConfigBase(root, "Admin", Generic.OPTIONS);

            writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }
    private static void readConfigBase(JsonObject root, String category, List<? extends ServerLevel> options)
    {
        JsonObject obj = getNestedObject(root, category, false);

        if (obj != null)
        {
            for (ServerLevel option : options)
            {
                String name = option.getName();

                if (obj.has(name))
                {
                    option.setValueFromJsonElement(obj.get(name));
                }
            }
        }
    }

    private static void writeConfigBase(JsonObject root, String category, List<? extends ServerLevel> options)
    {
        JsonObject obj = getNestedObject(root, category, true);

        for (ServerLevel option : options)
        {
            obj.add(option.getName(), option.getAsJsonElement());
        }
    }

    public static File getConfigDirectory()
    {
        return new File(MinecraftClient.getInstance().runDirectory, "config");
    }

    @Nullable
    public static JsonElement parseJsonFile(File file)
    {
        if (file != null && file.exists() && file.isFile() && file.canRead())
        {
            String fileName = file.getAbsolutePath();

            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))
            {
                return JsonParser.parseReader(reader);
            }
            catch (Exception e)
            {
                Variables.LOGGER.error("Failed to parse the JSON file '{}'", fileName, e);
            }
        }

        return null;
    }

    @Nullable
    public static JsonObject getNestedObject(JsonObject parent, String key, boolean create)
    {
        if (parent.has(key) == false || parent.get(key).isJsonObject() == false)
        {
            if (create == false)
            {
                return null;
            }

            JsonObject obj = new JsonObject();
            parent.add(key, obj);
            return obj;
        }
        else
        {
            return parent.get(key).getAsJsonObject();
        }
    }

    public static boolean writeJsonToFile(JsonObject root, File file)
    {
        File fileTmp = new File(file.getParentFile(), file.getName() + ".tmp");

        if (fileTmp.exists())
        {
            fileTmp = new File(file.getParentFile(), UUID.randomUUID() + ".tmp");
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileTmp), StandardCharsets.UTF_8))
        {
            writer.write(GSON.toJson(root));
            writer.close();

            if (file.exists() && file.isFile() && file.delete() == false)
            {
                Variables.LOGGER.warn("Failed to delete file '{}'", file.getAbsolutePath());
            }

            return fileTmp.renameTo(file);
        }
        catch (Exception e)
        {
            Variables.LOGGER.warn("Failed to write JSON data to file '{}'", fileTmp.getAbsolutePath(), e);
        }

        return false;
    }
}
