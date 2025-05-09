package net.revalorise.gowmod.block;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.revalorise.gowmod.GodOfWarMod;
import net.revalorise.gowmod.block.custom.MagicBlock;
import net.revalorise.gowmod.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
        DeferredRegister.createBlocks(GodOfWarMod.MOD_ID);

    public static final DeferredBlock<Block> BISMUTH_BLOCK = registerBlock("bismuth_block",
        () -> new Block(Block.Properties.of()
            .strength(3f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.METAL))
    );

    public static final DeferredBlock<Block> BISMUTH_ORE = registerBlock("bismuth_ore",
        () -> new DropExperienceBlock(UniformInt.of(2, 4),
            BlockBehaviour.Properties.of()
                .strength(3f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE))
    );

    public static final DeferredBlock<Block> BISMUTH_DEEPSLATE_ORE = registerBlock("bismuth_deepslate_ore",
        () -> new DropExperienceBlock(UniformInt.of(2, 4),
            BlockBehaviour.Properties.of()
                .strength(4f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE))
    );

    public static final DeferredBlock<Block> MAGIC_BLOCK = registerBlock("magic_block",
        () -> new MagicBlock(BlockBehaviour.Properties.of()
            .strength(2f).requiresCorrectToolForDrops()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
