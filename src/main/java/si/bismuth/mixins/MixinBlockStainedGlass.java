package si.bismuth.mixins;

import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockStainedGlass.class)
public abstract class MixinBlockStainedGlass {
	@Inject(method = "getMapColor", at = @At("HEAD"), cancellable = true)
	private void remapStainedGlassMapColor(IBlockState state, IBlockAccess world, BlockPos pos, CallbackInfoReturnable<MapColor> cir) {
		cir.setReturnValue(MapColor.AIR);
	}
}
