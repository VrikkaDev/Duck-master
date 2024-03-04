package net.VrikkaDuck.duck.config.client;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.gui.button.ConfigButtonBoolean;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.gui.DuckPrintOutputType;
import net.VrikkaDuck.duck.config.client.options.admin.DuckConfigLevel;
import net.VrikkaDuck.duck.config.client.options.generic.DuckConfigHotkeyToggleable;
import net.VrikkaDuck.duck.config.client.options.generic.DuckConfigString;
import net.VrikkaDuck.duck.config.common.ServerConfigs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.EntityDataType;
import net.VrikkaDuck.duck.util.PermissionLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.util.*;

public class Configs implements IConfigHandler {
    //TODO Needs cleanup
    private static final String CONFIG_FILE_NAME = Variables.MODID + ".json";

    public static class Generic {
        public static final DuckConfigHotkeyToggleable INSPECT_CONTAINER = new DuckConfigHotkeyToggleable(ServerConfigs.Generic.INSPECT_CONTAINER.getName(), true, "LEFT_SHIFT", KeybindSettings.MODIFIER_INGAME, "Allows you to inspect containers while they are placed.");
        public static final DuckConfigHotkeyToggleable INSPECT_MINECART_CONTAINERS = new DuckConfigHotkeyToggleable(ServerConfigs.Generic.INSPECT_MINECART_CONTAINERS.getName(), true, "LEFT_SHIFT", KeybindSettings.MODIFIER_INGAME, "Inspect minecart containers");
        public static final DuckConfigHotkeyToggleable INSPECT_PLAYER_INVENTORY = new DuckConfigHotkeyToggleable(ServerConfigs.Generic.INSPECT_PLAYER_INVENTORY.getName(), true, "LEFT_SHIFT", KeybindSettings.MODIFIER_INGAME, "Inspect player entity inventory");
        public static final DuckConfigHotkeyToggleable INSPECT_VILLAGER_TRADES = new DuckConfigHotkeyToggleable(ServerConfigs.Generic.INSPECT_VILLAGER_TRADES.getName(), true, "LEFT_SHIFT", KeybindSettings.MODIFIER_INGAME, "Inspect villager trades");
        public static final DuckConfigHotkeyToggleable SHOW_STATE_INFO = new DuckConfigHotkeyToggleable("showBlockstateInfo", true, "LEFT_SHIFT", KeybindSettings.MODIFIER_INGAME,"When enabled while inspecting there will be a window\n that shows more info about inspected thing.");

