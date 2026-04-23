package destinedtowander.oblivion.common.block;

import destinedtowander.oblivion.common.block.entity.AccumulateBlockEntity;
import destinedtowander.oblivion.common.index.OblivionBlocks;
import destinedtowander.oblivion.common.index.OblivionProperties;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class AccumulateBlock extends BlockWithEntity {
    private final Block baseBlock;
    private final SoundEvent brushingSound;
    private final SoundEvent brushingCompleteSound;

    public static final EnumProperty<AccumulateStage> ACCUMULATION_LEVEL = OblivionProperties.ACCUMULATION_LEVEL;
    private static final IntProperty DUSTED = Properties.DUSTED;

    public AccumulateBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.baseBlock = OblivionBlocks.CELESTIAL_ACCUMULATE;
        this.brushingSound = SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL;
        this.brushingCompleteSound = SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL_COMPLETE;
        this.setDefaultState(this.stateManager.getDefaultState().with(ACCUMULATION_LEVEL, AccumulateStage.NEUTRAL).with(DUSTED, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {builder.add(DUSTED, ACCUMULATION_LEVEL);}

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, 2);
    }

    public void resetStage(World world, BlockState state, BlockPos pos) {
        world.setBlockState(pos, state.with(ACCUMULATION_LEVEL, AccumulateStage.NEUTRAL), Block.NOTIFY_ALL);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleBlockTick(pos, this, 2);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBlockEntity(pos) instanceof BrushableBlockEntity brushableBlockEntity) {
            brushableBlockEntity.scheduledTick();
        }

        if (FallingBlock.canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY()) {
            FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
            fallingBlockEntity.setDestroyedOnLanding();
        }
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    public SoundEvent getBrushingSound() {
        return this.brushingSound;
    }

    public SoundEvent getBrushingCompleteSound() {
        return this.brushingCompleteSound;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(16) == 0) {
            BlockPos blockPos = pos.down();
            if (FallingBlock.canFallThrough(world.getBlockState(blockPos))) {
                double d = pos.getX() + random.nextDouble();
                double e = pos.getY() - 0.05;
                double f = pos.getZ() + random.nextDouble();
                world.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state), d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AccumulateBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : checkType(type, BlockEntityType.BRUSHABLE_BLOCK, (w, pos, s, be) -> {
            if (pos.getY() >= 318 && be instanceof AccumulateBlockEntity blockEntity) {
                AccumulateBlockEntity.serverTick(w,pos,s, blockEntity);
            }
        });
    }
}
