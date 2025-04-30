package net.revalorise.gowmod.item.custom;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import net.revalorise.gowmod.effect.ModEffects;

import java.util.function.Predicate;


public class LeviathanAxeItem extends Item implements IItemExtension {
    public static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
    private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
    public static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5F;
    private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7F;

    private static final int LEVIATHAN_SHIELD_DURATION = 2000; // 10 seconds
    private static final int LEVIATHAN_SHIELD_AMPLIFIER = 4; // Level 3
    private static final int LEVIATHAN_SHIELD_COOLDOWN_DURATION = 200; // 10 seconds

    public LeviathanAxeItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 50.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID, 1F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND
            )
            .build();
    }


    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block.
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound(player, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENDER_DRAGON_GROWL, player.getSoundSource(), 1.0F, 1.0F);
        player.startUsingItem(hand);

        if (player.getCooldowns().isOnCooldown(itemstack.getItem())) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            player.addEffect(new MobEffectInstance(
                ModEffects.LEVIATHAN_SHIELD,
                LEVIATHAN_SHIELD_DURATION,
                LEVIATHAN_SHIELD_AMPLIFIER,
                true,
                false,
                false
            ), player);
            player.getCooldowns().addCooldown(this, LEVIATHAN_SHIELD_COOLDOWN_DURATION);
        }

        return InteractionResultHolder.success(itemstack);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean success = super.hurtEnemy(stack, target, attacker);

        if (attacker instanceof ServerPlayer player && !attacker.level().isClientSide()) {
            if (canSmashAttack(attacker)) {
                ServerLevel serverLevel = (ServerLevel)attacker.level();

                // Set impact position and prevent fall damage
                if (player.isIgnoringFallDamageFromCurrentImpulse() && player.currentImpulseImpactPos != null) {
                    if (player.currentImpulseImpactPos.y > player.position().y) {
                        player.currentImpulseImpactPos = player.position();
                    }
                } else {
                    player.currentImpulseImpactPos = player.position();
                }

                player.setIgnoreFallDamageFromCurrentImpulse(true);
                player.setDeltaMovement(player.getDeltaMovement().with(Direction.Axis.Y, 0.01F));
                player.connection.send(new ClientboundSetEntityMotionPacket(player));

                // Play appropriate sound based on fall height
                if (target.onGround()) {
                    player.setSpawnExtraParticlesOnFall(true);
                    SoundEvent soundEvent = player.fallDistance > SMASH_ATTACK_HEAVY_THRESHOLD ?
                        SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
                    serverLevel.playSound(
                        null, player.getX(), player.getY(), player.getZ(),
                        soundEvent, player.getSoundSource(), 1.0F, 1.0F
                    );
                } else {
                    serverLevel.playSound(
                        null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.MACE_SMASH_AIR, player.getSoundSource(), 1.0F, 1.0F
                    );
                }

                knockback(serverLevel, player, target);

                player.resetFallDistance();
            }
        }

        // Return the result of the super call (true if damage was dealt)
        return success;
    }

    public static boolean canSmashAttack(LivingEntity entity) {
        return entity.fallDistance > SMASH_ATTACK_FALL_THRESHOLD && !entity.isFallFlying();
    }

    private static void knockback(Level level, Player player, Entity entity) {
        level.levelEvent(2013, entity.getOnPos(), 750);
        level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(SMASH_ATTACK_KNOCKBACK_RADIUS),
                knockbackPredicate(player, entity))
            .forEach(target -> {
                Vec3 vec3 = target.position().subtract(entity.position());
                double knockbackPower = getKnockbackPower(player, target, vec3);
                Vec3 knockbackVector = vec3.normalize().scale(knockbackPower);
                if (knockbackPower > 0.0) {
                    target.push(knockbackVector.x, 0.7F, knockbackVector.z);
                    if (target instanceof ServerPlayer serverPlayer) {
                        serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
                    }
                }
            });
    }

    private static Predicate<LivingEntity> knockbackPredicate(Player player, Entity entity) {
        return target -> {
            boolean isVisible = !target.isSpectator();
            boolean isDifferentEntity = target != player && target != entity;
            boolean isNotAllied = !player.isAlliedTo(target);
            boolean isNotOwnedPet = !(target instanceof TamableAnimal pet && pet.isTame() &&
                player.getUUID().equals(pet.getOwnerUUID()));
            boolean isNotMarkerArmorStand = !(target instanceof ArmorStand stand && stand.isMarker());
            boolean isInRange = entity.distanceToSqr(target) <= Math.pow(SMASH_ATTACK_KNOCKBACK_RADIUS, 2.0);

            return isVisible && isDifferentEntity && isNotAllied && isNotOwnedPet && isNotMarkerArmorStand && isInRange;
        };
    }

    private static double getKnockbackPower(Player player, LivingEntity entity, Vec3 entityPos) {
        return (SMASH_ATTACK_KNOCKBACK_RADIUS - entityPos.length())
            * SMASH_ATTACK_KNOCKBACK_POWER
            * (double)(player.fallDistance > SMASH_ATTACK_HEAVY_THRESHOLD ? 2 : 1)
            * (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }

    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource damageSource) {
        if (damageSource.getDirectEntity() instanceof LivingEntity attacker) {
            if (!canSmashAttack(attacker)) {
                return 0.0F;
            } else {
                float fallDamage;
                float fallDistance = attacker.fallDistance;

                if (fallDistance <= 3.0F) {
                    fallDamage = 4.0F * fallDistance;
                } else if (fallDistance <= 8.0F) {
                    fallDamage = 12.0F + 2.0F * (fallDistance - 3.0F);
                } else {
                    fallDamage = 22.0F + fallDistance - 8.0F;
                }

                if (attacker.level() instanceof ServerLevel serverLevel) {
                    return fallDamage + EnchantmentHelper.modifyFallBasedDamage(
                        serverLevel, attacker.getWeaponItem(), target, damageSource, 0.0F) * fallDistance;
                } else {
                    return fallDamage;
                }
            }
        }
        return 0.0F;
    }
}
