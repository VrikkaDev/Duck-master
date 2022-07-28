package net.VrikkaDuck.duck.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.gui.widgets.WidgetSlider;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.options.ConfigLevel;
import net.VrikkaDuck.duck.util.PermissionLevel;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.village.TradeOfferList;

import java.io.File;

public class Configs implements IConfigHandler {
    private static final String CONFIG_FILE_NAME = Variables.MODID + ".json";

    public static class Generic {
        public static final ConfigHotkey INSPECT_CONTAINER = new ConfigHotkey("inspectContainers", "k", "Inspect Containers when placed");
        public static final ConfigHotkey INSPECT_FURNACE = new ConfigHotkey("inspectFurnace", "k", "Inspect Furnaces when placed");
        public static final ConfigHotkey INSPECT_BEEHIVE = new ConfigHotkey("inspectBeehive", "k" , "Inspect beehives when on ground");
        public static final ConfigHotkey INSPECT_PLAYER_INVENTORY = new ConfigHotkey("inspectPlayerInventory", "k", "inspect entity inventory");
        public static final ConfigHotkey INSPECT_VILLAGER_TRADES = new ConfigHotkey("inspectVillagerTrades", "", "Inspect villager trades");
        public static ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_FURNACE, INSPECT_BEEHIVE, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);

        public static final ImmutableList<IConfigBase> DEFAULT_OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_FURNACE, INSPECT_BEEHIVE, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);
        public static final ImmutableList<ConfigHotkey> CONFIG_HOTKEYS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_FURNACE, INSPECT_BEEHIVE, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);
    }

    public static class Admin {
        public static final ConfigLevel INSPECT_CONTAINER = new ConfigLevel("inspectContainers", false, PermissionLevel.NORMAL ,"Inspect Containers when placed");
        public static final ConfigLevel INSPECT_FURNACE = new ConfigLevel("inspectFurnace", false, PermissionLevel.NORMAL,"Inspect furnaces when placed");
        public static final ConfigLevel INSPECT_BEEHIVE = new ConfigLevel("inspectBeehive", false , PermissionLevel.NORMAL,"Inspect beehives when on ground");
        public static final ConfigLevel INSPECT_PLAYER_INVENTORY = new ConfigLevel("inspectPlayerInventory", false, PermissionLevel.NORMAL,"inspect entity inventory");
        public static final ConfigLevel INSPECT_VILLAGER_TRADES = new ConfigLevel("inspectVillagerTrades", false, PermissionLevel.NORMAL, "Inspect villager trades");
        public static ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_FURNACE, INSPECT_BEEHIVE, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);

        public static final ImmutableList<IConfigBase> DEFAULT_OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_FURNACE, INSPECT_BEEHIVE, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);
    }

    public static class Actions{
        public static boolean RENDER_CONTAINER_TOOLTIP = false;
        public static boolean RENDER_FURNACE_TOOLTIP = false;
        public static boolean RENDER_BEEHIVE_PREVIEW = false;
        public static boolean RENDER_PLAYER_INVENTORY_PREVIEW = false;
        public static boolean RENDER_VILLAGER_TRADES = false;
        public static int RENDER_DOUBLE_CHEST_TOOLTIP = 0;
        public static NbtCompound FURNACE_NBT;
        public static NbtCompound BEEHIVE_NBT;
        public static TradeOfferList VILLAGER_TRADES;
        public static Inventory TARGET_PLAYER_INVENTORY;
        public static ItemStack CONTAINER_ITEM_STACK;
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
