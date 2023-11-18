package net.VrikkaDuck.duck.util;

import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.IServerLevel;
import net.VrikkaDuck.duck.config.ServerConfigs;
import net.VrikkaDuck.duck.config.options.ServerDouble;
import net.VrikkaDuck.duck.config.options.ServerLevel;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.PacketType;
import net.VrikkaDuck.duck.networking.PacketTypes;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PacketUtils {

    private static Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();
    private static float currentFurnaceXp = 0.0f;

    public static void sendServerConfigsToPlayer(ServerPlayerEntity player) {
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

    public static void sendServerConfigsToAllPlayers() {
        // Send server configs to all online players
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(1); // ADMINID

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

        List<ServerPlayerEntity> players = GameWorld.getServer().getPlayerManager().getPlayerList();
        for (ServerPlayerEntity player : players) {
            send(player.networkHandler, Variables.ADMINID, buf);
        }
    }

    public static void send(PacketByteBuf packet, Identifier channel, PacketByteBuf buf, ServerPlayerEntity player) {
        buf.writeBytes(packet, packet.writerIndex());
        sendSplitPackets(channel, buf, 1048576 - 5, buf1 -> player.networkHandler.sendPacket(new CustomPayloadS2CPacket(channel, buf1)));
    }

    public static void sendSplitPackets(Identifier channel, PacketByteBuf packet, int payloadLimit, Consumer<PacketByteBuf> sender) {
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
    public static void handleContainersInspection(CustomPayloadC2SPacket packet, ServerPlayerEntity player) {
        BlockPos ppos = player.getBlockPos();

        Box box = new Box(ppos).expand(6);
        Stream<BlockEntity> blockEntities = BlockPos.stream(box).map(player.getWorld()::getBlockEntity);
        // todo make parallel?
        blockEntities.forEach(blockEntity -> {

            if(blockEntity == null){
                return;
            }

            if(ContainerType.fromBlockEntity(blockEntity).Value != -1){

                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.CONTAINER));
                buf.writeBlockPos(blockEntity.getPos());
                handleContainerInspection(new CustomPayloadC2SPacket(buf), player);
            }
        });
    }
    public static void handleContainerInspection(CustomPayloadC2SPacket packet, ServerPlayerEntity player) {
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

        boolean isright = false;

        PacketByteBuf buf = new PacketByteBuf(new PacketByteBuf(Unpooled.buffer()));
        buf.writeIdentifier(PacketType.typeToIdentifier(PacketTypes.CONTAINER));

        if (blockEntity instanceof ChestBlockEntity sbEntity) {
            BlockState state = sbEntity.getCachedState();

            if (!state.get(ChestBlock.CHEST_TYPE).equals(ChestType.SINGLE)) {
                Direction direction = ChestBlock.getFacing(state);
                ChestBlockEntity doubleChest = (ChestBlockEntity) player.getWorld().getBlockEntity(sbEntity.getPos().offset(direction, 1));

                if (state.get(ChestBlock.CHEST_TYPE).equals(ChestType.RIGHT)) {
                    isright = true;
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

        if(isright){
            buf.writeBlockPos(ChestUtils.getOtherChestBlockPos(player.getServerWorld(), blockEntity.getPos()));
        }else{
            buf.writeBlockPos(blockEntity.getPos());
        }

        send(player.networkHandler, Variables.ACTIONID, buf);
    }
    public static void handleFurnaceInspection(CustomPayloadC2SPacket packet, ServerPlayerEntity player) {

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

    public static void handleBeehiveInspection(CustomPayloadC2SPacket packet, ServerPlayerEntity player) {
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
    public static void handlePlayerInventoryInspection(CustomPayloadC2SPacket packet, ServerPlayerEntity player) {
        if (!ServerConfigs.Generic.INSPECT_PLAYER_INVENTORY.getBooleanValue() || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_PLAYER_INVENTORY.getPermissionLevel())) {
            return;
        }

        PlayerEntity splayer = player.getWorld().getPlayerByUuid(packet.getData().readUuid());

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

    public static void handleVillagerTradesInspection(CustomPayloadC2SPacket packet, ServerPlayerEntity player) {
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

    public static NbtCompound getDoubleChestNbt(NbtCompound first, NbtCompound second) {
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

    public static void send(ServerPlayNetworkHandler networkHandler, Identifier channel, PacketByteBuf packet) {
        packet.writeString(Variables.MODVERSION);
        send(packet, 1048576 - 5, buf -> networkHandler.sendPacket(new CustomPayloadS2CPacket(channel, buf)));
    }
    public static void send(PacketByteBuf packet, int payloadLimit, Consumer<PacketByteBuf> sender)
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
}
