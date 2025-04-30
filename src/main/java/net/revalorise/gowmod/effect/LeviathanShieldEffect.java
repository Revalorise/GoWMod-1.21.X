package net.revalorise.gowmod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;


public class LeviathanShieldEffect extends MobEffect {

    public LeviathanShieldEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x3FB0D3);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        super.onEffectAdded(entity, amplifier);

        entity.addEffect(new MobEffectInstance(
            MobEffects.DAMAGE_RESISTANCE,
            2000,
            4,
            true,
            false,
            false
        ));

        entity.addEffect(new MobEffectInstance(
            MobEffects.REGENERATION,
            2000,
            4,
            true,
            false,
            false
        ));

        entity.addEffect(new MobEffectInstance(
            MobEffects.ABSORPTION,
            2000,
            4,
            true,
            false,
            false
        ));
    }

}
