package net.VrikkaDuck.duck.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.options.DuckConfigDouble;
import net.VrikkaDuck.duck.config.options.DuckConfigLevel;
import net.VrikkaDuck.duck.util.PermissionLevel;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOfferList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configs implements IConfigHandler {
    private static final String CONFIG_FILE_NAME = Variables.MODID + ".json";

    public static class Generic {
        public static final ConfigHotkey INSPECT_CONTAINER = new ConfigHotkey("inspectContainers", "LEFT_SHIFT", "Allows you to inspect containers while they are placed.");
        public static final ConfigHotkey INSPECT_FURNACE = new ConfigHotkey("inspectFurnace", "LEFT_SHIFT", "Allows you to inspect furnaces while they are placed");
        public static final ConfigHotkey INSPECT_BEEHIVE = new ConfigHotkey("inspectBeehive", "LEFT_SHIFT" , "Allows you to inspect beehives while they are placed");
        public static final ConfigHotkey INSPECT_PLAYER_INVENTORY = new ConfigHotkey("inspectPlayerInventory", "LEFT_SHIFT", "Inspect player entity inventory");
        public static final ConfigHotkey INSPECT_VILLAGER_TRADES = new ConfigHotkey("inspectVillagerTrades", "LEFT_SHIFT", "Inspect villager trades");
        public static ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_FURNACE, INSPECT_BEEHIVE, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);

        public static final ImmutableList<IConfigBase> DEFAULT_OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_FURNACE, INSPECT_BEEHIVE, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);
        public static final ImmutableList<ConfigHotkey> CONFIG_HOTKEYS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_FURNACE, INSPECT_BEEHIVE, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);
    }

    public static class Admin {
        public static final DuckConfigLevel INSPECT_CONTAINER = new DuckConfigLevel("inspectContainers", true, PermissionLevel.NORMAL ,"Inspect Containers when placed");
        public static final DuckConfigLevel INSPECT_FURNACE = new DuckConfigLevel("inspectFurnace", true, PermissionLevel.NORMAL,"Inspect furnaces when placed");
        public static final DuckConfigLevel INSPECT_BEEHIVE = new DuckConfigLevel("inspectBeehive", true , PermissionLevel.NORMAL,"Inspect beehives when on ground");
        public static final DuckConfigLevel INSPECT_PLAYER_INVENTORY = new DuckConfigLevel("inspectPlayerInventory", false, PermissionLevel.NORMAL,"inspect entity inventory");
        public static final DuckConfigLevel INSPECT_VILLAGER_TRADES = new DuckConfigLevel("inspectVillagerTrades", false, PermissionLevel.NORMAL, "Inspect villager trades");
        public static final DuckConfigDouble INSPECT_DISTANCE = new DuckConfigDouble("inspectDistance", 5, "The distance how far away the \n player can be from container to inspect it.\n 0 = unlimited");

        public static ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_FURNACE, INSPECT_BEEHIVE, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);//, INSPECT_DISTANCE);

        public static final ImmutableList<IConfigBase> DEFAULT_OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_FURNACE, INSPECT_BEEHIVE, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);//, INSPECT_DISTANCE);
    }

    public static class Actions{
        public static String SERVER_DUCK_VERSION;
        public static boolean RENDER_CONTAINER_TOOLTIP = false;
        public static boolean RENDER_FURNACE_TOOLTIP = false;
        public static boolean RENDER_BEEHIVE_PREVIEW = false;
        public static boolean RENDER_PLAYER_INVENTORY_PREVIEW = false;
        public static boolean RENDER_VILLAGER_TRADES = false;
        public static int RENDER_DOUBLE_CHEST_TOOLTIP = 0;
        public static double INSPECT_DISTANCE = 5;
        public static NbtCompound FURNACE_NBT;
        public static NbtCompound BEEHIVE_NBT;
        public static TradeOfferList VILLAGER_TRADES;
        public static Inventory TARGET_PLAYER_INVENTORY;
        //public static ItemStack CONTAINER_ITEM_STACK;
        public static BlockPos LOOKING_AT;
        public static Map<BlockPos, ItemStack> WORLD_CONTAINERS = new HashMap<>();
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
