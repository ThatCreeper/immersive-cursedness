package nl.theepicblock.immersive_cursedness.mixin.interdimensionalpackets;

import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Clearable;
import nl.theepicblock.immersive_cursedness.PlayerInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public interface InventoryDistanceMixin extends Clearable {
	@Inject(method = "canPlayerUse(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/player/PlayerEntity;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;canInteractWithBlockAt(Lnet/minecraft/util/math/BlockPos;D)Z"), cancellable = true)
	private static void playerUseRedirect(BlockEntity blockEntity, PlayerEntity player, float range, CallbackInfoReturnable<Boolean> cir) {
		if (((PlayerInterface)player).immersivecursedness$getUnfakedWorld() != blockEntity.getWorld()) {
			if (player instanceof ServerPlayerEntity) {
				cir.setReturnValue(PlayerInterface.isCloseToPortal((ServerPlayerEntity)player));
			}
		}
	}
}
