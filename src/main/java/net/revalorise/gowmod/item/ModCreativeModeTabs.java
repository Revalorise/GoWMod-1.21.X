package net.revalorise.gowmod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.revalorise.gowmod.GodOfWarMod;
import net.revalorise.gowmod.block.ModBlocks;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GodOfWarMod.MOD_ID);

    public static final Supplier<CreativeModeTab> BISMUTH_ITEM_TAB = CREATIVE_MODE_TAB.register("bismuth_items_tab",
        () -> CreativeModeTab.builder()
            .icon(() -> ModItems.BISMUTH.get().getDefaultInstance())
            .title(Component.translatable("Bismuth Items"))
            .displayItems((featureFlagSet, output) -> {
                output.accept(ModItems.BISMUTH.get());
                output.accept(ModItems.RAW_BISMUTH.get());
            })
            .build());

    public static final Supplier<CreativeModeTab> BISMUTH_BLOCK_TAB = CREATIVE_MODE_TAB.register("bismuth_blocks_tab",
        () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(ModBlocks.BISMUTH_BLOCK))
            .withTabsBefore(ResourceLocation.fromNamespaceAndPath(GodOfWarMod.MOD_ID, "bismuth_items_tab"))
            .title(Component.translatable("Bismuth Blocks"))
            .displayItems((featureFlagSet, output) -> {
                output.accept(ModBlocks.BISMUTH_BLOCK.get());
                output.accept(ModBlocks.BISMUTH_ORE.get());
                output.accept(ModBlocks.BISMUTH_DEEPSLATE_ORE.get());
            })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
