package net.revalorise.gowmod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.revalorise.gowmod.entity.ModEntities;
import net.revalorise.gowmod.item.ModItems;

public class TomahawkProjectileEntity extends AbstractArrow {
    private int explosionPower = 1;
    private float rotation;
    public Vec2 groundedOffset;

    public TomahawkProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public TomahawkProjectileEntity(LivingEntity shooter, Level level) {
        super(ModEntities.TOMAHAWK.get(), shooter, level, new ItemStack(ModItems.TOMAHAWK.get()), null);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.TOMAHAWK.get());
    }

    public float getRenderingRotation() {
        // This method is called to get the rotation of the projectile for rendering
        rotation += 0.5f;
        if (rotation > 360) {
            rotation = 0;
        }
        return rotation;
    }

    public boolean isGrounded() {
        return inGround;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();  // The entity that was hit
        Level level = this.level(); // The level of the entity

        // Apply damage to the hit entity
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), 4);

        // --- Server-Side Logic ---
        if (!this.level().isClientSide) {
            // --- Spawn Lightning Bolt ---
            // Get the exact position of the hit entity
            Vec3 hitPos = entity.position();
            // Create a new LightningBolt entity
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level);
            // Ensure the lightning bolt was created successfully
            if (lightningBolt != null) {
                // Set the position of the lightning bolt to the hit entity's position
                lightningBolt.setPos(hitPos.x, hitPos.y, hitPos.z);
                // Add the lightning bolt entity to the world
                level.addFreshEntity(lightningBolt);
            }

            // --- End Lightning Bolt ---

            // Broadcast event (for client-side effects like particles)
            this.level().broadcastEntityEvent(this, (byte) 3);

            // Check griefing rules
            boolean flag = net.neoforged.neoforge.event.EventHooks.canEntityGrief(this.level(), this.getOwner());
            // Create explosion
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionPower, flag,
                Level.ExplosionInteraction.TRIGGER);

            // Discard the projectile after all effects are triggered
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        // Get the position of the hit block
        BlockPos blockPos = result.getBlockPos();

        // --- Server-Side Logic ---
        if (!this.level().isClientSide) {
            // --- Spawn Lightning Bolt ---
            ServerLevel serverLevel = (ServerLevel) this.level();

            // Create a new LightningBolt entity
            // EntityType.LIGHTNING_BOLT is the standard way to get the lightning bolt entity type
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightningBolt != null) {
                // Set the position of the lightning bolt to the block position
                // Add 0.5 to the x and z to center it on the block, and 1 to the y to place it just above the block.
                lightningBolt.setPos(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5);

                // Add the lightning bolt entity to the world
                serverLevel.addFreshEntity(lightningBolt);
            }

            // Broadcast event (for client-side effects like particles)
            this.level().broadcastEntityEvent(this, (byte) 3);

            // Check griefing rules
            boolean flag = net.neoforged.neoforge.event.EventHooks.canEntityGrief(this.level(), this.getOwner());
            // Create explosion
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), (float) this.explosionPower, flag,
                Level.ExplosionInteraction.TRIGGER);

            // Discard the projectile after all effects are triggered
            this.discard();
        }

        if (result.getDirection() == Direction.SOUTH) {
            groundedOffset = new Vec2(215f, 180f);
        }
        if (result.getDirection() == Direction.NORTH) {
            groundedOffset = new Vec2(215f, 0f);
        }
        if (result.getDirection() == Direction.EAST) {
            groundedOffset = new Vec2(215f, -90f);
        }
        if (result.getDirection() == Direction.WEST) {
            groundedOffset = new Vec2(215f, 90f);
        }
        if (result.getDirection() == Direction.DOWN) {
            groundedOffset = new Vec2(115f, 180f);
        }
        if (result.getDirection() == Direction.UP) {
            groundedOffset = new Vec2(285f, 180f);
        }
    }
}