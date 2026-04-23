package destinedtowander.oblivion.common.compat;

import destinedtowander.oblivion.Oblivion;
import destinedtowander.oblivion.common.index.OblivionItems;
import moriyashiine.enchancement.client.packet.PlayBrimstoneSoundPacket;
import moriyashiine.enchancement.common.enchantment.BrimstoneEnchantment;
import moriyashiine.enchancement.common.entity.projectile.BrimstoneEntity;
import moriyashiine.enchancement.common.init.*;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static destinedtowander.oblivion.Oblivion.id;

public class EnchancementCompat {
    public static boolean isBrimstoneAcceptable(Enchantment enchantment, ItemStack stack){
        return enchantment instanceof BrimstoneEnchantment && stack.isOf(OblivionItems.STARDUST_CONFLAGRATION);
    }

    public static int getBrimstoneLevel(LivingEntity entity){
        return EnchantmentHelper.getEquipmentLevel(ModEnchantments.BRIMSTONE, entity);
    }

    public static int getBrimstoneLevel(ItemStack stack){
        return EnchantmentHelper.getLevel(ModEnchantments.BRIMSTONE, stack);
    }

    public static int brimstoneDamage(float level){
        return EnchancementUtil.getBrimstoneDamage(level);
    }

    public static ItemStack brimstoneStack(){
        return BrimstoneEntity.BRIMSTONE_STACK;
    }

    @Unique
    public static SoundEvent getFireSound(int damage) {
        if (damage >= 12) {
            return ModSoundEvents.ITEM_CROSSBOW_BRIMSTONE_6;
        } else if (damage >= 10) {
            return ModSoundEvents.ITEM_CROSSBOW_BRIMSTONE_5;
        } else if (damage >= 8) {
            return ModSoundEvents.ITEM_CROSSBOW_BRIMSTONE_4;
        } else if (damage >= 6) {
            return ModSoundEvents.ITEM_CROSSBOW_BRIMSTONE_3;
        } else {
            return damage >= 4 ? ModSoundEvents.ITEM_CROSSBOW_BRIMSTONE_2 : ModSoundEvents.ITEM_CROSSBOW_BRIMSTONE_1;
        }
    }

    public static void sendBrimstoneSoundPacket(ServerPlayerEntity player, int entityId, UUID uuid) {
        PlayBrimstoneSoundPacket.send(player, entityId, uuid);
    }

    public static void drainLifeFromShooter(World world, LivingEntity shooter, int damage){
        shooter.damage(ModDamageTypes.create(world, ModDamageTypes.LIFE_DRAIN), shooter.getMaxHealth() * ((float)damage / 20.0F));
    }

    public static PersistentProjectileEntity stellarBrimstoneEntity(World world, LivingEntity shooter, int damage){
        AstralBrimstoneEntity brimstone = new AstralBrimstoneEntity(world, shooter);
        brimstone.setDamage(damage);
        brimstone.getDataTracker().set(BrimstoneEntity.FORCED_PITCH, shooter.getPitch());
        brimstone.getDataTracker().set(BrimstoneEntity.FORCED_YAW, shooter.getHeadYaw());
        return brimstone;
    }

    public static EntityType<AstralBrimstoneEntity> ASTRAL_BRIMSTONE;

    public static void init() {
        // Only called if Enchancement is loaded
        ASTRAL_BRIMSTONE = Registry.register(Registries.ENTITY_TYPE, id("brimstone"),
            FabricEntityTypeBuilder.<AstralBrimstoneEntity>create(SpawnGroup.MISC, AstralBrimstoneEntity::new)
                .dimensions(EntityType.ARROW.getDimensions())
                .build()
        );
    }

    public static void clientInit(){
        EntityRendererRegistry.register(ASTRAL_BRIMSTONE, AstralBrimstoneEntityRenderer::new);
    }


    public static class AstralBrimstoneEntity extends PersistentProjectileEntity {
        public static final ItemStack BRIMSTONE_STACK;
        public static final TrackedData<Float> DAMAGE;
        public static final TrackedData<Float> FORCED_PITCH;
        public static final TrackedData<Float> FORCED_YAW;
        private static final DustParticleEffect PARTICLE;
        public float maxY = 0.0F;
        public int ticksExisted = 0;
        private final Set<Entity> hitEntities = new HashSet();
        private final Set<Entity> killedEntities = new HashSet();

        public AstralBrimstoneEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
            super(entityType, world);
            this.ignoreCameraFrustum = true;
        }

        public AstralBrimstoneEntity(World world, LivingEntity owner) {
            super(ASTRAL_BRIMSTONE, owner, world);
            this.setPosition(owner.getX(), owner.getEyeY() - 0.3, owner.getZ());
        }

        protected ItemStack asItemStack() {
            return ItemStack.EMPTY;
        }

