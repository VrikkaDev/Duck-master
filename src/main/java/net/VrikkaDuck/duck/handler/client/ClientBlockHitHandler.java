package net.VrikkaDuck.duck.handler.client;

import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.networking.ContainerType;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.packet.ContainerPacket;
import net.VrikkaDuck.duck.util.ChestUtils;
import net.VrikkaDuck.duck.util.NbtUtils;
import net.VrikkaDuck.duck.world.client.DuckRaycastContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class ClientBlockHitHandler {
    public ClientBlockHitHandler(){
    }

    private static ClientBlockHitHandler instance;
    public static ClientBlockHitHandler INSTANCE(){
        if(instance == null){
            instance = new ClientBlockHitHandler();
        }
        return instance;
    }
    private static MinecraftClient mc = MinecraftClient.getInstance();

    private BlockPos PREVIOUS_BLOCK;

    public void reload(){

        Pair<HitResult, World> tpwhr = checkTPW();

        World worlduse = mc.world;
        HitResult blockHit = mc.cameraEntity.raycast(5, 0.0F, false);
        if(tpwhr != null && blockHit.squaredDistanceTo(mc.getCameraEntity()) > tpwhr.getLeft().squaredDistanceTo(mc.getCameraEntity())){
            blockHit = tpwhr.getLeft();
            worlduse = tpwhr.getRight();
        }

        if(blockHit.getType() != HitResult.Type.BLOCK) {
            PREVIOUS_BLOCK = null;
            lookingNewBlock(null, worlduse);
            return;
        }

        BlockPos blockPos = ((BlockHitResult) blockHit).getBlockPos();
        assert worlduse != null;

        if(PREVIOUS_BLOCK == null){
            PREVIOUS_BLOCK = blockPos;
            lookingNewBlock(blockPos, worlduse);
            return;
        }

        if(PREVIOUS_BLOCK.equals(blockPos)){
            return;
        }
        PREVIOUS_BLOCK = blockPos;

        lookingNewBlock(blockPos, worlduse);

    }

    public void tick(){
        Variables.PROFILER.start("clientBlockHitHandler_reloadContainers");
        checkNewContainers(mc);
        checkUnusedContainers(mc);
        Variables.PROFILER.stop("clientBlockHitHandler_reloadContainers");

        Variables.PROFILER.start("clientBlockHitHandler_tickBlockData");
        tickBlockData();
        Variables.PROFILER.stop("clientBlockHitHandler_tickBlockData");


        Variables.PROFILER.start("clientBlockHitHandler_reloadRaycast");
        this.reload();
        Variables.PROFILER.stop("clientBlockHitHandler_reloadRaycast");
    }

    private Pair<HitResult, World> checkTPW(){
        for(Map.Entry<String, World> entry : Configs.Actions.THIRD_PARTY_WORLDS.entrySet()){

            if(entry.getKey().equals("litematica")){
                if(!Configs.Generic.LITEMATICA_SUPPORT.getBooleanValue()){
                    continue;
                }
            }

            float maxDistance = 5;
            float tickDelta = 0.0F;
            boolean includeFluids = false;

            Vec3d vec3d = Objects.requireNonNull(mc.getCameraEntity()).getCameraPosVec(tickDelta);
            Vec3d vec3d2 = mc.getCameraEntity().getRotationVec(tickDelta);
            Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);


            HitResult blockHit = entry.getValue().raycast(new DuckRaycastContext(vec3d, vec3d3, DuckRaycastContext.DuckShapeType.OUTLINE_WITH_LAYER_RANGE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, mc.getCameraEntity(), Configs.Actions.THIRD_PARTY_RENDER_LAYERS.get(entry.getKey())));

            if(blockHit.getType() == HitResult.Type.BLOCK){
                return new Pair<>(blockHit, entry.getValue());
            }
        }
        return null;
    }

    private void tickBlockData(){
        Map.Entry<NbtCompound, ContainerType> entry = Configs.Actions.WORLD_CONTAINERS.get(Configs.Actions.LOOKING_AT);

        if(entry == null){
            return;
        }

        switch (entry.getValue()){
            case BREWING_STAND -> {
                // Decrement of brewtime in client

                NbtCompound nn = entry.getKey().getCompound("BlockEntityTag");
                int btime = nn.getShort("BrewTime");

                if (btime <= 0){
                    break;
                }

                nn.remove("BrewTime");
                nn.putShort("BrewTime", (short) (btime-1));
                NbtCompound heh = new NbtCompound();
                heh.put("BlockEntityTag", nn);
                Configs.Actions.WORLD_CONTAINERS.replace(Configs.Actions.LOOKING_AT, Map.entry(heh, entry.getValue()));
            }
            default -> {}
        }
    }

    public void lookingNewBlock(BlockPos blockPos, World world){

        Configs.Actions.LOOKING_AT = blockPos;
        Configs.Actions.LOOKING_AT_BE_CLIENT.setRight(ContainerType.NONE);

        if(world == null){
            Configs.Actions.LOOKING_AT_BS = null;
            return;
        }

        if(blockPos == null){
            resetAll();
            return;
        }

        Configs.Actions.LOOKING_AT_BS = world.getBlockState(blockPos);

        if(Configs.Actions.LOOKING_AT_ENTITY != null){
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(blockPos);

        if(blockEntity == null){
            //resetAll();
            return;
        }


        ContainerType ct = ContainerType.fromBlockEntity(blockEntity);

        Configs.Actions.LOOKING_AT_BE_CLIENT.setRight(ct);

        // A COMMENT IN THIS PROJECT... THATS CRAZY
        // Checks if targeted block is one of the supported container blocks
        if(ct != ContainerType.NONE){
            resetAll();
        }



        if(ct == ContainerType.DOUBLE_CHEST){
            ChestBlockEntity chestbe = (ChestBlockEntity)blockEntity;
            BlockState bs = chestbe.getCachedState();
            if((bs.get(ChestBlock.CHEST_TYPE).equals(ChestType.RIGHT))){
                BlockPos obp = ChestUtils.getOtherChestBlockPos(world, blockPos);
                BlockEntity be = world.getBlockEntity(obp);
                if (be != null){
                    Configs.Actions.LOOKING_AT = obp;
                }else{
                    Configs.Actions.LOOKING_AT = chestbe.getPos();
                }
            }
        }

        Optional<ContainerPacket.ContainerS2CPacket> o = NbtUtils.getContainerPacket(List.of(Configs.Actions.LOOKING_AT), mc.player, world);
        NbtCompound n = new NbtCompound();
        o.ifPresent(s2CPacket -> n.put("BlockEntityTag", s2CPacket.nbtMap().get(Configs.Actions.LOOKING_AT)));
        Configs.Actions.LOOKING_AT_BE_CLIENT.setLeft(n);

        if(!Configs.Generic.INSPECT_CONTAINER.getKeybind().isKeybindHeld()){
            return;
        }

        Configs.Actions.RENDER_DOUBLE_CHEST_TOOLTIP = ct.value;


    }

    private void resetAll(){
        this.PREVIOUS_BLOCK = null;
    }

    private Instant unusedContainerLast = Instant.now();
    private void checkUnusedContainers(MinecraftClient mc){
        if(mc.player == null){
            return;
        }

        if(!Configs.Generic.INSPECT_CONTAINER.getBooleanValue()){
            return;
        }

        if(Duration.between(unusedContainerLast, Instant.now()).toMillis() > 500){
            unusedContainerLast = Instant.now();
            BlockPos ppos = mc.getCameraEntity().getBlockPos();

            // Remove containers that are further away from player than 10
            Configs.Actions.WORLD_CONTAINERS.entrySet().removeIf(entry -> !ppos.isWithinDistance(entry.getKey(), 10));
            Configs.Actions.WORLD_CONTAINERS.entrySet().removeIf(entry -> ContainerType.fromBlockEntity(mc.world.getBlockEntity(entry.getKey())) == ContainerType.NONE);
        }
    }

    private Instant containerCheck = Instant.now();
    private CompletableFuture<Void> future;
    private final AtomicBoolean found = new AtomicBoolean(false);

    private void checkNewContainers(MinecraftClient mc){

        // Don't send packets if inspect container isn't enabled
        if(!Configs.Generic.INSPECT_CONTAINER.getBooleanValue() || !Configs.Admin.INSPECT_CONTAINER.getBooleanValue()){
            return;
        }


        if(future != null && !future.isDone()){
            return;
        }

        if(found.get()){

            ContainerPacket.ContainerC2SPacket packet = new ContainerPacket.ContainerC2SPacket(mc.player.getUuid(), mc.getCameraEntity().getBlockPos());
            NetworkHandler.Client.SendToServer(packet);

            found.set(false);
        }

        future = null;

        //todo need different system...

        if(Duration.between(containerCheck, Instant.now()).toMillis() > 400){
            containerCheck = Instant.now();

            found.set(false);


            Box box = new Box(mc.getCameraEntity().getBlockPos()).expand(5);
            Stream<BlockEntity> blockEntities = BlockPos.stream(box).map(mc.world::getBlockEntity);

            future = CompletableFuture.allOf(
                    blockEntities
                            .map(blockEntity -> CompletableFuture.runAsync(() -> {
                                if(blockEntity == null){
                                    return;
                                }

                                if(ContainerType.fromBlockEntity(blockEntity).value != -1){

                                    if(Configs.Actions.WORLD_CONTAINERS.containsKey(blockEntity.getPos())){
                                        return;
                                    }

                                    found.set(true);
                                }

                            }))
                            .toArray(CompletableFuture[]::new)
            );
        }

    }
    private static enum TestShapeType implements RaycastContext.ShapeProvider {
        TEST_SHAPE_TYPE;

        @Override
        public VoxelShape get(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
            return null;
        }
    }
}
