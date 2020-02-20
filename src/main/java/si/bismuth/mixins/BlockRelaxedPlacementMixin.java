package si.bismuth.mixins;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = {BlockFenceGate.class, BlockPumpkin.class})
public abstract class BlockRelaxedPlacementMixin {
	@Redirect(method = "canPlaceBlockAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/material/Material;isSolid()Z"))
	private boolean onPlaceFenceGate(Material material) {
		return true;
	}
}
