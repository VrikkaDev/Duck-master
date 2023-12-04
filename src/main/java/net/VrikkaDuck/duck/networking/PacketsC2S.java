package net.VrikkaDuck.duck.networking;

import com.google.common.collect.ImmutableList;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.common.IServerLevel;
import net.VrikkaDuck.duck.config.common.ServerConfigs;
import net.VrikkaDuck.duck.config.common.options.ServerDouble;
import net.VrikkaDuck.duck.config.common.options.ServerLevel;
import net.VrikkaDuck.duck.networking.packet.*;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.*;
import java.util.stream.Stream;

import static net.VrikkaDuck.duck.util.NbtUtils.*;

public class PacketsC2S {


    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ContainerPacket.ContainerC2SPacket.TYPE, PacketsC2S::onContainerPacket);
        ServerPlayNetworking.registerGlobalReceiver(ErrorPacket.ErrorC2SPacket.TYPE, PacketsC2S::onErrorPacket);
        ServerPlayNetworking.registerGlobalReceiver(HandshakePacket.HandshakeC2SPacket.TYPE, PacketsC2S::onHandshakePacket);
        ServerPlayNetworking.registerGlobalReceiver(AdminPacket.AdminC2SPacket.TYPE, PacketsC2S::onAdminPacket);
        ServerPlayNetworking.registerGlobalReceiver(EntityPacket.EntityC2SPacket.TYPE, PacketsC2S::onEntityPacket);
    }

    public static void onContainerPacket(ContainerPacket.ContainerC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender) {

        Variables.PROFILER.start("packetsC2S_processContainerPacket");

        BlockPos ppos = packet.pos();

        ServerPlayerManager.INSTANCE().putProperty(packet.uuid(), "playerLastBlockpos", ppos);

        Box box = new Box(ppos).expand(5);
        Stream<BlockEntity> blockEntities = BlockPos.stream(box).map(player.getWorld()::getBlockEntity);

        List<BlockPos> _posl = new ArrayList<>();

        blockEntities.forEach(blockEntity -> {

            if(blockEntity == null){
                return;
            }

            if(ContainerType.fromBlockEntity(blockEntity).value != -1){
                _posl.add(blockEntity.getPos());
            }
        });

        Optional<ContainerPacket.ContainerS2CPacket> p = getContainerPacket(_posl, player);
        p.ifPresent(containerS2CPacket -> NetworkHandler.SendToClient(player, containerS2CPacket));

        Variables.PROFILER.stop("packetsC2S_processContainerPacket");
    }

    private static void onErrorPacket(ErrorPacket.ErrorC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender){

        ErrorLevel errorLevel = packet.level();
        switch (errorLevel){
            case INFO -> Variables.LOGGER.info(packet.error().getString());
            case INFO_INCHAT -> player.sendMessage(packet.error());
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

    private static void onHandshakePacket(HandshakePacket.HandshakeC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender){

        ServerPlayerManager.INSTANCE().putProperty(packet.uuid(), "duckVersion", packet.duckVersion());

        HandshakePacket.HandshakeS2CPacket r = new HandshakePacket.HandshakeS2CPacket(packet.uuid(), Variables.MODVERSION);
        NetworkHandler.SendToClient(player, r);
    }

    public static void onAdminPacket(AdminPacket.AdminC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender){


        NbtCompound n = packet.nbtCompound();

        if(n.getBoolean("request")){
            NbtCompound rn = new NbtCompound();
            rn.putBoolean("request", false);

            rn.put("options", ServerConfigs.Generic.getAsNbtList());

            AdminPacket.AdminS2CPacket r = new AdminPacket.AdminS2CPacket(packet.uuid(), rn);
            NetworkHandler.SendToClient(player, r);
            return;
        }

        Variables.PROFILER.start("packetsC2S_processAdminPacket");

        // If type isn't request it is setadmin

        NbtList cl = n.getList("options", NbtList.COMPOUND_TYPE);
        Map<String, NbtCompound> _compoundmap = new HashMap<>();

        // todo: make better :D
        for(NbtElement element : cl){
            if(element instanceof NbtCompound compound){

                if(!player.hasPermissionLevel(Variables.PERMISSIONLEVEL) || compound.isEmpty()){
                    break;
                }

                String name = compound.getString("optionName");

                _compoundmap.put(name, compound);
            }
        }

        List<IServerLevel> _list = new ArrayList<>();
        for (IServerLevel base : ServerConfigs.Generic.OPTIONS) {

            NbtCompound compound = _compoundmap.getOrDefault(base.getName(), new NbtCompound());

            if (base instanceof ServerLevel sbase) {

                if(!compound.isEmpty()){
                    boolean value = compound.getBoolean("optionValue");
                    int pvalue = compound.getInt("optionPermissionLevel");

                    sbase.setBooleanValue(value);
                    sbase.setPermissionLevel(pvalue);
                    _list.add(sbase);
                    continue;
                }

                _list.add(sbase);
            }else if(base instanceof ServerDouble sbase){

                if(!compound.isEmpty()){
                    double value = compound.getDouble("optionValue");
                    sbase.setDoubleValue(value);
                    _list.add(sbase);
                    continue;
                }
                _list.add(sbase);
            }
        }

        ServerConfigs.Generic.OPTIONS = ImmutableList.copyOf(_list);
        ServerConfigs.saveToFile();

        // Send new configs to all players
        List<ServerPlayerEntity> players = Objects.requireNonNull(player.getServer()).getPlayerManager().getPlayerList();
        NbtCompound c = new NbtCompound();
        c.putBoolean("request", true);
        for (ServerPlayerEntity pl : players) {
            AdminPacket.AdminC2SPacket t = new AdminPacket.AdminC2SPacket(pl.getUuid(), c);

            PacketsC2S.onAdminPacket(t, pl, null);
        }
        Variables.PROFILER.stop("packetsC2S_processAdminPacket");

    }

    public static void onEntityPacket(EntityPacket.EntityC2SPacket packet, ServerPlayerEntity player, PacketSender responseSender){

        Variables.PROFILER.start("packetsC2S_processEntityPacket");

        NbtList _list = new NbtList();


        // todo add max entity limit
        Box box = new Box(packet.playerpos()).expand(5);
        List<Entity> entities = player.getEntityWorld().getOtherEntities(player, box);

        for(Entity entity : entities){

            NbtCompound compound = new NbtCompound();
            EntityDataType type = EntityDataType.fromEntity(entity);

            switch (type){
                case VILLAGER_TRADES -> compound = getVillagerTradesNbt((VillagerEntity) entity, player).orElse(new NbtCompound());
                case PLAYER_INVENTORY -> compound = getPlayerInventoryNbt((ServerPlayerEntity) entity, player).orElse(new NbtCompound());
                case MINECART_CHEST, MINECART_HOPPER -> compound = getMinecartContainerNbt((AbstractMinecartEntity) entity, player).orElse(new NbtCompound());
                default -> {continue;}
            }

            compound.putUuid("uuid", entity.getUuid());
            compound.putInt("entityType", type.value);
            _list.add(compound);
        }

        NbtCompound n = new NbtCompound();
        n.put("entities", _list);
        EntityPacket.EntityS2CPacket r = new EntityPacket.EntityS2CPacket(packet.uuid(), n);
        NetworkHandler.SendToClient(player, r);

        Variables.PROFILER.stop("packetsC2S_processEntityPacket");
    }
}
