package net.VrikkaDuck.duck.mixin;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.Unpooled;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.VrikkaDuck.duck.config.ServerBoolean;
import net.VrikkaDuck.duck.config.ServerConfigs;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerConnectionHandler {
    @Shadow public ServerPlayerEntity player;

    @Shadow @Final private MinecraftServer server;

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

            NbtCompound compound = packet.getData().readNbt();

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

                ServerConfigs.saveToFile();

               // ServerPlayerEntity player = ((ServerPlayNetworkHandler) (Object) this).getPlayer();

                List<ServerPlayerEntity> players = this.server.getPlayerManager().getPlayerList();

                for(ServerPlayerEntity player : players) {
                    player.networkHandler.getConnection().send(new CustomPayloadS2CPacket(Variables.ADMINSETID, buf));
                }
            }
        }else if(packet.getChannel().equals(Variables.ACTIONID)){

            PacketTypes type = PacketType.identifierToType(packet.getData().readIdentifier());

            switch (type){
                case SHULKER:

                    if(!packet.getData().isReadable()){
                        Variables.LOGGER.error("Packet data is not readable");
                        return;
                    }

                    if(!ServerConfigs.Generic.INSPECT_SHULKER.getBooleanValue()){
                        return;
                    }

                    BlockPos pos = packet.getData().readBlockPos();

                    if(player.world.getBlockEntity(pos) == null){
                        Variables.LOGGER.error("Could not find BlockEntity from given position");
                        return;
                    }

                    ShulkerBoxBlockEntity sb = (ShulkerBoxBlockEntity) player.world.getBlockEntity(pos);

                    NbtCompound compound = sb.createNbtWithId();

                    if(compound.isEmpty()) {
                        return;
                    }

                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.SHULKER));
                    buf.writeNbt(compound);

                    player.networkHandler.getConnection().send(new CustomPayloadS2CPacket(Variables.ACTIONID, buf));

                    break;
                default:
                    Variables.LOGGER.error("Could not get viable PacketType");
                    break;
            }
        }
    }
}
