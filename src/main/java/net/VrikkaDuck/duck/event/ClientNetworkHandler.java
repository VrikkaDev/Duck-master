package net.VrikkaDuck.duck.event;

import io.netty.buffer.Unpooled;
import net.VrikkaDuck.duck.Variables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

public class ClientNetworkHandler {
    public static void refreshGenericTweaks(){
        try {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(Variables.GENERICID,
                    new PacketByteBuf(Unpooled.buffer())
                            .writeString(Variables.MODVERSION)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void refreshAdmin(){
        try {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(Variables.ADMINID,
                new PacketByteBuf(Unpooled.buffer())
                        .writeString(Variables.MODVERSION)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void setAdminBoolean(String optionName, boolean value, int permissionLevel){
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean(optionName, value);
        nbt.putInt("level", permissionLevel);
        buf.writeNbt(nbt);
        buf.writeString(optionName);


        try {
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(Variables.ADMINSETID,
                buf));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void sendAction(PacketByteBuf buf){
        try {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(Variables.ACTIONID,
                    buf));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
