package net.VrikkaDuck.duck.config.common;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.common.options.ServerLevel;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.packet.AdminPacket;
import net.VrikkaDuck.duck.world.common.GameWorld;
import net.VrikkaDuck.duck.util.PermissionLevel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ServerConfigs {

    private static final String CONFIG_FILE_NAME = Variables.MODID + "-server" + ".json";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static class Generic {
        public static final IServerLevel INSPECT_CONTAINER = new ServerLevel("inspectContainers", true, PermissionLevel.NORMAL);
        public static final IServerLevel INSPECT_MINECART_CONTAINERS = new ServerLevel("inspectMinecartContainers", true, PermissionLevel.NORMAL);
        public static final IServerLevel INSPECT_PLAYER_INVENTORY = new ServerLevel("inspectPlayerInventory", false, PermissionLevel.NORMAL);
        public static final IServerLevel INSPECT_VILLAGER_TRADES = new ServerLevel("inspectVillagerTrades", false, PermissionLevel.NORMAL);

        public static ImmutableList<IServerLevel> OPTIONS = ImmutableList.of(
                INSPECT_CONTAINER,
                INSPECT_MINECART_CONTAINERS,
                INSPECT_PLAYER_INVENTORY,
                INSPECT_VILLAGER_TRADES
        );

        public static NbtList getAsNbtList(){

            NbtList _l = new NbtList();

            for(IServerLevel l : Generic.OPTIONS){
                NbtCompound compound = new NbtCompound();
                compound.putString("optionName", l.getName());
                compound.putBoolean("optionValue", l.getBooleanValue());
                compound.putInt("optionPermissionLevel", l.getPermissionLevel());

                _l.add(compound);
            }

            return _l;
        }
    }

    public static void refreshFromServer(){
        NbtCompound compound = new NbtCompound();
        compound.putBoolean("request", true);
        AdminPacket.AdminC2SPacket packet = new AdminPacket.AdminC2SPacket(MinecraftClient.getInstance().player.getUuid(), compound);
        NetworkHandler.Client.SendToServer(packet);
    }

    public static void loadFromFile() {
        if (GameWorld.getServer() == null) {
            return;
        }

        File configFile = new File(GameWorld.getDataFolder(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                readConfigBase(root, "Admin", Generic.OPTIONS);
            }
        }
    }

    public static void saveToFile() {

        if (GameWorld.getServer() == null) {
            return;
        }

        File dir = GameWorld.getDataFolder();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();

            writeConfigBase(root, "Admin", Generic.OPTIONS);

            writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    private static void readConfigBase(JsonObject root, String category, List<? extends IServerLevel> options) {
        JsonObject obj = getNestedObject(root, category, false);

        if (obj != null) {
            for (IServerLevel option : options) {
                String name = option.getName();

                if (obj.has(name)) {
                    option.setValueFromJsonElement(obj.get(name));
                }
            }
        }
    }

    private static void writeConfigBase(JsonObject root, String category, List<? extends IServerLevel> options) {
        JsonObject obj = getNestedObject(root, category, true);

        for (IServerLevel option : options) {
            obj.add(option.getName(), option.getAsJsonElement());
        }
    }

    public static File getConfigDirectory() {
        return new File(MinecraftClient.getInstance().runDirectory, "config");
    }

    @Nullable
    public static JsonElement parseJsonFile(File file) {
        if (file != null && file.exists() && file.isFile() && file.canRead()) {
            String fileName = file.getAbsolutePath();

            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                return JsonParser.parseReader(reader);
            } catch (Exception e) {
                Variables.LOGGER.error("Failed to parse the JSON file '{}'", fileName, e);
            }
        }

        return null;
    }

    @Nullable
    public static JsonObject getNestedObject(JsonObject parent, String key, boolean create) {
        if (!parent.has(key) || !parent.get(key).isJsonObject()) {
            if (!create) {
                return null;
            }

            JsonObject obj = new JsonObject();
            parent.add(key, obj);
            return obj;
        } else {
            return parent.get(key).getAsJsonObject();
        }
    }

    private static Instant lastSave = Instant.now();

    public static boolean writeJsonToFile(JsonObject root, File file) {

        // Get time since last save in milliseconds
        // This is for safety.
        long delta = Duration.between(lastSave, Instant.now()).toMillis();
        if(delta < 1000){
            return false;
        }
        lastSave = Instant.now();

        File fileTmp = new File(file.getParentFile(), file.getName() + ".tmp");

        if (fileTmp.exists()) {
            fileTmp = new File(file.getParentFile(), UUID.randomUUID() + ".tmp");
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileTmp), StandardCharsets.UTF_8)) {
            writer.write(GSON.toJson(root));
            writer.close();

            if (file.exists() && file.isFile() && !file.delete()) {
                Variables.LOGGER.warn("Failed to delete file '{}'", file.getAbsolutePath());
            }

            return fileTmp.renameTo(file);
        } catch (Exception e) {
            Variables.LOGGER.warn("Failed to write JSON data to file '{}'", fileTmp.getAbsolutePath(), e);
        }

        return false;
    }
}
