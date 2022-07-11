package net.VrikkaDuck.duck.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import io.netty.buffer.Unpooled;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.PacketType;
import net.VrikkaDuck.duck.config.ServerBoolean;
import net.VrikkaDuck.duck.config.ServerConfigs;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.NbtText;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerConnectionHandler {
    @Shadow public ServerPlayerEntity player;

    @Inject(at = @At("RETURN"),method = "onCustomPayload")
    private void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo cb){
        NetworkThreadUtils.forceMainThread(packet, ((ServerPlayNetworkHandler)(Object)this),
                ((ServerPlayNetworkHandler)(Object)this).player.getWorld());
        if(packet.getChannel().equals(Variables.GENERICID)){
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

            NbtCompound nbt = new NbtCompound();
            for(ServerBoolean base : ServerConfigs.Generic.OPTIONS){
                nbt.putBoolean(base.getName(), base.getBooleanValue());
            }
            buf.writeNbt(nbt);
            ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).getPlayer();
            player.networkHandler.getConnection().send(new CustomPayloadS2CPacket(Variables.GENERICID, buf));
        }else if(packet.getChannel().equals(Variables.ADMINID)){
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

            NbtCompound nbt = new NbtCompound();
            for(ServerBoolean base : ServerConfigs.Generic.OPTIONS){
                nbt.putBoolean(base.getName(), base.getBooleanValue());
            }
            buf.writeNbt(nbt);
            ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).getPlayer();
            player.networkHandler.getConnection().send(new CustomPayloadS2CPacket(Variables.ADMINID, buf));
        }else if(packet.getChannel().equals(Variables.ADMINSETID)){
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeString(":)");
            //Variables.LOGGER.info(packet.getData().readString());
            NbtCompound compound = packet.getData().readNbt();
            // Variables.LOGGER.info(packet.getData().readBoolean());
            if(!compound.isEmpty()) {
                    String name = compound.getKeys().stream().toList().get(0);
                if(name.isEmpty()){
                    Variables.LOGGER.error("Option name is empty", name);
                    return;
                }
                boolean value = compound.getBoolean(name);
                if (((ServerPlayNetworkHandler) (Object) this).getPlayer().hasPermissionLevel(Variables.PERMISSIONLEVEL)) {
                    List<ServerBoolean> _list = List.copyOf(ServerConfigs.Generic.OPTIONS);
                    for (ServerBoolean base : _list) {
                        if (base.getName().equals(name)) {
                            base.setBooleanValue(value);
                        }
                    }
                    ServerConfigs.Generic.OPTIONS = ImmutableList.copyOf(_list);
                }
                ServerPlayerEntity player = ((ServerPlayNetworkHandler) (Object) this).getPlayer();
                player.networkHandler.getConnection().send(new CustomPayloadS2CPacket(Variables.ADMINSETID, buf));
            }
        }else if(packet.getChannel().equals(Variables.ACTIONID)){
            Variables.LOGGER.info("inse");
            PacketType type = PacketType.valueOf("SHULKER");
            if(type == null){return;}
            Variables.LOGGER.info("rver");
            switch (type){
                case SHULKER:
                    if(!ServerConfigs.Generic.INSPECT_SHULKER.getBooleanValue()){
                        return;
                    }
                    BlockPos pos = packet.getData().readBlockPos();
                    ShulkerBoxBlockEntity sb = (ShulkerBoxBlockEntity) player.world.getBlockEntity(pos);
                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    NbtCompound compound = sb.createNbtWithId();
                    buf.writeNbt(compound);
                    buf.writeMedium(4);
                    if(sb.createNbtWithId() == null){
                        return;
                    }
                    player.networkHandler.getConnection().send(new CustomPayloadS2CPacket(Variables.ACTIONID, buf));
                    break;
            }
        }
        //Variables.LOGGER.info(packet.getData().readString());
    }
}
