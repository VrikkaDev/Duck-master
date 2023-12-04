package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.networking.PacketsC2S;
import net.VrikkaDuck.duck.networking.ServerPlayerManager;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.VrikkaDuck.duck.world.common.ContainerWorld;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayerNetworkHandlerMixin {
    @Shadow public abstract ServerPlayerEntity getPlayer();

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void duck$ServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){

        ServerPlayerManager.INSTANCE().putProperty(player.getUuid(), "containerWorld", new ContainerWorld(player));

        player.getEnderChestInventory().addListener(l -> {

            Optional<Object> _fp = ServerPlayerManager.INSTANCE().getProperty(player.getUuid(), "playerLastBlockpos");
            BlockPos _p = _fp.map(o -> (BlockPos) o).orElseGet(player::getBlockPos);

            ContainerPacket.ContainerC2SPacket p = new ContainerPacket.ContainerC2SPacket(
                    player.getUuid(), _p);
            PacketsC2S.onContainerPacket(p, player, null);
        });
    }

    @Inject(method = "onDisconnected", at = @At("RETURN"))
    private void duck$onDisconnected(CallbackInfo c){
        ServerPlayerManager.INSTANCE().playerProperties.remove(getPlayer().getUuid());
    }
}
