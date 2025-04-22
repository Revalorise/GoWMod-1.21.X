package net.revalorise.gowmod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.level.ItemLike;
import net.revalorise.gowmod.block.ModBlocks;
import net.revalorise.gowmod.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        List<ItemLike> BISMUTH_SMELTABLES = List.of(
            ModItems.RAW_BISMUTH,
            ModBlocks.BISMUTH_ORE,
            ModBlocks.BISMUTH_DEEPSLATE_ORE
        );

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BISMUTH_BLOCK.get())
            .pattern("AAA")
            .pattern("AAA")
            .pattern("AAA")
            .define('A', ModItems.BISMUTH.get())
            .unlockedBy("has_bismuth", has(ModItems.BISMUTH)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BISMUTH.get(), 9)
                .requires(ModBlocks.BISMUTH_BLOCK)
                    .unlockedBy("has_bismuth_block", has(ModBlocks.BISMUTH_BLOCK)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BISMUTH.get(), 18)
            .requires(ModBlocks.MAGIC_BLOCK)
            .unlockedBy("has_magic_block", has(ModBlocks.MAGIC_BLOCK))
            .save(recipeOutput, "godofwarmod:bismuth_from_magic_block");

        oreSmelting(
            recipeOutput,
            BISMUTH_SMELTABLES,
            RecipeCategory.MISC,
            ModItems.BISMUTH.get(),
            0.25f, 200, "bismuth"
        );

        oreBlasting(
            recipeOutput,
            BISMUTH_SMELTABLES,
            RecipeCategory.MISC,
            ModItems.BISMUTH.get(),
            0.25f, 100, "bismuth"
        );

    }
}
