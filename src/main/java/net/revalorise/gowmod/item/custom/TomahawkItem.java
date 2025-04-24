package net.revalorise.gowmod.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.revalorise.gowmod.entity.custom.TomahawkProjectileEntity;

public class TomahawkItem extends Item {
    public TomahawkItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        /*
         * use method will execute when the player right-clicks with the item in hand
         * pLevel: The level of the world
         * pPlayer: The player who is using the item
         * pUsedHand: The hand that is being used (left or right)
         */
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        pLevel.playSound(
            null, // No specific source entity
            pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), // Play at the player's position
            SoundEvents.SNOWBALL_THROW, // Sound effect
            SoundSource.NEUTRAL, // Sound category
            0.5F, // Volume (50%)
            0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)); // Randomized pitch
        if (!pLevel.isClientSide) { // Only spawn the projectile on the server side
            TomahawkProjectileEntity tomahawkProjectile = // a new instance of the projectile entity
                new TomahawkProjectileEntity(pPlayer, pLevel);

            tomahawkProjectile.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 0F);
            pLevel.addFreshEntity(tomahawkProjectile); // Spawn projectile in the world
        }

        pPlayer.awardStat(Stats.ITEM_USED.get(this)); // Track item usage in stats
        if (!pPlayer.getAbilities().instabuild) { // If not in creative mode
            itemstack.shrink(1); // Decrease item stack size by 1
        }

        // Return the result of the interaction
        return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
    }
}
