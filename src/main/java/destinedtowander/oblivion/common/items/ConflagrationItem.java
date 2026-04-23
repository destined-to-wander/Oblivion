package destinedtowander.oblivion.common.items;

import com.google.common.collect.Lists;
import destinedtowander.oblivion.Oblivion;
import destinedtowander.oblivion.client.event.AstralBrimstoneRenderEvent;
import destinedtowander.oblivion.common.entities.StarShardProjectileEntity;
import destinedtowander.oblivion.common.index.OblivionEnchantments;
import destinedtowander.oblivion.common.index.OblivionItemTags;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import static destinedtowander.oblivion.common.compat.EnchancementCompat.*;

public class ConflagrationItem extends CrossbowItem implements Vanishable {
    public static final Predicate<ItemStack> CONFLAGRATION_PROJECTILES = stack -> stack.isIn(OblivionItemTags.CONFLAGRATION_ACCEPTED);
    @Unique
    private static int brimstoneTimer = -1;
    private boolean charged = false;
    private boolean loaded = false;

    public ConflagrationItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getHeldProjectiles() {
        return CONFLAGRATION_PROJECTILES;
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return CONFLAGRATION_PROJECTILES;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack weapon = user.getStackInHand(hand);
        if (isCharged(weapon)) {
            shootAll(world, user, hand, weapon, getSpeed(weapon));
            setCharged(weapon, false);
            return TypedActionResult.consume(weapon);
        } else if (!user.getProjectileType(weapon).isEmpty()) {
            if (!isCharged(weapon)) {
                this.charged = false;
                this.loaded = false;
                user.setCurrentHand(hand);
            }

            return TypedActionResult.consume(weapon);
        } else {
            return TypedActionResult.fail(weapon);
        }
    }

    private static float getSpeed(ItemStack stack) {
        return EnchantmentHelper.getLevel(OblivionEnchantments.RICOCHET, stack) > 0 ? 2.0F : 4.0F;
    }


    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (FabricLoader.getInstance().isModLoaded("enchancement") && getBrimstoneLevel(stack) > 0 && !isCharged(stack)) {
            int damage = brimstoneDamage(getPullProgress(this.getMaxUseTime(stack) - remainingUseTicks, stack));
            if (damage > 0 && loadProjectiles(user, stack)) {
                setCharged(stack, true);
                stack.getOrCreateSubNbt("enchancement").putInt("BrimstoneDamage", damage);
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
                return;
            }
        }
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = getPullProgress(i, stack);
        if (f >= 1.0F && !isCharged(stack) && loadProjectiles(user, stack)) {
            setCharged(stack, true);
            SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            world.playSound(
                    null,
                    user.getX(),
                    user.getY(),
                    user.getZ(),
                    SoundEvents.ITEM_CROSSBOW_LOADING_END,
                    soundCategory,
                    1.0F,
                    1.0F / (world.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
            );
        }
    }

