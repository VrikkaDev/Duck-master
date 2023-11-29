package net.VrikkaDuck.duck.networking;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.util.InventoryUtils;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.config.client.options.admin.DuckConfigDouble;
import net.VrikkaDuck.duck.config.client.options.admin.DuckConfigLevel;
import net.VrikkaDuck.duck.config.common.ServerConfigs;
import net.VrikkaDuck.duck.networking.packet.*;
import net.VrikkaDuck.duck.util.GameWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.village.TradeOfferList;

import java.util.*;

@Environment(EnvType.CLIENT)
public class PacketsS2C {

    public static final Map<String, Object> serverProperties = new HashMap<>();

    public static void register(){
        ClientPlayNetworking.registerReceiver(ContainerPacket.ContainerS2CPacket.TYPE, PacketsS2C::onContainerPacket);
        ClientPlayNetworking.registerReceiver(ErrorPacket.ErrorS2CPacket.TYPE, PacketsS2C::onErrorPacket);
        ClientPlayNetworking.registerReceiver(HandshakePacket.HandshakeS2CPacket.TYPE, PacketsS2C::onHandshakePacket);
        ClientPlayNetworking.registerReceiver(AdminPacket.AdminS2CPacket.TYPE, PacketsS2C::onAdminPacket);
        ClientPlayNetworking.registerReceiver(EntityPacket.EntityS2CPacket.TYPE, PacketsS2C::onEntityPacket);
    }

    private static void onContainerPacket(ContainerPacket.ContainerS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender){


        NbtCompound nbt = new NbtCompound();
        NbtCompound tnbt = packet.nbtCompound();

        switch (packet.type()){

            case FURNACE, BEEHIVE -> {
                nbt = tnbt;
            }

            default -> {
                nbt.put("BlockEntityTag", tnbt);

                if (!nbt.getCompound("BlockEntityTag").contains("Items") || nbt.getCompound("BlockEntityTag").getList("Items", 10).isEmpty()) {
                    NbtList lst = new NbtList();
                    NbtCompound a = new NbtCompound();
                    a.put("Count", NbtByte.of((byte) 1));
                    lst.add(0, a);
                    NbtCompound b = new NbtCompound();
                    b.put("Slot", NbtByte.of((byte) 1));
                    lst.add(1, b);
                    NbtCompound c = new NbtCompound();
                    c.put("Count", NbtString.of("minecraft:air"));
                    lst.add(2, c);
                    nbt.getCompound("BlockEntityTag").put("Items", lst);
                }
            }
        }

        Configs.Actions.WORLD_CONTAINERS.put(packet.pos(), Map.entry(nbt, packet.type()));
        Configs.Actions.RENDER_CONTAINER_TOOLTIP = true;
    }

    private static void onErrorPacket(ErrorPacket.ErrorS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender){
        ErrorLevel errorLevel = packet.level();
        switch (errorLevel){
            case INFO -> Variables.LOGGER.info(packet.error().getString());
            case INFO_INCHAT -> Objects.requireNonNull(player).sendMessage(packet.error());
            case WARN -> Variables.LOGGER.warn(packet.error().getString());
            case ERROR -> Variables.LOGGER.error(packet.error().getString());
            default -> Variables.LOGGER.warn(("""
                            Duck couldn't recognise error level (%s) with error message\s
                             %s\s
                             please consider updating your duck mod""")
                    .formatted(errorLevel.toString(), packet.error().getString())
            );
        }
    }

    private static void onHandshakePacket(HandshakePacket.HandshakeS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender){
        serverProperties.put("duckVersion", packet.duckVersion());
    }

    private static void onAdminPacket(AdminPacket.AdminS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender){
        Map<String, NbtCompound> _map = new HashMap<>();
        for(NbtElement element : packet.nbtCompound().getList("options", NbtList.COMPOUND_TYPE)){
            if(element instanceof NbtCompound compound){
                _map.put(compound.getString("optionName"), compound);
            }
        }

        List<IConfigBase> _list = new ArrayList<>();
        for(IConfigBase base : Configs.Admin.DEFAULT_OPTIONS){
            if(base instanceof DuckConfigDouble sbase){
                // inspectdistance. not used
                continue;
            }
            if(_map.containsKey(base.getName())){
                NbtCompound nbt = _map.get(base.getName());
                ((DuckConfigLevel)base).setBooleanValue(nbt.getBoolean("optionValue"));
                ((DuckConfigLevel)base).setPermissionLevel(nbt.getInt("optionPermissionLevel"));
                _list.add(base);
            }
        }
        Configs.Admin.OPTIONS = ImmutableList.copyOf(_list);

        for(IConfigBase base : Configs.Generic.DEFAULT_OPTIONS){
            if(!_map.containsKey(base.getName())){
                continue;
            }
            NbtCompound n = _map.get(base.getName());
            if(n.getBoolean("optionValue") && GameWorld.hasPermissionLevel(n.getInt("optionPermissionLevel"), MinecraftClient.getInstance())){
                _list.add(base);
            }
        }
        Configs.Generic.OPTIONS = ImmutableList.copyOf(_list);

        ServerConfigs.saveToFile();
    }

    private static void onEntityPacket(EntityPacket.EntityS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender){
        switch (packet.type()){
            case PLAYER_INVENTORY -> {
                NbtCompound invnbt = packet.nbt();
                NbtList invList = invnbt.getList("Inventory", 10);
                DefaultedList<ItemStack> itemsasstack = DefaultedList.ofSize(121, new ItemStack(Items.AIR));
                for (NbtElement a : invList) {
                    ItemStack sst = ItemStack.fromNbt((NbtCompound) a);
                    if (((NbtCompound) a).getByte("Slot") == -106) {
                        itemsasstack.set(120, sst);
                    } else {
                        itemsasstack.set(((NbtCompound) a).getByte("Slot"), sst);
                    }
                }
                Configs.Actions.TARGET_PLAYER_INVENTORY = InventoryUtils.getAsInventory(itemsasstack);
                Configs.Actions.RENDER_PLAYER_INVENTORY_PREVIEW = true;
            }

            case VILLAGER_TRADES -> {
                TradeOfferList veList = new TradeOfferList(packet.nbt());
                Configs.Actions.RENDER_VILLAGER_TRADES = true;
                Configs.Actions.VILLAGER_TRADES = veList;
            }
        }
    }
}
