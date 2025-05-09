package net.revalorise.gowmod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.revalorise.gowmod.GodOfWarMod;
import net.revalorise.gowmod.item.custom.*;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(GodOfWarMod.MOD_ID);

    public static final DeferredItem<Item> BISMUTH = ITEMS.register("bismuth",
        () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> RAW_BISMUTH = ITEMS.register("raw_bismuth",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> CHISEL = ITEMS.register("chisel",
            () -> new ChiselItem(new Item.Properties().durability(32)));

    public static final DeferredItem<Item> RADISH = ITEMS.register("radish",
            () -> new Item(new Item.Properties().food(ModFoodProperties.RADISH)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable("tooltip.gowmod.radish.tooltip"));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> FROSTFIRE_ICE = ITEMS.register("frostfire_ice",
            () -> new FuelItem(new Item.Properties(), 800));

    public static final DeferredItem<Item> STARLIGHT_ASHES = ITEMS.register("starlight_ashes",
        () -> new FuelItem(new Item.Properties(), 800));

    public static final DeferredItem<Item> TOMAHAWK = ITEMS.register("tomahawk",
        () -> new TomahawkItem(new Item.Properties().durability(1).fireResistant()));

    public static final DeferredItem<Item> LEVIATHAN_AXE = ITEMS.register("leviathan_axe",
        () -> new LeviathanAxeItem(new Item.Properties().durability(1).fireResistant().attributes(LeviathanAxeItem.createAttributes())));

    public static final DeferredItem<Item> DRAUPNIR_SPEAR = ITEMS.register("draupnir_spear",
        () -> new DraupnirItem(new Item.Properties().durability(1).fireResistant()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
