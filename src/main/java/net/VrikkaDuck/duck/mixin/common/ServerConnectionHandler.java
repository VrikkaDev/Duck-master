package net.VrikkaDuck.duck.mixin.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.IConfigValue;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.IServerLevel;
import net.VrikkaDuck.duck.config.ServerConfigs;
import net.VrikkaDuck.duck.config.options.ServerDouble;
import net.VrikkaDuck.duck.config.options.ServerLevel;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.VrikkaDuck.duck.util.PacketUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerConnectionHandler {
    @Shadow public ServerPlayerEntity player;


    @Inject(at = @At("RETURN"), method = "onCustomPayload")
    private void onCustomPayload(CustomPayloadC2SPacket pak, CallbackInfo cb) {
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(pak.getChannel(), new PacketByteBuf(pak.getData().copy()));

        NetworkThreadUtils.forceMainThread(packet, ((ServerPlayNetworkHandler) (Object) this),
                ((ServerPlayNetworkHandler) (Object) this).player.getServerWorld());

        if (handleCustomPayload(packet)) {
            PacketUtils.sendServerConfigsToPlayer(player);
        }
    }

    @Unique
    private boolean handleCustomPayload(CustomPayloadC2SPacket packet) {
        Identifier channel = packet.getChannel();

        if(channel.equals(Variables.ERRORID)){
            Variables.LOGGER.error(packet.getData().readString());
            return true;
        }

        if (channel.equals(Variables.GENERICID) || channel.equals(Variables.ADMINID)) {
            PacketByteBuf buf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
            buf.writeVarInt(channel.equals(Variables.GENERICID) ? 0 : 1);
            NbtCompound nbt = new NbtCompound();

            for (IServerLevel base : ServerConfigs.Generic.OPTIONS) {
                if (base instanceof ServerLevel) {
                    ServerLevel sbase = (ServerLevel) base;
                    nbt.putBoolean(sbase.getName(), sbase.getBooleanValue());
                    nbt.putInt(sbase.getName() + ",level", sbase.getPermissionLevel());
                }else if(base instanceof ServerDouble){
                    ServerDouble sbase = (ServerDouble) base;
                    nbt.putDouble(sbase.getName(), sbase.getDoubleValue());
                }
            }

            buf.writeNbt(nbt);
            PacketUtils.send(player.networkHandler, channel, buf);
            return true;
        } else if (channel.equals(Variables.ADMINSETID)) {
            PacketByteBuf buf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
            buf.writeVarInt(2);
            NbtCompound compound = packet.getData().readNbt();

            if (!compound.isEmpty()) {
                String name = packet.getData().readString();

                if (player.hasPermissionLevel(Variables.PERMISSIONLEVEL)) {
                    List<IServerLevel> _list = new ArrayList<>();
                    for (IServerLevel base : ServerConfigs.Generic.OPTIONS) {
                        if (base instanceof ServerLevel sbase) {

                            if(base.getName().equals(name)){
                                boolean value = compound.getBoolean(name);
                                int pvalue = compound.getInt("level");

                                sbase.setBooleanValue(value);
                                sbase.setPermissionLevel(pvalue);
                                _list.add(sbase);
                                continue;
                            }

                            _list.add(sbase);
                        }else if(base instanceof ServerDouble sbase){

                            if(base.getName().equals(name)){
                                double value = compound.getDouble(name);
                                sbase.setDoubleValue(value);
                                _list.add(sbase);
                                continue;
                            }
                            _list.add(sbase);
                        }
                    }
                    ServerConfigs.Generic.OPTIONS = ImmutableList.copyOf(_list);
                    ServerConfigs.saveToFile();
                    PacketUtils.sendServerConfigsToAllPlayers();
                }
            }
            return true;
        } else if (channel.equals(Variables.ACTIONID)) {
            PacketTypes type = PacketType.identifierToType(packet.getData().readIdentifier());

            if (type != null && packet.getData().isReadable()) {
                switch (type) {
                    case CONTAINERS -> PacketUtils.handleContainersInspection(packet, player);
                    case CONTAINER -> PacketUtils.handleContainerInspection(packet, player);
                    case FURNACE -> PacketUtils.handleFurnaceInspection(packet, player);
                    case BEEHIVE -> PacketUtils.handleBeehiveInspection(packet, player);
                    case PLAYERINVENTORY -> PacketUtils.handlePlayerInventoryInspection(packet, player);
                    case VILLAGERTRADES -> PacketUtils.handleVillagerTradesInspection(packet, player);
                    default -> Variables.LOGGER.error("Could not get a valid PacketType,\n You most likely have outdated version of the Duck mod");
                }
            } else {
                Variables.LOGGER.error("Packet data is not readable \n Try updating the duck mod");
            }
            return true;
        }
        return false;
    }
}