        @Override
        public void tick() {
            if (this.isCritical()) {this.setCritical(false);}

            this.setVelocity(Vec3d.ZERO);
            super.tick();
            ++this.ticksExisted;
            this.maxY = 0.0F;
            Vec3d start = this.getPos();

            Vec3d end;
            for(end = start.add(this.getRotationVector()); this.maxY < 256.0F; end = end.add(this.getRotationVector())) {
                ++this.maxY;
                BlockHitResult hitResult = this.getWorld().raycast(new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    if (this.getWorld().isClient) addParticles(hitResult.getPos().getX(), hitResult.getPos().getY(), hitResult.getPos().getZ());
                    break;
                }

                if (this.ticksExisted == 3) {
                    Entity owner = this.getOwner();
                    this.getWorld().getOtherEntities(owner, Box.from(hitResult.getPos()).expand(0.5F), EntityPredicates.EXCEPT_SPECTATOR.and((entity) -> this.canEntityBeHit(owner, entity))).forEach((entity) -> {
                        if (this.getWorld().isClient) addParticles(entity.getX(), entity.getRandomBodyY(), entity.getZ());
                        else {
                            double damage = this.getDamage();
                            if (entity instanceof LivingEntity living) {
                                damage *= (living.getMaxHealth() / 20.0F);
                            }

                            if (this.maxY < 16.0F) {
                                damage *=  MathHelper.lerp(this.maxY / 16.0F, 0.25F, 1.0F);
                            } else {
                                damage *= Math.min(2.0F, MathHelper.lerp((this.maxY - 16.0F) / 200.0F, 1.0F, 2.0F));
                            }

                            damage = Math.min(50.0F, damage);
                            entity.damage(ModDamageTypes.create(this.getWorld(), ModDamageTypes.BRIMSTONE, this, owner), (float)damage);
                            this.hitEntities.add(entity);
                            if (entity instanceof LivingEntity living) if (living.isDead()) this.killedEntities.add(living);
                        }

                    });
                }
                start = end;
            }

            if (!this.getWorld().isClient) {
                if (this.ticksExisted == 3) this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, end, GameEvent.Emitter.of(this));
                if (this.ticksExisted > 10) this.discard();
            }
        }

        @Override
        protected void onEntityHit(EntityHitResult entityHitResult) {
        }

        @Override
        protected void onBlockHit(BlockHitResult blockHitResult) {
        }

        public void readCustomDataFromNbt(NbtCompound nbt) {
            super.readCustomDataFromNbt(nbt);
            this.setDamage(nbt.getFloat("Damage"));
            this.dataTracker.set(FORCED_PITCH, nbt.getFloat("ForcedPitch"));
            this.dataTracker.set(FORCED_YAW, nbt.getFloat("ForcedYaw"));
            this.ticksExisted = nbt.getInt("TicksExisted");
        }

        public void writeCustomDataToNbt(NbtCompound nbt) {
            super.writeCustomDataToNbt(nbt);
            nbt.putFloat("Damage", (float)this.getDamage());
            nbt.putFloat("ForcedPitch", this.getPitch());
            nbt.putFloat("ForcedYaw", this.getYaw());
            nbt.putInt("TicksExisted", this.ticksExisted);
        }

        protected void initDataTracker() {
            super.initDataTracker();
            this.dataTracker.startTracking(DAMAGE, 0.0F);
            this.dataTracker.startTracking(FORCED_PITCH, 0.0F);
            this.dataTracker.startTracking(FORCED_YAW, 0.0F);
        }

        public float getPitch() {
            return this.dataTracker.get(FORCED_PITCH);
        }

        public float getYaw() {
            return this.dataTracker.get(FORCED_YAW);
        }

        public void setDamage(double damage) {
            this.dataTracker.set(DAMAGE, (float)damage);
        }

        public double getDamage() {
            return (double)this.dataTracker.get(DAMAGE);
        }

        private void addParticles(double x, double y, double z) {
            Oblivion.LOGGER.info("Particles");
            float range = (float)MathHelper.lerp(this.getDamage() / (double)12.0F, 0.0F, 0.3F);

            for(int i = 0; i < 9; ++i) {
                DustParticleEffect particle;
                if ((i+2) % 3 == 0) particle = new DustParticleEffect(new Vector3f(65/255.0F, 198/255.0F, 144/255.0F), 1.0F);
                else if ((i+1) % 3 == 0) particle = new DustParticleEffect(new Vector3f(72/255.0F, 21/255.0F, 126/255.0F), 1.0F);
                else particle = new DustParticleEffect(new Vector3f(53/255.0F, 100/255.0F, 170/255.0F), 1.0F);
                this.getWorld().addParticle(particle, x + (double)MathHelper.nextFloat(this.random, -range, range), y + (double)MathHelper.nextFloat(this.random, -range, range), z + (double)MathHelper.nextFloat(this.random, -range, range), MathHelper.nextFloat(this.random, -1.0F, 1.0F), MathHelper.nextFloat(this.random, -1.0F, 1.0F), MathHelper.nextFloat(this.random, -1.0F, 1.0F));
            }
        }

        private boolean canEntityBeHit(Entity owner, Entity entity) {
            if (!(entity instanceof LivingEntity) && !entity.getType().isIn(ModTags.EntityTypes.BRIMSTONE_HITTABLE)) {
                return false;
            } else {
                return !this.hitEntities.contains(entity) && entity.isAlive() && EnchancementUtil.shouldHurt(owner, entity);
            }
        }

        static {
            BRIMSTONE_STACK = new ItemStack(Items.LAVA_BUCKET);
            BRIMSTONE_STACK.getOrCreateSubNbt("enchancement").putBoolean("Brimstone", true);
            DAMAGE = DataTracker.registerData(AstralBrimstoneEntity.class, TrackedDataHandlerRegistry.FLOAT);
            FORCED_PITCH = DataTracker.registerData(AstralBrimstoneEntity.class, TrackedDataHandlerRegistry.FLOAT);
            FORCED_YAW = DataTracker.registerData(AstralBrimstoneEntity.class, TrackedDataHandlerRegistry.FLOAT);
            PARTICLE = new DustParticleEffect(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F);
        }
    }
}
