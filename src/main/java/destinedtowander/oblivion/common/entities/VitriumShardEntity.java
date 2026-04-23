package destinedtowander.oblivion.common.entities;

import destinedtowander.oblivion.common.index.OblivionDamageTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import static destinedtowander.oblivion.common.compat.PickYourPoisonCompat.addVulnerabilityEffect;

public class VitriumShardEntity extends PersistentProjectileEntity {
    public int ticksUntilRemoval = -1;

    public VitriumShardEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.setSound(this.getHitSound());
        this.setDamage(0.5F);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    protected ItemStack asItemStack() {
        return new ItemStack(Items.AIR);
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
    }

    public void tick() {
        super.tick();
        if (this.inGround) {
            if (this.ticksUntilRemoval == -1) {
                for(int i = 0; i < 8; ++i) {
                    this.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(this.getBreakItemParticle(), 1)), this.getX() + this.random.nextGaussian() / (double)20.0F, this.getY() + this.random.nextGaussian() / (double)20.0F, this.getZ() + this.random.nextGaussian() / (double)20.0F, this.random.nextGaussian() / (double)20.0F, 0.2 + this.random.nextGaussian() / (double)20.0F, this.random.nextGaussian() / (double)20.0F);
                }

                this.ticksUntilRemoval = 2;
            }

            if (this.ticksUntilRemoval > 0) {
                --this.ticksUntilRemoval;
                if (this.ticksUntilRemoval <= 0) {
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        }

        if (this.age < 10) {
            for(LivingEntity livingEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand((double)1.0F), LivingEntity::isAlive)) {
                this.onEntityHit(new EntityHitResult(livingEntity));
                this.kill();
            }
        }

    }

    protected SoundEvent getHitSound() {
        return SoundEvents.BLOCK_GLASS_BREAK;
    }

    public Item getBreakItemParticle() {
        return Items.GLASS;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();
        DamageSource damageSource2;
        if (entity2 == null) {
            damageSource2 = OblivionDamageTypes.damageSource(OblivionDamageTypes.VITRIFICATION,this, this);
        } else {
            damageSource2 = OblivionDamageTypes.damageSource(OblivionDamageTypes.VITRIFICATION,this, entity2);
            if (entity2 instanceof LivingEntity) {
                ((LivingEntity)entity2).onAttacking(entity);
            }
        }

        boolean isEnderman = entity.getType() == EntityType.ENDERMAN;
        int fireTicks = entity.getFireTicks();
        if (this.isOnFire() && !isEnderman) {
            entity.setOnFireFor(5);
        }

        if (entity.damage(damageSource2, (float)this.getDamage())) {
            if (isEnderman) return;

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;
                if (!this.getWorld().isClient && entity2 instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity, entity2);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity);
                }

                this.onHit(livingEntity);
                if (livingEntity != entity2 && livingEntity instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity && !this.isSilent()) {
                    ((ServerPlayerEntity)entity2).networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, 0.0F));
                }

                livingEntity.timeUntilRegen = 0;
                if (FabricLoader.getInstance().isModLoaded("pickyourpoison"))
                    addVulnerabilityEffect(livingEntity, entity2, 600);
                if (entity2 != null)
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600), entity2);
                else
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 600));
            }
        } else {
            entity.setFireTicks(fireTicks);
            this.setVelocity(this.getVelocity().multiply(-0.1));
            this.setYaw(this.getYaw() + 180.0F);
            this.prevYaw += 180.0F;
            if (!this.getWorld().isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
                if (this.pickupType == PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1F);
                }
                this.discard();
            }
        }

        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, SoundCategory.NEUTRAL, 1.0F, 1.5F);
    }
}