package net.VrikkaDuck.duck.world.client;

import fi.dy.masa.malilib.util.LayerRange;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;

import java.util.function.Predicate;

public class DuckRaycastContext extends RaycastContext{
    private final Vec3d start;
    private final Vec3d end;
    private final DuckShapeType shapeType;
    private final net.minecraft.world.RaycastContext.FluidHandling fluid;
    private final ShapeContext shapeContext;
    private final LayerRange layerRange;

    public DuckRaycastContext(Vec3d start, Vec3d end, DuckShapeType shapeType, net.minecraft.world.RaycastContext.FluidHandling fluidHandling, Entity entity, LayerRange layerRange) {
        this(start, end, shapeType, fluidHandling, ShapeContext.of(entity), layerRange);
    }

    public DuckRaycastContext(Vec3d start, Vec3d end, DuckShapeType shapeType, net.minecraft.world.RaycastContext.FluidHandling fluidHandling, ShapeContext shapeContext, LayerRange layerRange) {
        super(start, end, RaycastContext.ShapeType.OUTLINE, fluidHandling, shapeContext);
        this.start = start;
        this.end = end;
        this.shapeType = shapeType;
        this.fluid = fluidHandling;
        this.shapeContext = shapeContext;
        this.layerRange = layerRange;
    }

    public Vec3d getEnd() {
        return this.end;
    }

    public Vec3d getStart() {
        return this.start;
    }

    @Override
    public VoxelShape getBlockShape(BlockState state, BlockView world, BlockPos pos) {
        return this.shapeType.get(state, world, pos, this.shapeContext, this.layerRange);
    }

    public VoxelShape getFluidShape(FluidState state, BlockView world, BlockPos pos) {
        return this.fluid.handled(state) ? state.getShape(world, pos) : VoxelShapes.empty();
    }

    public static interface DuckShapeProvider {
        public VoxelShape get(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext, LayerRange layerRange);
    }

    public static VoxelShape getOutlineWithLayerShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext) {
        return blockState.getOutlineShape(blockView, blockPos);
    }

    public static VoxelShape getOutlineWithLayerShape(BlockState blockState, BlockView blockView, BlockPos blockPos, ShapeContext shapeContext, LayerRange layerRange) {
        if (layerRange.isPositionWithinRange(blockPos)) {
            return blockState.getOutlineShape(blockView, blockPos);
        }
        return VoxelShapes.empty();
    }

    public static final class DuckShapeType implements DuckShapeProvider {

        public static final DuckShapeType OUTLINE_WITH_LAYER_RANGE = new DuckShapeType(DuckRaycastContext::getOutlineWithLayerShape);

        private final DuckShapeProvider provider;

        public DuckShapeType(DuckShapeProvider provider) {
            this.provider = provider;
        }

        @Override
        public VoxelShape get(BlockState state, BlockView world, BlockPos pos, ShapeContext context, LayerRange layerRange) {
            return this.provider.get(state, world, pos, context, layerRange);
        }
    }
}