package net.VrikkaDuck.duck.config.client;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.options.admin.DuckConfigDouble;
import net.VrikkaDuck.duck.config.client.options.admin.DuckConfigLevel;
import net.VrikkaDuck.duck.config.client.options.generic.DuckConfigHotkeyToggleable;
import net.VrikkaDuck.duck.config.common.IServerLevel;
import net.VrikkaDuck.duck.config.common.ServerConfigs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.util.PermissionLevel;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOfferList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Configs implements IConfigHandler {
    private static final String CONFIG_FILE_NAME = Variables.MODID + ".json";

    public static class Generic {
        public static final DuckConfigHotkeyToggleable INSPECT_CONTAINER = new DuckConfigHotkeyToggleable("inspectContainers", true, "LEFT_SHIFT", KeybindSettings.MODIFIER_INGAME, "Allows you to inspect containers while they are placed.");
        public static final DuckConfigHotkeyToggleable INSPECT_PLAYER_INVENTORY = new DuckConfigHotkeyToggleable("inspectPlayerInventory", true, "LEFT_SHIFT", KeybindSettings.MODIFIER_INGAME, "Inspect player entity inventory");
        public static final DuckConfigHotkeyToggleable INSPECT_VILLAGER_TRADES = new DuckConfigHotkeyToggleable("inspectVillagerTrades", true, "LEFT_SHIFT", KeybindSettings.MODIFIER_INGAME, "Inspect villager trades");
        public static ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);
        public static final ImmutableList<IConfigBase> DEFAULT_OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);
        public static final ImmutableList<DuckConfigHotkeyToggleable> CONFIG_HOTKEYS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);
    }

    public static class Admin {
        public static final DuckConfigLevel INSPECT_CONTAINER = new DuckConfigLevel("inspectContainers", true, PermissionLevel.NORMAL ,"Inspect Containers when placed");
        public static final DuckConfigLevel INSPECT_PLAYER_INVENTORY = new DuckConfigLevel("inspectPlayerInventory", false, PermissionLevel.NORMAL,"inspect entity inventory");
        public static final DuckConfigLevel INSPECT_VILLAGER_TRADES = new DuckConfigLevel("inspectVillagerTrades", false, PermissionLevel.NORMAL, "Inspect villager trades");

        public static ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);

        public static final ImmutableList<IConfigBase> DEFAULT_OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);

        public static NbtList getAsNbtList(){

            NbtList _l = new NbtList();

            for(IConfigBase l : Configs.Admin.OPTIONS){

                if(l instanceof DuckConfigLevel level){
                    NbtCompound compound = new NbtCompound();
                    compound.putString("optionName", level.getName());
                    compound.putBoolean("optionValue", level.getBooleanValue());
                    compound.putInt("optionPermissionLevel", level.getPermissionLevel());

                    _l.add(compound);
                }
            }

            return _l;
        }
    }

    public static class Actions{
        public static boolean RENDER_CONTAINER_TOOLTIP = false;
        public static boolean RENDER_PLAYER_INVENTORY_PREVIEW = false;
        public static boolean RENDER_VILLAGER_TRADES = false;
        public static int RENDER_DOUBLE_CHEST_TOOLTIP = 0;
        public static TradeOfferList VILLAGER_TRADES;
        public static Inventory TARGET_PLAYER_INVENTORY;
        public static BlockPos LOOKING_AT;
        public static Map<BlockPos, Map.Entry<NbtCompound, ContainerType>> WORLD_CONTAINERS = new HashMap<>();
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

            ConfigUtils.writeConfigBase(root, "Generic", Generic.CONFIG_HOTKEYS);

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
