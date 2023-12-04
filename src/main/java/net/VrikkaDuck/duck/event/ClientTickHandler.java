package net.VrikkaDuck.duck.event;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.minecraft.client.MinecraftClient;

public class ClientTickHandler implements IClientTickHandler {

    private final ClientBlockHitHandler blockHitHandler = ClientBlockHitHandler.INSTANCE();
    private final ClientEntityHitHandler entityHitHandler = ClientEntityHitHandler.INSTANCE();

    @Override
    public void onClientTick(MinecraftClient mc) {
        if (mc.world != null && mc.player != null)
        {
            blockHitHandler.tick();
            entityHitHandler.tick();
        }
    }


}
