package net.VrikkaDuck.duck.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.ServerBoolean;
import net.VrikkaDuck.duck.config.ServerConfigs;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerConnectionHandler {
    @Shadow public ServerPlayerEntity player;

    @Shadow @Final private MinecraftServer server;
    private Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap();
    private float currentFurnaceXp = 0f;

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

            if(!packet.getData().isReadable()){
                Variables.LOGGER.error("Packet data is not readable");
                return;
            }

            switch (type){
                case CONTAINER:

                    if(!ServerConfigs.Generic.INSPECT_CONTAINER.getBooleanValue()){
                        return;
                    }

                    BlockPos pos = packet.getData().readBlockPos();

                    if(player.world.getBlockEntity(pos) == null){
                        Variables.LOGGER.error("Could not find BlockEntity from given position");
                        return;
                    }

                    BlockEntity blockEntity = player.world.getBlockEntity(pos);

                    NbtCompound compound = blockEntity.createNbtWithId();

                    if(compound.isEmpty()) {
                        return;
                    }

                    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                    buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.CONTAINER));

                    if(blockEntity instanceof ChestBlockEntity){
                        ChestBlockEntity sbEntity = (ChestBlockEntity) blockEntity;
                        BlockState state = sbEntity.getCachedState();
                        if(!state.get(ChestBlock.CHEST_TYPE).equals(ChestType.SINGLE)){
                            Direction direction = ChestBlock.getFacing(state);
                            ChestBlockEntity doubleChest = (ChestBlockEntity) player.world.getBlockEntity(sbEntity.getPos().offset(direction, 1));
                            if(state.get(ChestBlock.CHEST_TYPE).equals(ChestType.RIGHT)){
                                compound = getDoubleChestNbt(sbEntity.createNbtWithId(), doubleChest.createNbtWithId());
                            }else{
                                compound = getDoubleChestNbt(doubleChest.createNbtWithId(), sbEntity.createNbtWithId());
                            }
                            buf.writeNbt(compound);
                            buf.writeVarInt(1);
                        }else{
                            buf.writeNbt(compound);
                            buf.writeVarInt(0);
                        }

                    }else if(blockEntity instanceof HopperBlockEntity){
                        buf.writeNbt(compound);
                        buf.writeVarInt(2);
                    }else{
                        buf.writeNbt(compound);
                        buf.writeVarInt(0);
                    }


                    player.networkHandler.getConnection().send(new CustomPayloadS2CPacket(Variables.ACTIONID, buf));

                    break;
                case FURNACE:

                    if(!ServerConfigs.Generic.INSPECT_FURNACE.getBooleanValue()){
                        return;
                    }

                    BlockPos fpos = packet.getData().readBlockPos();

                    if(player.world.getBlockEntity(fpos) == null){
                        Variables.LOGGER.error("Could not find BlockEntity from given position");
                        return;
                    }

                    BlockEntity fblockEntity = player.world.getBlockEntity(fpos);

                    NbtCompound fcompound = fblockEntity.createNbtWithId();

                    if(fcompound.isEmpty()) {
                        return;
                    }

                    if(fblockEntity instanceof AbstractFurnaceBlockEntity){
                        //TODO: make this huge mess better:/
                        recipesUsed = new Object2IntOpenHashMap();
                        NbtCompound NBT = fcompound.getCompound("RecipesUsed");
                        Iterator var3 = NBT.getKeys().iterator();
                        while(var3.hasNext()) {
                            String string = (String)var3.next();
                            this.recipesUsed.put(new Identifier(string), NBT.getInt(string));
                        }
                        List<Recipe<?>> list = Lists.newArrayList();
                        ObjectIterator var4 = this.recipesUsed.object2IntEntrySet().iterator();

                        currentFurnaceXp = 0f;

                        while(var4.hasNext()) {
                            Object2IntMap.Entry<Identifier> entry = (Object2IntMap.Entry)var4.next();
                            player.world.getRecipeManager().get((Identifier)entry.getKey()).ifPresent((recipe) -> {
                                list.add(recipe);
                                currentFurnaceXp = currentFurnaceXp + ( entry.getIntValue() * ((AbstractCookingRecipe)recipe).getExperience());
                            });
                        }

                        fcompound.putFloat("xp", currentFurnaceXp);
                    }else{
                        Variables.LOGGER.error("Couldnt get furnace");
                    }


                    PacketByteBuf fbuf = new PacketByteBuf(Unpooled.buffer());
                    fbuf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.FURNACE));
                    fbuf.writeNbt(fcompound);

                    player.networkHandler.getConnection().send(new CustomPayloadS2CPacket(Variables.ACTIONID, fbuf));
                    break;
                case BEEHIVE:

                    if(!ServerConfigs.Generic.INSPECT_BEEHIVE.getBooleanValue()){
                        return;
                    }

                    BlockPos beepos = packet.getData().readBlockPos();

                    if(player.world.getBlockEntity(beepos) == null){
                        Variables.LOGGER.error("Could not find BlockEntity from given position");
                        return;
                    }

                    BlockEntity beeblockEntity = player.world.getBlockEntity(beepos);

                    if(!(beeblockEntity instanceof BeehiveBlockEntity)){
                        return;
                    }

                    BeehiveBlockEntity bbe = (BeehiveBlockEntity)beeblockEntity;

                    int honeyLevel = BeehiveBlockEntity.getHoneyLevel(bbe.getCachedState());
                    int beeCount = bbe.getBeeCount();

                    NbtCompound beecompound = new NbtCompound();

                    beecompound.putInt("HoneyLevel", honeyLevel);
                    beecompound.putInt("BeeCount", beeCount);

                    PacketByteBuf beebuf = new PacketByteBuf(Unpooled.buffer());
                    beebuf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.BEEHIVE));
                    beebuf.writeNbt(beecompound);

                    player.networkHandler.getConnection().send(new CustomPayloadS2CPacket(Variables.ACTIONID, beebuf));

                    break;
                default:
                    Variables.LOGGER.error("Could not get viable PacketType");
                    break;
            }
        }
    }
    private NbtCompound getDoubleChestNbt(NbtCompound first, NbtCompound second){
        NbtCompound a = new NbtCompound();
        NbtList list = first.getList("Items", 10);
        NbtList sList = second.getList("Items", 10);
        for(int i = 0; i < sList.size(); i++){
            NbtCompound c = sList.getCompound(i);
            c.putByte("Slot", (byte) (c.getByte("Slot") + 27));
            list.add(c);
        }
        a.put("Items", list);
        return a;
    }
}
