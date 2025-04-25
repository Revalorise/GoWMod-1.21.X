package net.revalorise.gowmod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class BismuthParticles extends TextureSheetParticle {
    protected BismuthParticles(
        ClientLevel level, double x, double y, double z,
        SpriteSet spriteSet, double xSpeed, double ySpeed, double zSpeed) {

        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.friction = 0.8F; // slow down
        this.lifetime = 200; // 10 seconds
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(
            SimpleParticleType type, ClientLevel level,
            double pX, double pY, double pZ,
            double xSpeed, double ySpeed, double zSpeed)
        {
            return new BismuthParticles(level, pX, pY, pZ, this.sprites, xSpeed, ySpeed, zSpeed);
        }
    }
}
