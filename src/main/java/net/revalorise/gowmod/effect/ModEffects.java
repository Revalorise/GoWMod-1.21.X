package net.revalorise.gowmod.effect;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.revalorise.gowmod.GodOfWarMod;


public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECT =
        DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, GodOfWarMod.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> LEVIATHAN_SHIELD =
        EFFECT.register("leviathan_shield", LeviathanShieldEffect::new);

    public static final DeferredHolder<MobEffect, MobEffect> FREEZING_ABSORPTION =
        EFFECT.register("freezing_absorption", FreezingHeartAbsorptionEffect::new);

    public static void register(IEventBus eventBus) {
        EFFECT.register(eventBus);
    }
}
