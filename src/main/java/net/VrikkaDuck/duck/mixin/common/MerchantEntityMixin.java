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

    @Inject(method = "trade", at = @At("RETURN"))
    private void duck$trade(TradeOffer offer, CallbackInfo ci){
        NetworkHandler.Server.SendEntityToNearby(((MerchantEntity)(Object)this));
    }
}
