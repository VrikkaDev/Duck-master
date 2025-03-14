package net.VrikkaDuck.duck.world.client;

import fi.dy.masa.malilib.util.LayerRange;
import net.VrikkaDuck.duck.config.client.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ThirdPartyRaycastableWorld {
    public final String Name;
    public World World;
    public LayerRange LayerRange;
    public ThirdPartyRaycastableWorld(String name){
        this.Name = name;
    }

    public void UpdateWorld(World world){
        this.World = world;
    }
    public void UpdateLayerRange(LayerRange layerRange){
        this.LayerRange = layerRange;
    }

    @Nullable
    public HitResult Raycast(MinecraftClient mc){
        float maxDistance = 5;
        float tickDelta = 0.0F;
        boolean includeFluids = false;

        Vec3d vec3d = Objects.requireNonNull(mc.getCameraEntity()).getCameraPosVec(tickDelta);
        Vec3d vec3d2 = mc.getCameraEntity().getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);


        HitResult blockHit = World.raycast(new DuckRaycastContext(vec3d, vec3d3, DuckRaycastContext.DuckShapeType.OUTLINE_WITH_LAYER_RANGE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, mc.getCameraEntity(), this.LayerRange));

        if(blockHit.getType() == HitResult.Type.BLOCK){
            return blockHit;
        }
        return null;
    }
}
