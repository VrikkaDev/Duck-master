package net.VrikkaDuck.duck.event;

import com.google.errorprone.annotations.Var;
import io.netty.buffer.Unpooled;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ClientNetworkHandler {
    public static void refreshGenericTweaks(){
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(Variables.GENERICID,
                new PacketByteBuf(Unpooled.buffer())
                        .writeString(":)")));

    }
    public static void refreshAdmin(){
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(Variables.ADMINID,
                new PacketByteBuf(Unpooled.buffer())
                        .writeString(":)")));
    }
    public static void setAdminBoolean(String optionName, boolean value){
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean(optionName, value);
        buf.writeNbt(nbt);
        buf.writeString(optionName);

        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(Variables.ADMINSETID,
                buf));
    }
    public static void sendAction(PacketByteBuf buf, PacketType type){

        Variables.LOGGER.info(buf.readString());
        buf.writeString(type.name(), 20);
        Variables.LOGGER.info(buf.readString());
        Variables.LOGGER.info(type.name());
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(Variables.ACTIONID,
                buf));
    }
}
