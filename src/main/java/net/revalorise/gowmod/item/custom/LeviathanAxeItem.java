package net.revalorise.gowmod.item.custom;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IItemExtension;

import static net.minecraft.world.item.MaceItem.canSmashAttack;


public class LeviathanAxeItem extends Item implements IItemExtension {
    public static final float SMASH_ATTACK_FALL_THRESHOLD = 1.5F;
    private static final float SMASH_ATTACK_HEAVY_THRESHOLD = 5.0F;
    public static final float SMASH_ATTACK_KNOCKBACK_RADIUS = 3.5F;
    private static final float SMASH_ATTACK_KNOCKBACK_POWER = 0.7F;

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

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            player.sendSystemMessage(Component.literal("Leviathan Axe unleashed!"));
        }
        return InteractionResultHolder.success(stack);
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
            }
        }

        // Return the result of the super call (true if damage was dealt)
        return success;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            context.getPlayer().sendSystemMessage(Component.literal("You used the Leviathan Axe!"));
        }
        return InteractionResult.SUCCESS;
    }
}
