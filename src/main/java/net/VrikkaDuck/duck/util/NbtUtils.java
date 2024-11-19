package net.VrikkaDuck.duck.util;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.common.ServerConfigs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.PacketsC2S;
import net.VrikkaDuck.duck.networking.packet.AdminPacket;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.recipe.*;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NbtUtils {

    private static Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();
    private static float currentFurnaceXp = 0.0f;

    public static Map<BlockPos, Integer> getHashedmap(List<BlockEntity> blockEntities){
        Map<BlockPos, Integer> r = new HashMap<>();
        for(BlockEntity be : blockEntities){
            r.put(be.getPos(), be.createNbtWithId().hashCode());
        }
        return r;
    }

    public static Optional<ContainerPacket.ContainerS2CPacket> getContainerPacket(List<BlockPos> positions, PlayerEntity player, World world) {
        if (!ServerConfigs.Generic.INSPECT_CONTAINER.getBooleanValue()
                || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_CONTAINER.getPermissionLevel())
                || positions.isEmpty()) {
            return Optional.empty();
        }

        Map<BlockPos, NbtCompound> rmap = new HashMap<>();

        for(BlockPos pos : positions){
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity == null) {
                //Variables.LOGGER.warn("Could not find BlockEntity from the given position");
                rmap.put(pos, new NbtCompound());
                continue;
            }

            NbtCompound compound = blockEntity.createNbtWithId();

            if (compound.isEmpty()) {
                return Optional.empty();
            }


            BlockPos blockPos = blockEntity.getPos();
            ContainerType type = ContainerType.fromBlockEntity(blockEntity);

            switch (type){
                case DOUBLE_CHEST -> {
                    ChestBlockEntity sbEntity = (ChestBlockEntity)blockEntity;
                    BlockState state = sbEntity.getCachedState();

                    // Direction direction = ChestBlock.getFacing(state);
                    // world.getBlockEntity(sbEntity.getPos().offset(direction, 1))

                    ChestBlockEntity doubleChest = (ChestBlockEntity) world.getBlockEntity(ChestUtils.getOtherChestBlockPos(world, sbEntity.getPos()));

                    if(doubleChest == null){
                        break;
                    }

                    if (state.get(ChestBlock.CHEST_TYPE).equals(ChestType.RIGHT)) {
                        blockPos = ChestUtils.getOtherChestBlockPos(world, blockEntity.getPos());
                        compound = getDoubleChestNbt(sbEntity.createNbtWithId(), doubleChest.createNbtWithId());
                    } else {
                        blockPos = blockEntity.getPos();
                        compound = getDoubleChestNbt(doubleChest.createNbtWithId(), sbEntity.createNbtWithId());
                    }
                }

                case ENDER_CHEST -> compound = getEnderChestNbt(player, world).orElse(new NbtCompound());
                case FURNACE -> compound = getFurnaceNbt((AbstractFurnaceBlockEntity) blockEntity, player, world).orElse(new NbtCompound());
                case BEEHIVE -> compound = getBeehiveNbt((BeehiveBlockEntity) blockEntity, player, world).orElse(new NbtCompound());
                case CRAFTER -> compound = addResultRecipe(compound, player, world).orElse(new NbtCompound());
                default -> {
                }
            }

            removeExtra(compound);

            if (!compound.contains("Items") || compound.getList("Items", 10).isEmpty()) {
                NbtList lst = new NbtList();
                NbtCompound a = new NbtCompound();
                a.put("Count", NbtByte.of((byte) 1));
                lst.add(0, a);
                NbtCompound b = new NbtCompound();
                b.put("Slot", NbtByte.of((byte) 1));
                lst.add(1, b);
                NbtCompound c = new NbtCompound();
                c.put("Count", NbtString.of("minecraft:air"));
                lst.add(2, c);
                compound.put("Items", lst);
            }

            compound.putInt("containerType", type.value);

            rmap.put(blockPos, compound);
        }



        return Optional.of(new ContainerPacket.ContainerS2CPacket(player.getUuid(), rmap));
    }
    public static Optional<NbtCompound> getFurnaceNbt(AbstractFurnaceBlockEntity fblockEntity, PlayerEntity player, World world) {

        if (fblockEntity == null) {
            Variables.LOGGER.error("Could not find BlockEntity from the given position");
            return Optional.empty();
        }

        NbtCompound fcompound = fblockEntity.createNbtWithId();

        if (fcompound.isEmpty()) {
            return Optional.empty();
        }

        recipesUsed = new Object2IntOpenHashMap<>();
        NbtCompound NBT = fcompound.getCompound("RecipesUsed");

        NBT.getKeys().forEach((String string) -> recipesUsed.put(new Identifier(string), NBT.getInt(string)));

        List<Recipe<?>> list = Lists.newArrayList();
        currentFurnaceXp = 0.0f;

        recipesUsed.object2IntEntrySet().forEach(entry -> {
            world.getRecipeManager().get(entry.getKey()).ifPresent(recipe -> {
                list.add(recipe.value());

                currentFurnaceXp += entry.getIntValue() * ((AbstractCookingRecipe) recipe.value()).getExperience();
            });
        });

        fcompound.putFloat("xp", currentFurnaceXp);

        return Optional.of(fcompound);
    }

    public static Optional<NbtCompound> addResultRecipe(NbtCompound nbt, PlayerEntity player, World world){

        CraftingInventory craftingInventory = new CraftingInventory(new ScreenHandler(null, -1) {
            @Override
            public ItemStack quickMove(PlayerEntity player, int slot) {
                return null;
            }

            @Override
            public boolean canUse(PlayerEntity player) {
                return false;
            }
        }, 3, 3);

        for(NbtElement c : nbt.getList("Items", NbtList.COMPOUND_TYPE)){
            NbtCompound cc = (NbtCompound) c;
            ItemStack stak = ItemStack.fromNbt(cc);
            byte slot = cc.getByte("Slot");
            craftingInventory.setStack(slot, stak);
        }

        Optional<RecipeEntry<CraftingRecipe>> optional = world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
        if (optional.isPresent()) {
            RecipeEntry<CraftingRecipe> recipeEntry = optional.get();
            ItemStack ss = recipeEntry.value().getResult(world.getRegistryManager());

            NbtCompound cc = new NbtCompound();
            ss.writeNbt(cc);
            nbt.put("result", cc);
        }

        return Optional.of(nbt);
    }

    public static Optional<NbtCompound> getEnderChestNbt(PlayerEntity player, World world){
        if(player == null){
            return Optional.empty();
        }

        NbtCompound compound = new NbtCompound();
        compound.put("Items", player.getEnderChestInventory().toNbtList());

        return Optional.of(compound);
    }

    public static Optional<NbtCompound> getBeehiveNbt(BeehiveBlockEntity beeblockEntity, PlayerEntity player, World world) {

        if (beeblockEntity == null) {
            return Optional.empty();
        }

        BeehiveBlockEntity bbe = (BeehiveBlockEntity) beeblockEntity;
        int honeyLevel = BeehiveBlockEntity.getHoneyLevel(bbe.getCachedState());
        int beeCount = bbe.getBeeCount();

        NbtCompound beecompound = new NbtCompound();
        beecompound.putInt("HoneyLevel", honeyLevel);
        beecompound.putInt("BeeCount", beeCount);

        return Optional.of(beecompound);
    }
    public static Optional<NbtCompound> getPlayerInventoryNbt(ServerPlayerEntity target, PlayerEntity player, World world) {
        if (!ServerConfigs.Generic.INSPECT_PLAYER_INVENTORY.getBooleanValue() || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_PLAYER_INVENTORY.getPermissionLevel())) {
            return Optional.empty();
        }

        if (target == null) {
            Variables.LOGGER.warn("Couldn't find the targeted player");
            return Optional.empty();
        }

        if (!target.getPos().isInRange(target.getPos(), 5)) {
            Variables.LOGGER.warn("Targeted player is too far");
            return Optional.empty();
        }

        NbtList list = new NbtList();
        list = target.getInventory().writeNbt(list);
        NbtCompound playerInvCompound = new NbtCompound();


        playerInvCompound.put("Inventory", list);

        return Optional.of(playerInvCompound);
    }

    public static Optional<NbtCompound> getVillagerTradesNbt(VillagerEntity entity, ServerPlayerEntity player) {
        if (!ServerConfigs.Generic.INSPECT_VILLAGER_TRADES.getBooleanValue() || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_VILLAGER_TRADES.getPermissionLevel())) {
            return Optional.empty();
        }


        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(entity.getOffers().toNbt());
    }

    public static Optional<NbtCompound> getMinecartContainerNbt(AbstractMinecartEntity entity, ServerPlayerEntity player){

        if (!ServerConfigs.Generic.INSPECT_MINECART_CONTAINERS.getBooleanValue() || !player.hasPermissionLevel(ServerConfigs.Generic.INSPECT_MINECART_CONTAINERS.getPermissionLevel())) {
            return Optional.empty();
        }

        NbtCompound compound = new NbtCompound();
        if(entity instanceof HopperMinecartEntity m){
            m.writeInventoryToNbt(compound);
        }else if(entity instanceof ChestMinecartEntity m){
            m.writeInventoryToNbt(compound);
        }else {
            return Optional.empty();
        }

        removeExtra(compound);

        // Add Air in there if its empty so the renderer renders it :) i am lazy
        NbtList _list = compound.getList("Items", NbtElement.COMPOUND_TYPE);
        if(_list.isEmpty()){
            NbtCompound c = new NbtCompound();
            c.putByte("Count", (byte) 1);
            c.putByte("Slot", (byte) 1);
            c.putString("id", "minecraft:air");
            _list.add(c);
        }

        // Add BlockEntityTag so can render it with the same function as the blockentity rend
        NbtCompound tcomp = new NbtCompound();
        tcomp.put("BlockEntityTag", compound);
        compound = tcomp;

        return Optional.of(compound);
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

    private static void removeExtra(NbtCompound compound){


        if(!compound.contains("Items")){
            return;
        }

        NbtList _list = compound.getList("Items", NbtList.COMPOUND_TYPE);

        for(NbtElement element : _list){
            if(!(element instanceof NbtCompound c)){
                continue;
            }
            if(!c.contains("tag")){
                continue;
            }
            NbtCompound cc = c.getCompound("tag");
            if(!cc.contains("BlockEntityTag")){
                continue;
            }
            NbtCompound ccc = cc.getCompound("BlockEntityTag");
            if(!ccc.contains("Items")){
                continue;
            }

            ccc.remove("Items");
        }

    }
}
