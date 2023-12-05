package net.VrikkaDuck.duck.mixin.common;

import net.VrikkaDuck.duck.networking.EntityDataType;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.ServerPlayerManager;
import net.VrikkaDuck.duck.networking.packet.EntityPacket;
import net.VrikkaDuck.duck.util.NbtUtils;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(MerchantEntity.class)
public class MerchantEntityMixin {

    @Unique
    private void sendP(){
        MerchantEntity s = ((MerchantEntity) (Object)this);

        if(!(s instanceof VillagerEntity self)){
            return;
        }

        Stream<? extends PlayerEntity> players = self.getWorld().getPlayers().stream().filter(p -> {
            Optional<Object> _fp = ServerPlayerManager.INSTANCE().getProperty(p.getUuid(), "playerLastBlockpos");
            BlockPos _p = _fp.map(o -> (BlockPos) o).orElseGet(p::getBlockPos);

            return self.getPos().isInRange(_p.toCenterPos(), 10);
        });


        for(var player : players.toList()){
            if(player instanceof ServerPlayerEntity splayer){

                NbtCompound compound = new NbtCompound();
                NbtList _l = new NbtList();
                NbtCompound tc = NbtUtils.getVillagerTradesNbt(self, splayer).orElse(new NbtCompound());

                tc.putUuid("uuid", self.getUuid());
                tc.putInt("entityType",  EntityDataType.VILLAGER_TRADES.value);
                _l.add(tc);
                compound.put("entities", _l);


                EntityPacket.EntityS2CPacket p = new EntityPacket.EntityS2CPacket(splayer.getUuid(), compound);

                NetworkHandler.SendToClient(splayer, p);
            }
        }
    }
    @Inject(method = "trade", at = @At("RETURN"))
    private void duck$trade(TradeOffer offer, CallbackInfo ci){
        sendP();
    }
}
