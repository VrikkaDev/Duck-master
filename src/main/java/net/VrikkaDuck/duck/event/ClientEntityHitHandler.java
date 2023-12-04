package net.VrikkaDuck.duck.event;

import net.VrikkaDuck.duck.Variables;
import net.VrikkaDuck.duck.config.client.Configs;
import net.VrikkaDuck.duck.mixin.client.IClientWorldMixin;
import net.VrikkaDuck.duck.networking.EntityDataType;
import net.VrikkaDuck.duck.networking.NetworkHandler;
import net.VrikkaDuck.duck.networking.packet.EntityPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientEntityHitHandler {

    public ClientEntityHitHandler(){
    }

    private static ClientEntityHitHandler instance;
    public static ClientEntityHitHandler INSTANCE(){
        if(instance == null){
            instance = new ClientEntityHitHandler();
        }
        return instance;
    }
    private static MinecraftClient mc = MinecraftClient.getInstance();
    private int lastEntityId = 0;
    private final double raycastDistance = 8;

    public void reload(){

        Entity entity = mc.getCameraEntity();
        Vec3d vec3d = entity.getCameraPosVec(0);
        Vec3d vec3d2 = entity.getRotationVec(0.0F);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * raycastDistance, vec3d2.y * raycastDistance, vec3d2.z * raycastDistance);

        Box box = entity.getBoundingBox().stretch(vec3d2.multiply(raycastDistance)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.canHit(), raycastDistance);

        if(entityHitResult == null){
            Configs.Actions.LOOKING_AT_ENTITY = null;
            return;
        }
        if(entityHitResult.getType() == HitResult.Type.ENTITY){

            Configs.Actions.LOOKING_AT_ENTITY = entityHitResult.getEntity().getUuid();

            if(entityHitResult.getEntity().getId() == lastEntityId){
                return;
            }
            lastEntityId = entityHitResult.getEntity().getId();
        }else if(entityHitResult.getType() == HitResult.Type.MISS){
            Configs.Actions.LOOKING_AT_ENTITY = null;
        }

    }

    public void tick(){

        Variables.PROFILER.start("clientEntityHitHandler_reloadRaycast");
        reload();
        Variables.PROFILER.stop("clientEntityHitHandler_reloadRaycast");

        checkUnusedEntities(mc);
        checkNewEntities(mc);
    }

    private Instant unusedEntityLast = Instant.now();

    private void checkUnusedEntities(MinecraftClient mc){

        if(mc.player == null){
            return;
        }

        if(!Configs.Generic.isAnyPressed(List.of(Configs.Generic.INSPECT_CONTAINER))){
            return;
        }

        if(Duration.between(unusedEntityLast, Instant.now()).toMillis() > 1000){

            Variables.PROFILER.start("clientEntityHitHandler_checkUnusedEntities");

            unusedEntityLast = Instant.now();
            BlockPos ppos = mc.getCameraEntity().getBlockPos();

            // Remove containers that are further away from player than 10
            Configs.Actions.WORLD_ENTITIES.entrySet().removeIf(entry ->   {

                Entity entity = ((IClientWorldMixin)mc.world).duck_getEntityManager().getLookup().get(entry.getKey());

                if(entity == null || entity.getBlockPos() == null){
                    return true;
                }

                return !ppos.isWithinDistance(entity.getBlockPos(),
                        10);
            });
            Variables.PROFILER.stop("clientEntityHitHandler_checkUnusedEntities");
        }
    }

    private Instant lastEntityCheck = Instant.now();
    private CompletableFuture<Void> future;
    private final AtomicBoolean found = new AtomicBoolean(false);

    private void checkNewEntities(MinecraftClient mc){

        if(!Configs.Generic.isAnyPressed(List.of(Configs.Generic.INSPECT_CONTAINER))){
            return;
        }

        if(future != null && !future.isDone()){
            return;
        }

        if(found.get()){

            EntityPacket.EntityC2SPacket packet = new EntityPacket.EntityC2SPacket(mc.player.getUuid(), mc.getCameraEntity().getBlockPos());
            NetworkHandler.SendToServer(packet);

            found.set(false);
        }

        future = null;


        if(Duration.between(lastEntityCheck, Instant.now()).toMillis() > 400){
            lastEntityCheck = Instant.now();

            found.set(false);


            Box box = new Box(mc.getCameraEntity().getBlockPos()).expand(5);
            List<Entity> entities = mc.world.getOtherEntities(mc.player, box);

            future = CompletableFuture.allOf(
                    entities.stream()
                            .map(entity -> CompletableFuture.runAsync(() -> {
                                if(entity == null){
                                    return;
                                }

                                if(EntityDataType.fromEntity(entity) != EntityDataType.NONE){

                                    if(Configs.Actions.WORLD_ENTITIES.containsKey(entity.getUuid())){
                                        return;
                                    }

                                    found.set(true);
                                }

                            }))
                            .toArray(CompletableFuture[]::new)
            );
        }
    }

    private void resetEntity(){
        lastEntityId = 0;
    }
}
