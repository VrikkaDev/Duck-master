package net.VrikkaDuck.duck.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.ServerConfigs;
import net.VrikkaDuck.duck.config.options.ServerLevel;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
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
import java.util.function.Consumer;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerConnectionHandler {
    @Shadow private ServerPlayerEntity player;

    @Shadow @Final private MinecraftServer server;

    private Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap();
    private float currentFurnaceXp = 0f;

    @Inject(at = @At("RETURN"),method = "onCustomPayload")
    private void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo cb){

        NetworkThreadUtils.forceMainThread(packet, ((ServerPlayNetworkHandler)(Object)this),
                ((ServerPlayNetworkHandler)(Object)this).player.getWorld());

        if(packet.getChannel().equals(Variables.GENERICID)){
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeVarInt(0);//GENERICID

            NbtCompound nbt = new NbtCompound();

            for(ServerLevel base : ServerConfigs.Generic.OPTIONS){
                nbt.putBoolean(base.getName(), base.getBooleanValue());
                nbt.putInt(base.getName()+",level", base.getPermissionLevel());
            }

            buf.writeNbt(nbt);

            ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).getPlayer();

            send(player.networkHandler ,Variables.GENERICID, buf);

        }else if(packet.getChannel().equals(Variables.ADMINID)){
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeVarInt(1);//ADMINID

            NbtCompound nbt = new NbtCompound();

            for(ServerLevel base : ServerConfigs.Generic.OPTIONS){
                nbt.putBoolean(base.getName(), base.getBooleanValue());
                nbt.putInt(base.getName()+",level", base.getPermissionLevel());
            }

            buf.writeNbt(nbt);

            ServerPlayerEntity player = ((ServerPlayNetworkHandler)(Object)this).getPlayer();

            send(player.networkHandler ,Variables.ADMINID, buf);

        }else if(packet.getChannel().equals(Variables.ADMINSETID)){
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeVarInt(2);//ADMINSETID

            NbtCompound compound = packet.getData().readNbt();

            if(!compound.isEmpty()) {
                   // String name = compound.getKeys().stream().toList().get(0);
                String name = packet.getData().readString();
                if(name.isEmpty()){
                    Variables.LOGGER.error("Option name is empty", name);
                    return;
                }
                boolean value = compound.getBoolean(name);
                int pvalue = compound.getInt("level");
                if (player.hasPermissionLevel(Variables.PERMISSIONLEVEL)) {

                    List<ServerLevel> _list = List.copyOf(ServerConfigs.Generic.OPTIONS);

                    for (ServerLevel base : _list) {
                        if (base.getName().equals(name)) {
                            base.setBooleanValue(value);
                            base.setPermissionLevel(pvalue);
                        }
                    }
                    ServerConfigs.Generic.OPTIONS = ImmutableList.copyOf(_list);
                }

                ServerConfigs.saveToFile();

                List<ServerPlayerEntity> players = this.server.getPlayerManager().getPlayerList();

                for(ServerPlayerEntity player : players) {//TODO: check if player can recieve
                    send(player.networkHandler ,Variables.ADMINSETID, buf);
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

                    if(!ServerConfigs.Generic.INSPECT_CONTAINER.getBooleanValue()
                            || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_CONTAINER.getPermissionLevel())){
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


                    send(player.networkHandler ,Variables.ACTIONID, buf);

                    break;
                case FURNACE:

                    if(!ServerConfigs.Generic.INSPECT_FURNACE.getBooleanValue()
                            || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_FURNACE.getPermissionLevel())){
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

                    send(player.networkHandler ,Variables.ACTIONID, fbuf);
                    break;
                case BEEHIVE:

                    if(!ServerConfigs.Generic.INSPECT_BEEHIVE.getBooleanValue()
                            || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_BEEHIVE.getPermissionLevel())){
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

                    send(player.networkHandler ,Variables.ACTIONID, beebuf);

                    break;
                case PLAYERINVENTORY:
                    if(!ServerConfigs.Generic.INSPECT_PLAYER_INVENTORY.getBooleanValue()
                            || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_PLAYER_INVENTORY.getPermissionLevel())){
                        return;
                    }

                    PlayerEntity splayer = getPlayer().world.getPlayerByUuid(packet.getData().readUuid());

                    if(splayer == null){
                        Variables.LOGGER.warn("Couldnt find targeted player");
                        return;
                    }

                    if(!splayer.getPos().isInRange(player.getPos(), 5)){
                        Variables.LOGGER.warn("Targeted player is too far");
                        return;
                    }

                    NbtList list = new NbtList();
                    list = splayer.getInventory().writeNbt(list);
                    //list = player.getInventory().writeNbt(list);
                    NbtCompound playerInvCompound = new NbtCompound();
                    playerInvCompound.put("Inventory", list);

                    PacketByteBuf invBuf = new PacketByteBuf(Unpooled.buffer());
                    invBuf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.PLAYERINVENTORY));
                    invBuf.writeNbt(playerInvCompound);

                    send(player.networkHandler ,Variables.ACTIONID, invBuf);

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
    /*private void send(ServerPlayNetworkHandler networkHandler, Identifier channel, PacketByteBuf packet){
        PacketSplitter.send(networkHandler, channel, packet);
    }*/
    public void send(ServerPlayNetworkHandler networkHandler, Identifier channel, PacketByteBuf packet)
    {
        send(packet, 1048576-5, buf -> networkHandler.sendPacket(new CustomPayloadS2CPacket(channel, buf)));
    }
    private void send(PacketByteBuf packet, int payloadLimit, Consumer<PacketByteBuf> sender)
    {
        int len = packet.writerIndex();

        packet.resetReaderIndex();

        for (int offset = 0; offset < len; offset += payloadLimit)
        {
            int thisLen = Math.min(len - offset, payloadLimit);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer(thisLen));

            buf.resetWriterIndex();

            if (offset == 0)
            {
                buf.writeVarInt(len);
            }

            buf.writeBytes(packet, thisLen);

            sender.accept(buf);
        }

        packet.release();
    }

    public ServerPlayerEntity getPlayer(){
        return player;
    }
}
