package net.VrikkaDuck.duck.event;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.VrikkaDuck.duck.networking.ErrorLevel;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.packet.ErrorPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientTickHandler implements IClientTickHandler {

    private final ClientBlockHitHandler blockHitHandler = ClientBlockHitHandler.INSTANCE();
    private final ClientEntityHitHandler entityHitHandler = ClientEntityHitHandler.INSTANCE();

    private int i = 0;
    @Override
    public void onClientTick(MinecraftClient mc) {
        if (mc.world != null && mc.player != null)
        {
            blockHitHandler.tick();
            entityHitHandler.tick();

            i++;
            if(mc.player.isSneaking() && i >= 20){
                i = 0;

                ErrorPacket.ErrorC2SPacket p = new ErrorPacket.ErrorC2SPacket(mc.player.getUuid(), ErrorLevel.INFO, Text.of(""));
                NetworkHandler.Client.SendToServer(p);
            }
        }
    }


}
