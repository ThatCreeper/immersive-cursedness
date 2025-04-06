package nl.theepicblock.immersive_cursedness.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {
    public MixinPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(EntityType.PLAYER, world);
    }
}
