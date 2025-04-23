package net.revalorise.gowmod.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.revalorise.gowmod.GodOfWarMod;
import net.revalorise.gowmod.entity.custom.TomahawkProjectileEntity;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, GodOfWarMod.MOD_ID);

    public static final Supplier<EntityType<TomahawkProjectileEntity>> TOMAHAWK =
        ENTITY_TYPES.register("tomahawk", () -> EntityType
            .Builder.<TomahawkProjectileEntity>of(TomahawkProjectileEntity::new, MobCategory.MISC)
            .sized(0.5f, 1.15f).build("tomahawk"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