    private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        boolean bl = shooter instanceof PlayerEntity player && player.getAbilities().creativeMode;
        ItemStack itemStack = shooter.getProjectileType(crossbow);
        if ((itemStack.isOf(Items.ARROW) || itemStack.isEmpty()) && bl) itemStack = new ItemStack(Items.NETHER_STAR);
        return loadProjectile(shooter, crossbow, itemStack, bl);
    }

    private static boolean loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean creative) {
        if (projectile.isEmpty()) return false;
        else {
            boolean bl = creative && projectile.isIn(OblivionItemTags.CONFLAGRATION_ACCEPTED);
            ItemStack itemStack;
            if (!bl && !creative) {
                itemStack = projectile.split(1);
                if (projectile.isEmpty() && shooter instanceof PlayerEntity player) player.getInventory().removeOne(projectile);
            } else itemStack = projectile.copy();

            putProjectile(crossbow, itemStack);
            return true;
        }
    }

    public static boolean isCharged(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.getBoolean("Charged");
    }

    public static void setCharged(ItemStack stack, boolean charged) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putBoolean("Charged", charged);
    }

    private static void putProjectile(ItemStack crossbow, ItemStack projectile) {
        NbtCompound nbtCompound = crossbow.getOrCreateNbt();
        NbtList nbtList;
        if (nbtCompound.contains("ChargedProjectiles", NbtElement.LIST_TYPE)) {
            nbtList = nbtCompound.getList("ChargedProjectiles", NbtElement.COMPOUND_TYPE);
        } else {
            nbtList = new NbtList();
        }

        NbtCompound nbtCompound2 = new NbtCompound();
        projectile.writeNbt(nbtCompound2);
        nbtList.add(nbtCompound2);
        nbtCompound.put("ChargedProjectiles", nbtList);
    }

    private static List<ItemStack> getProjectiles(ItemStack crossbow) {
        List<ItemStack> list = Lists.newArrayList();
        NbtCompound nbtCompound = crossbow.getNbt();
        if (nbtCompound != null && nbtCompound.contains("ChargedProjectiles", NbtElement.LIST_TYPE)) {
            NbtList nbtList = nbtCompound.getList("ChargedProjectiles", NbtElement.COMPOUND_TYPE);
            if (nbtList != null) {
                for (int i = 0; i < nbtList.size(); i++) {
                    NbtCompound nbtCompound2 = nbtList.getCompound(i);
                    list.add(ItemStack.fromNbt(nbtCompound2));
                }
            }
        }

        return list;
    }

    private static void clearProjectiles(ItemStack crossbow) {
        NbtCompound nbtCompound = crossbow.getNbt();
        if (nbtCompound != null) {
            NbtList nbtList = nbtCompound.getList("ChargedProjectiles", NbtElement.LIST_TYPE);
            nbtList.clear();
            nbtCompound.put("ChargedProjectiles", nbtList);
        }
    }

    public static boolean hasProjectile(ItemStack crossbow, Item projectile) {
        return getProjectiles(crossbow).stream().anyMatch(s -> s.isOf(projectile));
    }

    public static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack weapon, float speed) {
        List<ItemStack> list = getProjectiles(weapon);
        float[] fs = getSoundPitches(entity.getRandom());

        for (int i = 0; i < list.size(); i++) {
            ItemStack itemStack = list.get(i);
            if (!itemStack.isEmpty()) shoot(world, entity, hand, weapon, itemStack, fs[i], speed);
        }
        postShoot(world, entity, weapon);
    }


    private static void shoot(
        World world,
        LivingEntity shooter,
        Hand hand,
        ItemStack crossbow,
        ItemStack projectile,
        float soundPitch,
        float speed
    ) {
        if (!world.isClient) {
            boolean bl = projectile.isIn(OblivionItemTags.CONFLAGRATION_DESTRUCTIVE);
            PersistentProjectileEntity projectileEntity = createProjectile(world, shooter, crossbow, projectile);

            if (shooter instanceof CrossbowUser crossbowUser)
                crossbowUser.shoot(crossbowUser.getTarget(), crossbow, projectileEntity, 0);
             else {
                Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
                Quaternionf quaternionf = new Quaternionf().setAngleAxis(0, vec3d.x, vec3d.y, vec3d.z);
                Vec3d vec3d2 = shooter.getRotationVec(1.0F);
                Vector3f vector3f = vec3d2.toVector3f().rotate(quaternionf);
                projectileEntity.setVelocity(vector3f.x(), vector3f.y(), vector3f.z(), speed, (float) 0.5);
            }

            crossbow.damage(bl ? 1 : 3, shooter, e -> e.sendToolBreakStatus(hand));
            world.spawnEntity(projectileEntity);

            SoundEvent sound = SoundEvents.ITEM_CROSSBOW_SHOOT;
            if (brimstoneTimer >= 0) {
                sound = getFireSound(brimstoneTimer);
                brimstoneTimer = -1;
            }

            world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), sound, SoundCategory.PLAYERS, 1.0F, soundPitch);
        }
    }

    private static PersistentProjectileEntity createProjectile(World world, LivingEntity shooter, ItemStack weapon, ItemStack projectile) {
        if (FabricLoader.getInstance().isModLoaded("enchancement") && ItemStack.areEqual(projectile, brimstoneStack())) {
            NbtCompound subNbt = weapon.getSubNbt("enchancement");
            assert subNbt != null;
            brimstoneTimer = subNbt.getInt("BrimstoneDamage");
            subNbt.remove("BrimstoneDamage");
            drainLifeFromShooter(world, shooter, brimstoneTimer);
            PersistentProjectileEntity brimstone = stellarBrimstoneEntity(world, shooter, brimstoneTimer);
            if (shooter instanceof PlayerEntity player)
                player.getItemCooldownManager().set(weapon.getItem(), (int)((float)getPullTime(weapon) * ((float)brimstoneTimer / 12.0F)));
            return brimstone;
        } else {
            StarShardProjectileEntity fragmentEntity = new StarShardProjectileEntity(world, shooter);
            fragmentEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
            if (projectile.isIn(OblivionItemTags.CONFLAGRATION_DESTRUCTIVE)) {
                if (EnchantmentHelper.getLevel(OblivionEnchantments.DESOLATION, weapon) > 0)
                    fragmentEntity.setVariant(StarShardProjectileEntity.Variant.DESOLATED_DESTRUCTIVE);
                else fragmentEntity.setVariant(StarShardProjectileEntity.Variant.NORMAL_DESTRUCTIVE);
            } else {
                if (EnchantmentHelper.getLevel(OblivionEnchantments.DESOLATION, weapon) > 0)
                    fragmentEntity.setVariant(StarShardProjectileEntity.Variant.DESOLATED);
                else if (EnchantmentHelper.getLevel(OblivionEnchantments.VITRIFICATION, weapon) > 0) {
                    fragmentEntity.setVariant(StarShardProjectileEntity.Variant.VITRIFIED);
                    int bounces = EnchantmentHelper.getLevel(OblivionEnchantments.VITRIFICATION, weapon);
                    fragmentEntity.setBounces(bounces * 2);
                } else if (EnchantmentHelper.getLevel(OblivionEnchantments.RICOCHET, weapon) > 0)
                    fragmentEntity.setVariant(StarShardProjectileEntity.Variant.RICOCHET);
            }

            if (shooter instanceof PlayerEntity player && !player.isCreative()) {
                player.getItemCooldownManager().set(weapon.getItem(), (int)(getPullTime(weapon) * 1.5F));
            }

            return fragmentEntity;
        }
    }

    private static float[] getSoundPitches(Random random) {
        boolean bl = random.nextBoolean();
        return new float[]{1.0F, getSoundPitch(bl, random), getSoundPitch(!bl, random)};
    }

    private static float getSoundPitch(boolean flag, Random random) {
        float f = flag ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    private static void postShoot(World world, LivingEntity entity, ItemStack stack) {
        clearProjectiles(stack);
    }


    public static int getBrimstoneTime(float progress) {
        return (int) (5.0F * progress * 60);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        AstralBrimstoneRenderEvent.health = getBrimstoneTime(getPullProgress(this.getMaxUseTime(stack) - remainingUseTicks, stack));
        if (!world.isClient) {
            int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
            if (FabricLoader.getInstance().isModLoaded("enchancement") && getBrimstoneLevel(stack) > 0) {
                NbtCompound subNbt = stack.getOrCreateSubNbt("enchancement");
                UUID uuid;
                if (subNbt.contains("BrimstoneUUID")) {
                    uuid = subNbt.getUuid("BrimstoneUUID");
                } else {
                    uuid = UUID.randomUUID();
                    subNbt.putUuid("BrimstoneUUID", uuid);
                }

                if (remainingUseTicks == this.getMaxUseTime(stack)) {
                    PlayerLookup.tracking(user).forEach((foundPlayer) -> sendBrimstoneSoundPacket(foundPlayer, user.getId(), uuid));
                    if (user instanceof ServerPlayerEntity player) {
                        sendBrimstoneSoundPacket(player, user.getId(), uuid);
                    }
                }
            }
            SoundEvent soundEvent = this.getQuickChargeSound(i);
            SoundEvent soundEvent2 = i == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
            float f = (float)(stack.getMaxUseTime() - remainingUseTicks) / getPullTime(stack);
            if (f < 0.2F) {
                this.charged = false;
                this.loaded = false;
            }

            if (f >= 0.2F && !this.charged) {
                this.charged = true;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }

            if (f >= 0.5F && soundEvent2 != null && !this.loaded) {
                this.loaded = true;
                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent2, SoundCategory.PLAYERS, 0.5F, 1.0F);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return getPullTime(stack) + 3;
    }

    public static int getPullTime(ItemStack stack) {
        if (FabricLoader.getInstance().isModLoaded("enchancement") && getBrimstoneLevel(stack) > 0) {
            int time = 120 / getBrimstoneLevel(stack);
            return Math.max(1, time);
        }
        return 75;
    }

    private SoundEvent getQuickChargeSound(int stage) {
        return SoundEvents.ITEM_CROSSBOW_LOADING_START;
    }

    private static float getPullProgress(int useTicks, ItemStack stack) {
        float f = (float)useTicks / getPullTime(stack);
        return Math.min(f, 1.0F);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        List<ItemStack> list = getProjectiles(stack);
        if (isCharged(stack) && !list.isEmpty()) {
            ItemStack itemStack = list.get(0);
            tooltip.add(Text.translatable("item.oblivion.conflagration.projectile").append(ScreenTexts.SPACE).append(itemStack.toHoverableText()));
        }
    }

    @Override
    public int getRange() {
        return 20;
    }
}
