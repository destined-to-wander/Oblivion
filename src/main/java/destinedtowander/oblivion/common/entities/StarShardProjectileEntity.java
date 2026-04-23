package destinedtowander.oblivion.common.entities;

import destinedtowander.oblivion.common.index.OblivionEntities;
import ladysnake.blast.common.entity.BombEntity;
import ladysnake.blast.common.world.CustomExplosion;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;

import java.util.List;

public class StarShardProjectileEntity extends PersistentProjectileEntity {
    private static final TrackedData<Integer> PROJECTILE_VARIANT = DataTracker.registerData(StarShardProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private int bounces = 3;
    private int serverAge = 0;

    @Unique
    public final TrailPointBuilder trailPointBuilder = TrailPointBuilder.create(20);

    public StarShardProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public StarShardProjectileEntity(World world, LivingEntity owner) {
        super(OblivionEntities.STAR_SHARD, owner, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(PROJECTILE_VARIANT, 0);
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();

        if (type == HitResult.Type.MISS || (type == HitResult.Type.ENTITY && ((EntityHitResult)hitResult).getEntity() instanceof StarShardProjectileEntity)) return;
        this.explode();
        if (getVariant() == Variant.RICOCHET && this.bounces > 0){
            this.bounces -= 1;
            if (type == HitResult.Type.BLOCK) {
                Direction side = ((BlockHitResult)hitResult).getSide();
                double newX = this.getVelocity().x;
                double newY = this.getVelocity().y;
                double newZ = this.getVelocity().z;
                switch (side) {
                    case EAST,WEST -> newX *= -1;
                    case UP,DOWN -> newY *= -1;
                    case NORTH,SOUTH -> newZ *= -1;
                }
                this.setVelocity(new Vec3d(newX, newY, newZ));
            }
        } else this.discard();
    }

    public void explode() {
        StarShardExplosion explosion = new StarShardExplosion(this, this.bounces + 1, getVariant());
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(true);
        if (!this.getWorld().isClient()) {
            for(PlayerEntity playerEntity : this.getWorld().getPlayers()) {
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerEntity;
                if (serverPlayerEntity.squaredDistanceTo(this.getX(), this.getY(), this.getZ()) < (double)4096.0F) {
                    serverPlayerEntity.networkHandler.sendPacket(new ExplosionS2CPacket(
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        explosion.getPower(),
                        explosion.getAffectedBlocks(),
                        explosion.getAffectedPlayers().get(serverPlayerEntity))
                    );
                }
            }
        }
    }

    @Override
    public void tick() {
        Vec3d position = this.getCameraPosVec(MinecraftClient.getInstance().getTickDelta()).add(0, -.1f, 0f);
        trailPointBuilder.addTrailPoint(position);
        trailPointBuilder.tickTrailPoints();
        super.tick();
        if (!this.getWorld().isClient() && (getVariant() == Variant.DESOLATED || getVariant() == Variant.DESOLATED_DESTRUCTIVE) && age % 10.0F == 0) {
            StarShardProjectileEntity fragmentEntity = new StarShardProjectileEntity(this.getWorld(), (LivingEntity)this.getOwner());
            if (getVariant().destructive) fragmentEntity.setVariant(Variant.DESOLATED_SHARD_DESTRUCTIVE);
            else fragmentEntity.setVariant(Variant.DESOLATED_SHARD);
            fragmentEntity.setBounces(2);
            fragmentEntity.setPosition(this.getPos());
            this.getWorld().spawnEntity(fragmentEntity);
        }
    }

    public List<TrailPoint> getPastPositions() {
        return trailPointBuilder.getTrailPoints();
    }

    public Variant getVariant() {
        return Variant.fromId(this.dataTracker.get(PROJECTILE_VARIANT));
    }

    public void setVariant(Variant mode) {
        this.dataTracker.set(PROJECTILE_VARIANT, mode.ordinal());
    }

    public void setBounces(int bounces) {
        this.bounces = bounces;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Variant", this.dataTracker.get(PROJECTILE_VARIANT));
        nbt.putInt("Bounces", bounces);
        nbt.putInt("Age", age);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setVariant(Variant.fromId(nbt.getInt("Variant")));
        bounces = nbt.getInt("Bounces");
        age = nbt.getInt("Age");
    }

    public enum Variant{
        NORMAL("6F15D6", true),
        VITRIFIED("36FFEE"),
        DESOLATED("D12C2C",true),
        DESOLATED_SHARD("E35B76",true),
        RICOCHET("43E070"),
        NORMAL_DESTRUCTIVE("6F15D6",true, true),
        DESOLATED_DESTRUCTIVE("D12C2C",true, true),
        DESOLATED_SHARD_DESTRUCTIVE("E35B76",true, true);

        public final boolean destructive;
        public final boolean firestarter;
        public final String color;

        Variant(String color, boolean destructive, boolean firestarter) {
            this.destructive = destructive;
            this.firestarter = firestarter;
            this.color = "0x".concat(color);
        }

        Variant(String color, boolean firestarter) {
            this(color, false, firestarter);
        }

        Variant(String color) {
            this(color, false, false);
        }

        public static Variant fromId(int id) {
            return values()[MathHelper.clamp(id, 0, values().length - 1)];
        }
    }
}
