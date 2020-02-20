package si.bismuth.mixins;

import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockPumpkin.class)
public abstract class MixinBlockPumpkin {
	@Redirect(method = "canPlaceBlockAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/IBlockState;isTopSolid()Z"))
	private boolean onPlaceFenceGate(IBlockState state) {
		return true;
	}
}
