package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.ServerPlayerManager;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.VrikkaDuck.duck.util.NbtUtils;
import net.VrikkaDuck.duck.world.common.ContainerWorld;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayerNetworkHandlerMixin {
    @Shadow public abstract ServerPlayerEntity getPlayer();

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void duck$ServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci){

        ServerPlayerManager.INSTANCE().putProperty(player.getUuid(), "containerWorld", new ContainerWorld(player));

        player.getEnderChestInventory().addListener(l -> {

            Optional<Object> _fp = ServerPlayerManager.INSTANCE().getProperty(player.getUuid(), "playerLastBlockpos");
            BlockPos _p = _fp.map(o -> (BlockPos) o).orElseGet(player::getBlockPos);

            Box box = new Box(_p).expand(5);
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

            Optional<ContainerPacket.ContainerS2CPacket> packet = NbtUtils.getContainerPacket(_posl, player);

            packet.ifPresent(p -> NetworkHandler.Server.SendToClient(player, p));
        });
    }

    @Inject(method = "onDisconnected", at = @At("RETURN"))
    private void duck$onDisconnected(CallbackInfo c){
        ServerPlayerManager.INSTANCE().playerProperties.remove(getPlayer().getUuid());
    }
}
