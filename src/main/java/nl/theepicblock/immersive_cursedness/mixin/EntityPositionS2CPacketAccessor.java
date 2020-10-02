package nl.theepicblock.immersive_cursedness.mixin;

import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPositionS2CPacket.class)
public interface EntityPositionS2CPacketAccessor {
    @SuppressWarnings("AccessorTarget")
    @Accessor
    void setX(double v);

    @SuppressWarnings("AccessorTarget")
    @Accessor
    void setY(double v);
}
