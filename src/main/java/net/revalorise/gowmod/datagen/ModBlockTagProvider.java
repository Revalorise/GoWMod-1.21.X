package net.revalorise.gowmod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.revalorise.gowmod.GodOfWarMod;
import net.revalorise.gowmod.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, GodOfWarMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.BISMUTH_BLOCK.get())
            .add(ModBlocks.BISMUTH_ORE.get())
            .add(ModBlocks.BISMUTH_DEEPSLATE_ORE.get())
            .add(ModBlocks.MAGIC_BLOCK.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
            .add(ModBlocks.BISMUTH_DEEPSLATE_ORE.get());

        // tag(BlockTags.NEEDS_DIAMOND_TOOL).add(ModBlocks.BISMUTH_DEEPSLATE_ORE.get());
    }
}
