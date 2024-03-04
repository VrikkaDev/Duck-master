package net.VrikkaDuck.duck.event;

import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.VrikkaDuck.duck.networking.ErrorLevel;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.packet.ErrorPacket;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.stream.Collectors;

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
