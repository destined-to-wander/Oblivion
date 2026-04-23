package destinedtowander.oblivion.common.block.entity;

import destinedtowander.oblivion.common.block.AccumulateBlock;
import destinedtowander.oblivion.common.block.AccumulateStage;
import net.minecraft.block.*;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AccumulateBlockEntity extends BrushableBlockEntity {
    private int brushesCount;
    private long nextDustTime;
    private long nextBrushTime;
    private ItemStack item = ItemStack.EMPTY; //new ItemStack(OblivionItems.STARDUST);
    @Nullable
    private Direction hitDirection;

    public AccumulateBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, AccumulateBlockEntity blockEntity) {
    }

    @Override
    public boolean brush(long worldTime, PlayerEntity player, Direction hitDirection) {
        if (this.hitDirection == null)  this.hitDirection = hitDirection;

        this.nextDustTime = worldTime + 40L;
        if (worldTime >= this.nextBrushTime && this.world instanceof ServerWorld) {
            this.nextBrushTime = worldTime + 10L;
            int i = this.getDustedLevel();
            if (++this.brushesCount >= 10) {
                this.finishBrushing(player);
                return true;
            }
            this.world.scheduleBlockTick(this.getPos(), this.getCachedState().getBlock(), 40);
            int j = this.getDustedLevel();
            if (i != j) {
                BlockState blockState = this.getCachedState();
                BlockState blockState2 = blockState.with(Properties.DUSTED, j);
                this.world.setBlockState(this.getPos(), blockState2, Block.NOTIFY_ALL);
            }
            return false;
        }
        return false;
    }

    private void finishBrushing(PlayerEntity player) {
        if (this.world != null && this.world.getServer() != null) {
            this.spawnItem(player);
            BlockState blockState = this.getCachedState();
            this.world.syncWorldEvent(WorldEvents.BLOCK_FINISHED_BRUSHING, this.getPos(), Block.getRawIdFromState(blockState));
            this.world.setBlockState(this.pos, this.getCachedState().with(AccumulateBlock.ACCUMULATION_LEVEL, AccumulateStage.NEUTRAL), Block.NOTIFY_ALL);
        }
    }

    private void spawnItem(PlayerEntity player) {
        if (this.world != null && this.world.getServer() != null) {
            this.generateItem(player);
            double d = EntityType.ITEM.getWidth();
            double e = 1.0 - d;
            double f = d / 2.0;
            Direction direction = Objects.requireNonNullElse(this.hitDirection, Direction.UP);
            BlockPos blockPos = this.pos.offset(direction, 1);
            double g = blockPos.getX() + 0.5 * e + f;
            double h = blockPos.getY() + 0.5 + EntityType.ITEM.getHeight() / 2.0F;
            double i = blockPos.getZ() + 0.5 * e + f;
            ItemEntity itemEntity = new ItemEntity(this.world, g, h, i, this.item.split(this.world.random.nextInt(21) + 10));
            itemEntity.setVelocity(Vec3d.ZERO);
            this.world.spawnEntity(itemEntity);
            this.item = ItemStack.EMPTY;
        }
    }

    public void scheduledTick() {
        if (this.world != null) {
            if (this.brushesCount != 0 && this.world.getTime() >= this.nextDustTime) {
                int i = this.getDustedLevel();
                this.brushesCount = Math.max(0, this.brushesCount - 2);
                int j = this.getDustedLevel();
                if (i != j) {
                    this.world.setBlockState(this.getPos(), this.getCachedState().with(Properties.DUSTED, j), Block.NOTIFY_ALL);
                }

                this.nextDustTime = this.world.getTime() + 4L;
            }

            if (this.brushesCount == 0) {
                this.hitDirection = null;
                this.nextDustTime = 0L;
                this.nextBrushTime = 0L;
            } else {
                this.world.scheduleBlockTick(this.getPos(), this.getCachedState().getBlock(), (int)(this.nextDustTime - this.world.getTime()));
            }
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = super.toInitialChunkDataNbt();
        if (this.hitDirection != null) {
            nbtCompound.putInt("hit_direction", this.hitDirection.ordinal());
        }

        nbtCompound.put("item", this.item.writeNbt(new NbtCompound()));
        return nbtCompound;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    private int getDustedLevel() {
        if (this.brushesCount == 0) {
            return 0;
        } else if (this.brushesCount < 3) {
            return 1;
        } else {
            return this.brushesCount < 6 ? 2 : 3;
        }
    }

    @Nullable
    public Direction getHitDirection() {
        return this.hitDirection;
    }

    public ItemStack getItem() {
        return this.item;
    }
}