        public static ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_MINECART_CONTAINERS, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES, SHOW_STATE_INFO);
        public static final ImmutableList<IConfigBase> DEFAULT_OPTIONS = ImmutableList.of(
                INSPECT_CONTAINER,
                INSPECT_MINECART_CONTAINERS,
                INSPECT_PLAYER_INVENTORY,
                INSPECT_VILLAGER_TRADES,
                SHOW_STATE_INFO,
                Hotkeys.OPEN_CONFIG_GUI
        );
        public static final ImmutableList<DuckConfigHotkeyToggleable> CONFIG_HOTKEYS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_MINECART_CONTAINERS, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES, SHOW_STATE_INFO);
        public static boolean isAnyPressed(){
            return isAnyPressed(new ArrayList<>());
        }
        public static boolean isAnyPressed(List<DuckConfigHotkeyToggleable> exclude){

            for(DuckConfigHotkeyToggleable h : CONFIG_HOTKEYS){
                if (Admin.fromName(h.getName()) == null){
                    continue;
                }
                if(h.isKeybindHeld() && Admin.fromName(h.getName()).getBooleanValue() && !exclude.contains(h)){
                    return true;
                }
            }
            return false;
        }

        public static boolean isAnyBoolean(DuckConfigHotkeyToggleable... h){
            for(DuckConfigHotkeyToggleable a : h){
                if(a.getBooleanValue() && Objects.requireNonNull(Admin.fromName(a.getName())).getBooleanValue()){
                    return true;
                }
            }
            return false;
        }
    }

    public static class Admin {
        public static final DuckConfigLevel INSPECT_CONTAINER = new DuckConfigLevel(Generic.INSPECT_CONTAINER.getName(), true, PermissionLevel.NORMAL ,Generic.INSPECT_CONTAINER.getComment());
        public static final DuckConfigLevel INSPECT_MINECART_CONTAINERS = new DuckConfigLevel(Generic.INSPECT_MINECART_CONTAINERS.getName(), false, PermissionLevel.NORMAL, Generic.INSPECT_MINECART_CONTAINERS.getComment());
        public static final DuckConfigLevel INSPECT_PLAYER_INVENTORY = new DuckConfigLevel(Generic.INSPECT_PLAYER_INVENTORY.getName(), false, PermissionLevel.NORMAL,Generic.INSPECT_PLAYER_INVENTORY.getComment());
        public static final DuckConfigLevel INSPECT_VILLAGER_TRADES = new DuckConfigLevel(Generic.INSPECT_VILLAGER_TRADES.getName(), false, PermissionLevel.NORMAL, Generic.INSPECT_VILLAGER_TRADES.getComment());

        public static ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_MINECART_CONTAINERS, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);

        public static final ImmutableList<IConfigBase> DEFAULT_OPTIONS = ImmutableList.of(INSPECT_CONTAINER, INSPECT_MINECART_CONTAINERS, INSPECT_PLAYER_INVENTORY, INSPECT_VILLAGER_TRADES);

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

        public static DuckConfigLevel fromName(String name){
            for(IConfigBase cb : Configs.Admin.OPTIONS) {

                if (cb.getName().equals(name)){
                    return (DuckConfigLevel)cb;
                }
            }
            return null;
        }
    }

    public static class Debug {
        public static final ConfigBoolean DRAW_DEBUG_PIE = new ConfigBoolean("draw_debug_pie", false, "Draws debug pie");
        public static final ConfigBoolean DRAW_DEBUG_INFO = new ConfigBoolean("draw_debug_info", false, "Draws some debugging info");
        public static final ConfigBoolean PRINT_PACKETS_S2C = new ConfigBoolean("print_packets_s2c", false, "Prints duck packets");
        public static final ConfigBoolean PRINT_PACKETS_C2S = new ConfigBoolean("print_packets_c2s", false, "Prints duck packets");
        public static final ConfigBoolean PRINT_MISC = new ConfigBoolean("print_misc", false, "Prints misc things");
        public static final ConfigOptionList PRINT_TYPE = new ConfigOptionList("print_type", DuckPrintOutputType.NONE, "");
        public static ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(DRAW_DEBUG_PIE, DRAW_DEBUG_INFO, PRINT_PACKETS_S2C, PRINT_PACKETS_C2S, PRINT_MISC, PRINT_TYPE);
        public static final ImmutableList<IConfigBase> DEFAULT_OPTIONS = ImmutableList.of(DRAW_DEBUG_PIE, DRAW_DEBUG_INFO, PRINT_PACKETS_S2C, PRINT_PACKETS_C2S, PRINT_MISC, PRINT_TYPE);
    }

    public static class Actions{
        // todo change
        public static int RENDER_DOUBLE_CHEST_TOOLTIP = 0;
        public static BlockPos LOOKING_AT;
        public static UUID LOOKING_AT_ENTITY;
        public static Map<BlockPos, Map.Entry<NbtCompound, ContainerType>> WORLD_CONTAINERS = new HashMap<>();
        public static Map<UUID, Map.Entry<NbtCompound, EntityDataType>> WORLD_ENTITIES = new HashMap<>();
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
                if(Variables.DEBUG){
                    ConfigUtils.readConfigBase(root, "Debug", Debug.OPTIONS);
                }
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
            if(Variables.DEBUG){
                ConfigUtils.writeConfigBase(root, "Debug", Debug.OPTIONS);
            }

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
