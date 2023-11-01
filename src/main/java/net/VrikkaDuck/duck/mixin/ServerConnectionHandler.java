package net.VrikkaDuck.duck.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.IConfigValue;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.IServerLevel;
import net.VrikkaDuck.duck.config.ServerConfigs;
import net.VrikkaDuck.duck.config.options.ServerDouble;
import net.VrikkaDuck.duck.config.options.ServerLevel;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
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
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerConnectionHandler {
    @Shadow private ServerPlayerEntity player;
    @Shadow @Final private MinecraftServer server;

    private Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();
    private float currentFurnaceXp = 0.0f;

    @Inject(at = @At("RETURN"), method = "onCustomPayload")
    private void onCustomPayload(CustomPayloadC2SPacket pak, CallbackInfo cb) {
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(pak.getChannel(), new PacketByteBuf(pak.getData().copy()));

        NetworkThreadUtils.forceMainThread(packet, ((ServerPlayNetworkHandler) (Object) this),
                ((ServerPlayNetworkHandler) (Object) this).player.getServerWorld());

        if (handleCustomPayload(packet)) {
            sendServerConfigsToPlayer(player);
        }
    }

    @Unique
    private boolean handleCustomPayload(CustomPayloadC2SPacket packet) {
        Identifier channel = packet.getChannel();

        if (channel.equals(Variables.GENERICID) || channel.equals(Variables.ADMINID)) {
            PacketByteBuf buf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
            buf.writeVarInt(channel.equals(Variables.GENERICID) ? 0 : 1);
            NbtCompound nbt = new NbtCompound();

            for (IServerLevel base : ServerConfigs.Generic.OPTIONS) {
                if (base instanceof ServerLevel) {
                    ServerLevel sbase = (ServerLevel) base;
                    nbt.putBoolean(sbase.getName(), sbase.getBooleanValue());
                    nbt.putInt(sbase.getName() + ",level", sbase.getPermissionLevel());
                }else if(base instanceof ServerDouble){
                    ServerDouble sbase = (ServerDouble) base;
                    nbt.putDouble(sbase.getName(), sbase.getDoubleValue());
                }
            }

            buf.writeNbt(nbt);
            send(player.networkHandler, channel, buf);
            return true;
        } else if (channel.equals(Variables.ADMINSETID)) {
            PacketByteBuf buf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
            buf.writeVarInt(2);
            NbtCompound compound = packet.getData().readNbt();

            if (!compound.isEmpty()) {
                String name = packet.getData().readString();

                if (player.hasPermissionLevel(Variables.PERMISSIONLEVEL)) {
                    List<IServerLevel> _list = new ArrayList<>();
                    for (IServerLevel base : ServerConfigs.Generic.OPTIONS) {
                        /*System.out.println(
                                base.getName() + " ServerLevel:" + String.valueOf(base instanceof ServerLevel)
                                + " ServerDouble:" + String.valueOf(base instanceof ServerDouble));*/
                        if (base instanceof ServerLevel sbase) {

                            if(base.getName().equals(name)){
                                boolean value = compound.getBoolean(name);
                                int pvalue = compound.getInt("level");

                                sbase.setBooleanValue(value);
                                sbase.setPermissionLevel(pvalue);
                                _list.add(sbase);
                                continue;
                            }

                            _list.add(sbase);
                        }else if(base instanceof ServerDouble sbase){

                            if(base.getName().equals(name)){
                                double value = compound.getDouble(name);
                                sbase.setDoubleValue(value);
                                _list.add(sbase);
                                continue;
                            }
                            _list.add(sbase);
                        }
                    }
                    ServerConfigs.Generic.OPTIONS = ImmutableList.copyOf(_list);
                    ServerConfigs.saveToFile();
                    sendServerConfigsToAllPlayers();
                }
            }
            return true;
        } else if (channel.equals(Variables.ACTIONID)) {
            PacketTypes type = PacketType.identifierToType(packet.getData().readIdentifier());

            if (type != null && packet.getData().isReadable()) {
                switch (type) {
                    case CONTAINER -> handleContainerInspection(packet);
                    case FURNACE -> handleFurnaceInspection(packet);
                    case BEEHIVE -> handleBeehiveInspection(packet);
                    case PLAYERINVENTORY -> handlePlayerInventoryInspection(packet);
                    case VILLAGERTRADES -> handleVillagerTradesInspection(packet);
                    default -> Variables.LOGGER.error("Could not get a valid PacketType");
                }
            } else {
                Variables.LOGGER.error("Packet data is not readable");
            }
            return true;
        }
        return false;
    }

    @Unique
    private void sendServerConfigsToPlayer(ServerPlayerEntity player) {
        // Send server configs to a single player
        PacketByteBuf buf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
        buf.writeVarInt(0); // GENERICID

        NbtCompound nbt = new NbtCompound();
        for (IServerLevel base : ServerConfigs.Generic.OPTIONS) {
            if (base instanceof ServerLevel sbase) {
                nbt.putBoolean(sbase.getName(), sbase.getBooleanValue());
                nbt.putInt(sbase.getName() + ",level", sbase.getPermissionLevel());
            }else if(base instanceof ServerDouble sbase){
                nbt.putDouble(sbase.getName(), sbase.getDoubleValue());
            }
        }
        buf.writeNbt(nbt);
        send(player.networkHandler, Variables.GENERICID, buf);
    }

    @Unique
    private void sendServerConfigsToAllPlayers() {
        // Send server configs to all online players
        PacketByteBuf buf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
        buf.writeVarInt(1); // ADMINID

        NbtCompound nbt = new NbtCompound();
        for (IServerLevel base : ServerConfigs.Generic.OPTIONS) {
            /*System.out.println(
                    base.getName() + " ServerLevel:" + String.valueOf(base instanceof ServerLevel)
                            + " ServerDouble:" + String.valueOf(base instanceof ServerDouble));*/
            if (base instanceof ServerLevel sbase) {
                nbt.putBoolean(sbase.getName(), sbase.getBooleanValue());
                nbt.putInt(sbase.getName() + ",level", sbase.getPermissionLevel());
            }else if(base instanceof ServerDouble sbase){
                nbt.putDouble(sbase.getName(), sbase.getDoubleValue());
            }
        }
        buf.writeNbt(nbt);

        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        for (ServerPlayerEntity player : players) {
            send(player.networkHandler, Variables.ADMINID, buf);
        }
    }

    @Unique
    private void send(PacketByteBuf packet, Identifier channel, PacketByteBuf buf) {
        buf.writeBytes(packet, packet.writerIndex());
        sendSplitPackets(channel, buf, 1048576 - 5, buf1 -> player.networkHandler.sendPacket(new CustomPayloadS2CPacket(channel, buf1)));
    }

    @Unique
    private void sendSplitPackets(Identifier channel, PacketByteBuf packet, int payloadLimit, Consumer<PacketByteBuf> sender) {
        int len = packet.writerIndex();
        try {
            packet.resetReaderIndex();
        } catch (IndexOutOfBoundsException ignored) {
        }
        for (int offset = 0; offset < len; offset += payloadLimit) {
            int thisLen = Math.min(len - offset, payloadLimit);
            PacketByteBuf buf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer(thisLen)));
            buf.resetWriterIndex();
            if (offset == 0) {
                buf.writeVarInt(len);
            }
            buf.writeBytes(packet, thisLen);
            sender.accept(buf);
        }
        packet.release();
    }

    @Unique
    private void handleContainerInspection(CustomPayloadC2SPacket packet) {
        if (!ServerConfigs.Generic.INSPECT_CONTAINER.getBooleanValue() || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_CONTAINER.getPermissionLevel())) {
            return;
        }

        BlockPos pos = packet.getData().readBlockPos();
        BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);

        if (blockEntity == null) {
            Variables.LOGGER.warn("Could not find BlockEntity from the given position");
            return;
        }

        NbtCompound compound = blockEntity.createNbtWithId();

        if (compound.isEmpty()) {
            return;
        }

        PacketByteBuf buf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
        buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.CONTAINER));

        if (blockEntity instanceof ChestBlockEntity) {
            ChestBlockEntity sbEntity = (ChestBlockEntity) blockEntity;
            BlockState state = sbEntity.getCachedState();

            if (!state.get(ChestBlock.CHEST_TYPE).equals(ChestType.SINGLE)) {
                Direction direction = ChestBlock.getFacing(state);
                ChestBlockEntity doubleChest = (ChestBlockEntity) player.getWorld().getBlockEntity(sbEntity.getPos().offset(direction, 1));

                if (state.get(ChestBlock.CHEST_TYPE).equals(ChestType.RIGHT)) {
                    compound = getDoubleChestNbt(sbEntity.createNbtWithId(), doubleChest.createNbtWithId());
                } else {
                    compound = getDoubleChestNbt(doubleChest.createNbtWithId(), sbEntity.createNbtWithId());
                }

                buf.writeNbt(compound);
                buf.writeVarInt(ContainerType.DOUBLE_CHEST.Value);
            } else {
                buf.writeNbt(compound);
                buf.writeVarInt(ContainerType.CHEST.Value);
            }
        } else if (blockEntity instanceof HopperBlockEntity) {
            buf.writeNbt(compound);
            buf.writeVarInt(ContainerType.HOPPER.Value);
        } else if (blockEntity instanceof DispenserBlockEntity || blockEntity instanceof DropperBlockEntity) {
            buf.writeNbt(compound);
            buf.writeVarInt(ContainerType.DISPENSER.Value);
        } else {
            buf.writeNbt(compound);
            buf.writeVarInt(ContainerType.SHULKER.Value);
        }

        send(player.networkHandler, Variables.ACTIONID, buf);
    }

    @Unique
    private void handleFurnaceInspection(CustomPayloadC2SPacket packet) {
        if (!ServerConfigs.Generic.INSPECT_FURNACE.getBooleanValue() || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_FURNACE.getPermissionLevel())) {
            return;
        }

        BlockPos fpos = packet.getData().readBlockPos();
        BlockEntity fblockEntity = player.getWorld().getBlockEntity(fpos);

        if (fblockEntity == null) {
            Variables.LOGGER.error("Could not find BlockEntity from the given position");
            return;
        }

        NbtCompound fcompound = fblockEntity.createNbtWithId();

        if (fcompound.isEmpty()) {
            return;
        }

        if (fblockEntity instanceof AbstractFurnaceBlockEntity) {
            recipesUsed = new Object2IntOpenHashMap<>();
            NbtCompound NBT = fcompound.getCompound("RecipesUsed");

            NBT.getKeys().forEach((String string) -> recipesUsed.put(new Identifier(string), NBT.getInt(string)));

            List<Recipe<?>> list = Lists.newArrayList();
            currentFurnaceXp = 0.0f;

            recipesUsed.object2IntEntrySet().forEach(entry -> {
                player.getWorld().getRecipeManager().get(entry.getKey()).ifPresent(recipe -> {
                    list.add(recipe);
                    currentFurnaceXp += entry.getIntValue() * ((AbstractCookingRecipe) recipe).getExperience();
                });
            });

            fcompound.putFloat("xp", currentFurnaceXp);
        } else {
            Variables.LOGGER.error("Could not get furnace");
        }

        PacketByteBuf fbuf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
        fbuf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.FURNACE));
        fbuf.writeNbt(fcompound);

        send(player.networkHandler, Variables.ACTIONID, fbuf);
    }

    @Unique
    private void handleBeehiveInspection(CustomPayloadC2SPacket packet) {
        if (!ServerConfigs.Generic.INSPECT_BEEHIVE.getBooleanValue() || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_BEEHIVE.getPermissionLevel())) {
            return;
        }

        BlockPos beepos = packet.getData().readBlockPos();
        BlockEntity beeblockEntity = player.getWorld().getBlockEntity(beepos);

        if (!(beeblockEntity instanceof BeehiveBlockEntity)) {
            return;
        }

        BeehiveBlockEntity bbe = (BeehiveBlockEntity) beeblockEntity;
        int honeyLevel = BeehiveBlockEntity.getHoneyLevel(bbe.getCachedState());
        int beeCount = bbe.getBeeCount();

        NbtCompound beecompound = new NbtCompound();
        beecompound.putInt("HoneyLevel", honeyLevel);
        beecompound.putInt("BeeCount", beeCount);

        PacketByteBuf beebuf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
        beebuf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.BEEHIVE));
        beebuf.writeNbt(beecompound);

        send(player.networkHandler, Variables.ACTIONID, beebuf);
    }
    @Unique
    private void handlePlayerInventoryInspection(CustomPayloadC2SPacket packet) {
        if (!ServerConfigs.Generic.INSPECT_PLAYER_INVENTORY.getBooleanValue() || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_PLAYER_INVENTORY.getPermissionLevel())) {
            return;
        }

        PlayerEntity splayer = getPlayer().getWorld().getPlayerByUuid(packet.getData().readUuid());

        if (splayer == null) {
            Variables.LOGGER.warn("Couldn't find the targeted player");
            return;
        }

        if (!splayer.getPos().isInRange(player.getPos(), 5)) {
            Variables.LOGGER.warn("Targeted player is too far");
            return;
        }

        NbtList list = new NbtList();
        list = splayer.getInventory().writeNbt(list);
        NbtCompound playerInvCompound = new NbtCompound();
        playerInvCompound.put("Inventory", list);

        PacketByteBuf invBuf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
        invBuf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.PLAYERINVENTORY));
        invBuf.writeNbt(playerInvCompound);

        send(player.networkHandler, Variables.ACTIONID, invBuf);
    }

    @Unique
    private void handleVillagerTradesInspection(CustomPayloadC2SPacket packet) {
        if (!ServerConfigs.Generic.INSPECT_VILLAGER_TRADES.getBooleanValue() || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_VILLAGER_TRADES.getPermissionLevel())) {
            return;
        }

        int id = packet.getData().readInt();
        Entity e = player.getWorld().getEntityById(id);

        if (!(e instanceof VillagerEntity)) {
            return;
        }

        VillagerEntity ve = (VillagerEntity) e;

        PacketByteBuf veBuf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
        veBuf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.VILLAGERTRADES));
        ve.getOffers().toPacket(veBuf);

        send(player.networkHandler, Variables.ACTIONID, veBuf);
    }

    @Unique
    private NbtCompound getDoubleChestNbt(NbtCompound first, NbtCompound second) {
        NbtCompound a = new NbtCompound();
        NbtList list = first.getList("Items", 10);
        NbtList sList = second.getList("Items", 10);
        for (int i = 0; i < sList.size(); i++) {
            NbtCompound c = sList.getCompound(i);
            c.putByte("Slot", (byte) (c.getByte("Slot") + 27));
            list.add(c);
        }
        a.put("Items", list);
        return a;
    }

    @Unique
    public void send(ServerPlayNetworkHandler networkHandler, Identifier channel, PacketByteBuf packet) {
        send(packet, 1048576 - 5, buf -> networkHandler.sendPacket(new CustomPayloadS2CPacket(channel, buf)));
    }
    @Unique
    private void send(PacketByteBuf packet, int payloadLimit, Consumer<PacketByteBuf> sender)
    {
        int len = packet.writerIndex();

        try {
            packet.resetReaderIndex();
        } catch (IndexOutOfBoundsException e){
        }

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

    public ServerPlayerEntity getPlayer() {
        return player;
    }
}